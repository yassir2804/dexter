from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from google import genai
from google.genai import types
from dotenv import load_dotenv
import os
import json
import re

load_dotenv()

client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))

app = FastAPI(title="Dexter AI Service")


# ── Request / Response models ─────────────────────────────────────────────────

class PlanRequest(BaseModel):
    userAName: str
    userAOffers: list[str]
    userBName: str
    userBOffers: list[str]
    availableDays: list[str]


class SessionPlan(BaseModel):
    sessionNumber: int
    title: str
    description: str
    skill: str


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
    days_text = ", ".join(req.availableDays) if req.availableDays else "horario flexible"

    all_a_skills = req.userAOffers
    all_b_skills = req.userBOffers

    # Assign skills round-robin for each session
    session_skill_hints = []
    for i in range(1, 7):
        if i % 2 == 1:  # odd → B teaches
            skill = all_b_skills[(i // 2) % len(all_b_skills)] if all_b_skills else (all_a_skills[0] if all_a_skills else "habilidad")
            teacher = req.userBName
        else:  # even → A teaches
            skill = all_a_skills[(i // 2 - 1) % len(all_a_skills)] if all_a_skills else (all_b_skills[0] if all_b_skills else "habilidad")
            teacher = req.userAName
        session_skill_hints.append(f"Sesión {i}: {teacher} enseña «{skill}» — skill debe ser exactamente \"{skill}\"")

    hints_text = "\n".join(session_skill_hints)

    prompt = f"""Eres un generador de planes de aprendizaje para una app de intercambio de habilidades.

HABILIDADES DISPONIBLES (usa EXACTAMENTE estos nombres en el campo "skill"):
- {req.userAName} puede enseñar: {", ".join(f'"{s}"' for s in req.userAOffers)}
- {req.userBName} puede enseñar: {", ".join(f'"{s}"' for s in req.userBOffers)}
Días disponibles: {days_text}

ASIGNACIÓN OBLIGATORIA (NO cambies el skill asignado a cada sesión):
{hints_text}

REGLAS:
- El campo "skill" de cada sesión DEBE ser exactamente el string indicado arriba, sin traducir ni modificar.
- El título debe ser específico, motivador y relacionado con esa habilidad concreta.
- La descripción explica qué se practicará (1-2 oraciones).

Responde ÚNICAMENTE con un JSON array válido, sin texto ni markdown adicional:
[
  {{
    "sessionNumber": 1,
    "title": "título concreto",
    "description": "descripción de la sesión",
    "skill": "nombre exacto tal como aparece arriba"
  }}
]"""

    try:
        response = client.models.generate_content(model=MODEL, contents=prompt)
        raw = extract_json(response.text)
        plans = json.loads(raw)

        if not isinstance(plans, list) or len(plans) != 6:
            raise ValueError(f"Se esperaban 6 sesiones, se recibieron {len(plans) if isinstance(plans, list) else 0}")

        return plans

    except json.JSONDecodeError as e:
        raise HTTPException(
            status_code=500,
            detail=f"Gemini devolvió JSON inválido: {e}"
        )
    except ValueError as e:
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al generar plan: {str(e)}")
