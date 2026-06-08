from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from google import genai
from dotenv import load_dotenv
import os
import json
import re
from datetime import date, timedelta
from typing import Optional

load_dotenv(override=True)

# Múltiples keys — se prueban en orden hasta que uno funcione
_RAW_KEYS = [
    os.getenv("GEMINI_API_KEY"),
    os.getenv("GEMINI_API_KEY_2"),
    os.getenv("GEMINI_API_KEY_3"),
]
API_KEYS = [k for k in _RAW_KEYS if k and k.startswith("AIza")]

def generate_with_fallback(prompt: str) -> str:
    """Intenta generar contenido con cada key disponible hasta que una funcione."""
    last_error = None
    for key in API_KEYS:
        try:
            c = genai.Client(api_key=key)
            response = c.models.generate_content(model=MODEL, contents=prompt)
            return response.text
        except Exception as e:
            last_error = e
            continue
    raise last_error or Exception("No hay API keys disponibles")

app = FastAPI(title="Dexter AI Service")

DAY_NAMES_ES = {1: "Lunes", 2: "Martes", 3: "Miércoles", 4: "Jueves", 5: "Viernes", 6: "Sábado", 7: "Domingo"}
DAY_SHORT_ES = {1: "Lun", 2: "Mar", 3: "Mié", 4: "Jue", 5: "Vie", 6: "Sáb", 7: "Dom"}
MONTH_SHORT_ES = {1:"ene",2:"feb",3:"mar",4:"abr",5:"may",6:"jun",7:"jul",8:"ago",9:"sep",10:"oct",11:"nov",12:"dic"}


def next_dates_for_days(day_numbers: list[int], count: int) -> list[str]:
    """Devuelve las próximas `count` fechas que caigan en alguno de los días ISO dados (1=Lun..7=Dom)."""
    if not day_numbers:
        return []
    results = []
    today = date.today()
    cursor = today + timedelta(days=1)
    while len(results) < count:
        iso_wd = cursor.isoweekday()
        if iso_wd in day_numbers:
            short = f"{DAY_SHORT_ES.get(iso_wd, '')} {cursor.day} {MONTH_SHORT_ES.get(cursor.month, '')}"
            results.append(short)
        cursor += timedelta(days=1)
    return results


# ── Request / Response models ─────────────────────────────────────────────────

class PlanRequest(BaseModel):
    userAName: str
    userAOffers: list[str]
    userBName: str
    userBOffers: list[str]
    availableDays: list[str]          # nombres de días ("Lunes", etc.) — legacy
    overlapDayNumbers: list[int] = [] # días ISO (1-7) en que ambos coinciden
    timezone: Optional[str] = None    # ej. "America/Costa_Rica"


class SessionPlan(BaseModel):
    sessionNumber: int
    title: str
    description: str
    skill: str
    scheduledDate: Optional[str] = None  # ej. "Lun 9 jun"


class ScheduleSlot(BaseModel):
    day: str
    time: str


class ScheduleRequest(BaseModel):
    userAName: str
    userBName: str
    userAAvailability: list[dict]
    userBAvailability: list[dict]
    sessionTitle: str


# ── Helpers ───────────────────────────────────────────────────────────────────

def extract_json(text: str) -> str:
    """Extrae el JSON aunque Gemini lo envuelva en bloques ```json ... ```."""
    match = re.search(r"```(?:json)?\s*([\s\S]*?)```", text)
    if match:
        return match.group(1).strip()
    return text.strip()


# ── Endpoints ─────────────────────────────────────────────────────────────────

MODEL = "gemini-2.5-flash-lite"

@app.get("/health")
def health():
    return {"status": "ok", "model": MODEL}


