/** Privilege **/
INSERT INTO public.privilege (id, name) VALUES (1, 'WRITE_PRIVILEGE');
INSERT INTO public.privilege (id, name) VALUES (2, 'READ_PRIVILEGE');

/** Role **/
INSERT INTO public.role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.role (id, name) VALUES (2, 'ROLE_USER');

/** Role-Privilege **/
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (1, 1);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (1, 2);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (2, 2);

/** Users **/
INSERT INTO public.users (id, email, password, first_name, last_name, username, bio, avatar_url, country, enabled, token_expired, rating, total_sessions, total_matches, create_date, updated_at)
VALUES (1, 'admin@dexter.app', '$2a$10$6hg/QTw8Th1EmYtg9/5HhOmRdg320A6J8DumNUqx.4AltXZAjp0VK', 'Admin', 'Dexter', 'admin', 'Guitar teacher', null, 'CR', true, false, 4.8, 5, 3, NOW(), NOW());
INSERT INTO public.users (id, email, password, first_name, last_name, username, bio, avatar_url, country, enabled, token_expired, rating, total_sessions, total_matches, create_date, updated_at)
VALUES (2, 'user@dexter.app',  '$2a$10$6hg/QTw8Th1EmYtg9/5HhOmRdg320A6J8DumNUqx.4AltXZAjp0VK', 'Regular', 'User', 'user', 'English teacher', null, 'CR', true, false, 4.5, 3, 2, NOW(), NOW());

/** User-Role **/
INSERT INTO public.user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO public.user_role (user_id, role_id) VALUES (2, 2);

/** Skill **/
INSERT INTO public.skill (id, user_id, name, category, skill_role, skill_level) VALUES (1, 1, 'Guitar',  'Music',     'OFFERED', 'ADVANCED');
INSERT INTO public.skill (id, user_id, name, category, skill_role, skill_level) VALUES (2, 1, 'English', 'Languages', 'SOUGHT',  'INTERMEDIATE');
INSERT INTO public.skill (id, user_id, name, category, skill_role, skill_level) VALUES (3, 2, 'English', 'Languages', 'OFFERED', 'ADVANCED');
INSERT INTO public.skill (id, user_id, name, category, skill_role, skill_level) VALUES (4, 2, 'Guitar',  'Music',     'SOUGHT',  'BEGINNER');

/** Match **/
INSERT INTO public.matches (id, initiator_id, receiver_id, compatibility_score, status, created_at)
VALUES (1, 1, 2, 92.0, 'ACCEPTED', NOW());

/** Session **/
INSERT INTO public.session (id, match_id, skill_id, session_number, title, description, status, scheduled_at, completed_at, created_at)
VALUES (1, 1, 1, 1, 'Guitar Basics', 'Introduction to guitar chords', 'COMPLETED', NOW(), NOW(), NOW());

/** Review **/
INSERT INTO public.review (id, reviewer_id, reviewee_id, session_id, rating, comment, created_at)
VALUES (1, 2, 1, 1, 5, 'Excellent teacher, very patient!', NOW());

/** Reiniciar secuencias (necesario porque Hibernate no sabe los IDs ya usados) **/
ALTER SEQUENCE privilege_seq RESTART WITH 3;
ALTER SEQUENCE role_seq RESTART WITH 3;
ALTER SEQUENCE users_seq RESTART WITH 3;
ALTER SEQUENCE skill_seq RESTART WITH 5;
ALTER SEQUENCE matches_seq RESTART WITH 2;
ALTER SEQUENCE session_seq RESTART WITH 2;
ALTER SEQUENCE review_seq RESTART WITH 2;
