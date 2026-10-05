USE event_management;

-- Demo password for every account below: password
-- The value is a BCrypt hash, never a plain-text password.
INSERT INTO users (name, email, password_hash, phone, role, status)
VALUES
    ('Avery Morgan', 'admin@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000001', 'ADMIN', 'ACTIVE'),
    ('Marcus Vance', 'organizer@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000002', 'ORGANIZER', 'ACTIVE'),
    ('Priya Shah', 'priya@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000003', 'ORGANIZER', 'ACTIVE'),
    ('Jordan Lee', 'jordan@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000004', 'ATTENDEE', 'ACTIVE'),
    ('Noah Williams', 'noah@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000005', 'ATTENDEE', 'ACTIVE'),
    ('Meera Patel', 'meera@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000006', 'ATTENDEE', 'ACTIVE'),
    ('Liam Brown', 'liam@eventflow.local', '$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2', '9000000007', 'ATTENDEE', 'ACTIVE')
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO system_settings (setting_key, setting_value)
VALUES
    ('platform_name', 'EventFlow'),
    ('support_email', 'support@eventflow.local'),
    ('default_currency', 'USD'),
    ('registration_enabled', 'true'),
    ('max_tickets_per_attendee', '6'),
    ('event_approval_required', 'true')
ON DUPLICATE KEY UPDATE setting_key = VALUES(setting_key);

INSERT INTO events (organizer_id, title, description, category, event_date, start_time, end_time, venue, capacity, banner_url, status)
SELECT u.id, 'Global FinTech Summit', 'A focused gathering for builders, investors, and operators shaping the future of financial technology.', 'Technology', DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), '09:00:00', '17:30:00', 'Indigo Convention Center', 800, 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1200&q=80', 'APPROVED'
FROM users u WHERE u.email = 'organizer@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM events WHERE title = 'Global FinTech Summit');

INSERT INTO events (organizer_id, title, description, category, event_date, start_time, end_time, venue, capacity, banner_url, status)
SELECT u.id, 'Design Systems Assembly', 'A practical workshop on creating accessible, scalable design systems for product teams.', 'Design', DATE_ADD(CURRENT_DATE, INTERVAL 48 DAY), '10:00:00', '16:00:00', 'North Hall Studio', 240, 'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1200&q=80', 'APPROVED'
FROM users u WHERE u.email = 'organizer@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM events WHERE title = 'Design Systems Assembly');

INSERT INTO events (organizer_id, title, description, category, event_date, start_time, end_time, venue, capacity, status)
SELECT u.id, 'Climate Tech Demo Day', 'Early-stage teams present practical climate solutions to a room of operators and partners.', 'Sustainability', DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY), '13:00:00', '18:00:00', 'Harbor Innovation Lab', 300, 'PENDING_APPROVAL'
FROM users u WHERE u.email = 'priya@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM events WHERE title = 'Climate Tech Demo Day');

INSERT INTO events (organizer_id, title, description, category, event_date, start_time, end_time, venue, capacity, status)
SELECT u.id, 'Private Organizer Draft', 'A draft event used to demonstrate the organizer workflow.', 'Community', DATE_ADD(CURRENT_DATE, INTERVAL 75 DAY), '11:00:00', '14:00:00', 'TBD', 100, 'DRAFT'
FROM users u WHERE u.email = 'priya@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM events WHERE title = 'Private Organizer Draft');

INSERT INTO tickets (event_id, name, description, price, quantity, sales_start_at, sales_end_at, status)
SELECT e.id, 'General Admission', 'Full-day access to the main event.', 49.00, 600, NOW(), DATE_ADD(NOW(), INTERVAL 25 DAY), 'ACTIVE'
FROM events e WHERE e.title = 'Global FinTech Summit'
AND NOT EXISTS (SELECT 1 FROM tickets WHERE event_id = e.id AND name = 'General Admission');

INSERT INTO tickets (event_id, name, description, price, quantity, sales_start_at, sales_end_at, status)
SELECT e.id, 'VIP Pass', 'Priority seating and speaker lounge access.', 129.00, 200, NOW(), DATE_ADD(NOW(), INTERVAL 25 DAY), 'ACTIVE'
FROM events e WHERE e.title = 'Global FinTech Summit'
AND NOT EXISTS (SELECT 1 FROM tickets WHERE event_id = e.id AND name = 'VIP Pass');

INSERT INTO tickets (event_id, name, description, price, quantity, sales_start_at, sales_end_at, status)
SELECT e.id, 'Workshop Pass', 'Includes all workshop sessions and materials.', 35.00, 240, NOW(), DATE_ADD(NOW(), INTERVAL 43 DAY), 'ACTIVE'
FROM events e WHERE e.title = 'Design Systems Assembly'
AND NOT EXISTS (SELECT 1 FROM tickets WHERE event_id = e.id AND name = 'Workshop Pass');

INSERT INTO registrations (event_id, attendee_id, status)
SELECT e.id, u.id, 'REGISTERED'
FROM events e JOIN users u ON u.email = 'jordan@eventflow.local'
WHERE e.title = 'Global FinTech Summit'
AND NOT EXISTS (SELECT 1 FROM registrations r WHERE r.event_id=e.id AND r.attendee_id=u.id);

INSERT INTO ticket_orders (attendee_id, event_id, ticket_id, quantity, unit_price, total_amount, payment_status, reference_code)
SELECT u.id, e.id, t.id, 1, t.price, t.price, 'PAID', 'EVT-DEMO-000001'
FROM users u JOIN events e ON e.title='Global FinTech Summit' JOIN tickets t ON t.event_id=e.id AND t.name='General Admission'
WHERE u.email='jordan@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM ticket_orders o WHERE o.reference_code='EVT-DEMO-000001');

UPDATE tickets t JOIN events e ON e.id=t.event_id
SET t.sold_quantity=1
WHERE e.title='Global FinTech Summit' AND t.name='General Admission' AND t.sold_quantity=0;

INSERT INTO event_messages (event_id, organizer_id, subject, message)
SELECT e.id, u.id, 'Welcome to the summit', 'Your event pass is confirmed. We will share the final schedule here shortly.'
FROM events e JOIN users u ON u.email='organizer@eventflow.local'
WHERE e.title='Global FinTech Summit'
AND NOT EXISTS (SELECT 1 FROM event_messages m WHERE m.event_id=e.id AND m.subject='Welcome to the summit');

INSERT INTO activity_logs (actor_user_id, action_type, entity_type, entity_id, description)
SELECT u.id, 'SEED_DATA_READY', 'SYSTEM', NULL, 'Demo data loaded for the EventFlow presentation flow.'
FROM users u WHERE u.email='admin@eventflow.local'
AND NOT EXISTS (SELECT 1 FROM activity_logs WHERE action_type='SEED_DATA_READY');