@app.post("/generate-plan", response_model=list[SessionPlan])
def generate_plan(req: PlanRequest):
    all_a_skills = req.userAOffers
    all_b_skills = req.userBOffers

    # Calcular fechas sugeridas basadas en días de solapamiento
    suggested_dates = next_dates_for_days(req.overlapDayNumbers, 6) if req.overlapDayNumbers else []
    has_dates = len(suggested_dates) == 6

    # Asignación de skills por sesión
    session_skill_hints = []
    for i in range(1, 7):
        if i % 2 == 1:  # odd → B teaches
            skill = all_b_skills[(i // 2) % len(all_b_skills)] if all_b_skills else (all_a_skills[0] if all_a_skills else "habilidad")
            teacher = req.userBName
        else:  # even → A teaches
            skill = all_a_skills[(i // 2 - 1) % len(all_a_skills)] if all_a_skills else (all_b_skills[0] if all_b_skills else "habilidad")
            teacher = req.userAName
        date_hint = f", fecha sugerida: \"{suggested_dates[i-1]}\"" if has_dates else ""
        session_skill_hints.append(f"Sesión {i}: {teacher} enseña «{skill}» — skill=\"{skill}\"{date_hint}")

    hints_text = "\n".join(session_skill_hints)
    days_text = ", ".join(req.availableDays) if req.availableDays else ("días de solapamiento: " + ", ".join(DAY_NAMES_ES.get(d, str(d)) for d in req.overlapDayNumbers) if req.overlapDayNumbers else "horario flexible")
    tz_text = f"Zona horaria: {req.timezone}" if req.timezone else ""
    today_text = f"Fecha de hoy: {date.today().strftime('%d/%m/%Y')}"

    scheduled_field = '"scheduledDate": "fecha sugerida exactamente como aparece arriba",' if has_dates else '"scheduledDate": null,'

    # Detectar idioma dominante a partir de los nombres de habilidades
    all_skill_names = req.userAOffers + req.userBOffers + [req.userAName, req.userBName]
    lang_hint = "Responde en el mismo idioma que los nombres de habilidades proporcionados. Si las habilidades están en inglés, responde en inglés; si están en español, responde en español; si están en portugués, responde en portugués."

    prompt = f"""You are a learning plan generator for a skill-exchange app.
{lang_hint}

{today_text}
{tz_text}
Available days for both users: {days_text}

SKILLS (use EXACTLY these strings in the "skill" field — do NOT translate them):
- {req.userAName} teaches: {", ".join(f'"{s}"' for s in req.userAOffers)}
- {req.userBName} teaches: {", ".join(f'"{s}"' for s in req.userBOffers)}

MANDATORY SESSION ASSIGNMENT:
{hints_text}

RULES:
- "skill" MUST be exactly the string shown above, unchanged.
- "scheduledDate": use the suggested date if provided; otherwise null.
- "title": specific, motivating, related to that skill.
- "description": what will be practiced (1-2 sentences).
- Do NOT include time or duration (users set those later).
- Respond in the same language as the skill names.

Respond ONLY with a valid JSON array:
[
  {{
    "sessionNumber": 1,
    "title": "specific title",
    "description": "session description",
    "skill": "exact name as shown above",
    {scheduled_field}
  }}
]"""

    try:
        raw = extract_json(generate_with_fallback(prompt))
        plans = json.loads(raw)

        if not isinstance(plans, list) or len(plans) != 6:
            raise ValueError(f"Se esperaban 6 sesiones, se recibieron {len(plans) if isinstance(plans, list) else 0}")

        return plans

    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"Gemini devolvió JSON inválido: {e}")
    except ValueError as e:
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al generar plan: {str(e)}")


@app.post("/suggest-schedule", response_model=list[ScheduleSlot])
def suggest_schedule(req: ScheduleRequest):
    day_names = {1: "Lunes", 2: "Martes", 3: "Miércoles", 4: "Jueves", 5: "Viernes", 6: "Sábado", 7: "Domingo"}

    def format_availability(slots: list[dict]) -> str:
        lines = []
        for s in slots:
            d = day_names.get(s.get("dayOfWeek", 0), f"Día {s.get('dayOfWeek')}")
            lines.append(f"  - {d}: {s.get('startTime', '?')} a {s.get('endTime', '?')}")
        return "\n".join(lines) if lines else "  Sin disponibilidad registrada"

    avail_a = format_availability(req.userAAvailability)
    avail_b = format_availability(req.userBAvailability)

    prompt = f"""Eres un asistente de agenda para una app de intercambio de habilidades.

Necesitas sugerir los 3 mejores horarios para la sesión "{req.sessionTitle}" entre {req.userAName} y {req.userBName}.

DISPONIBILIDAD de {req.userAName}:
{avail_a}

DISPONIBILIDAD de {req.userBName}:
{avail_b}

REGLAS:
- Sugiere exactamente 3 horarios donde AMBOS estén disponibles.
- Si no hay intersección perfecta, sugiere los horarios más cercanos a la disponibilidad de ambos.
- Usa formato corto para el día: "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom".
- El campo "day" debe incluir día abreviado y una fecha próxima (ej: "Lun 9 Jun").
- El campo "time" debe ser un rango de 1.5 horas (ej: "18:00 - 19:30").

Responde ÚNICAMENTE con un JSON array válido:
[
  {{"day": "Lun 9 Jun", "time": "18:00 - 19:30"}}
]"""

    try:
        raw = extract_json(generate_with_fallback(prompt))
        slots = json.loads(raw)

        if not isinstance(slots, list) or len(slots) == 0:
            raise ValueError("No se generaron sugerencias de horario")

        return slots[:3]

    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"Gemini devolvió JSON inválido: {e}")
    except ValueError as e:
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al sugerir horarios: {str(e)}")
