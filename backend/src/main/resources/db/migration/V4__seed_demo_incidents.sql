INSERT INTO incidents (
    disaster_type, title, description, latitude, longitude, location_name,
    severity, affected_people, injured_people, vulnerable_people, required_resources,
    status, priority_level, priority_score, reported_by, verified_by,
    reported_at, verified_at, resolved_at
)
SELECT
    'FLOOD', 'Flooding near Karur riverbank',
    'Rising water has affected homes near the riverbank. Residents need evacuation and medical support.',
    10.9450, 78.0900, 'Karur Riverbank',
    9, 180, 12, 30, 'BOAT,AMBULANCE,MEDICAL_TEAM',
    'VERIFIED', 'CRITICAL', 92.5, reporter.id, verifier.id,
    NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 90 MINUTE, NULL
FROM users reporter
JOIN users verifier ON verifier.email = 'ravi.coordinator@resqnet.in'
WHERE reporter.email = 'arjun@example.com'
  AND NOT EXISTS (
      SELECT 1 FROM incidents WHERE title = 'Flooding near Karur riverbank'
  );

INSERT INTO incidents (
    disaster_type, title, description, latitude, longitude, location_name,
    severity, affected_people, injured_people, vulnerable_people, required_resources,
    status, priority_level, priority_score, reported_by, verified_by,
    reported_at, verified_at, resolved_at
)
SELECT
    'FIRE', 'Warehouse fire in Manmangalam',
    'A warehouse fire is producing heavy smoke. Nearby residents have been moved to a safe area.',
    10.9167, 78.1167, 'Manmangalam',
    8, 45, 8, 5, 'FIRE_TRUCK,AMBULANCE,MEDICAL_TEAM',
    'RESOURCE_ASSIGNED', 'HIGH', 76.0, reporter.id, verifier.id,
    NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 150 MINUTE, NULL
FROM users reporter
JOIN users verifier ON verifier.email = 'ravi.coordinator@resqnet.in'
WHERE reporter.email = 'lakshmi@example.com'
  AND NOT EXISTS (
      SELECT 1 FROM incidents WHERE title = 'Warehouse fire in Manmangalam'
  );

INSERT INTO incidents (
    disaster_type, title, description, latitude, longitude, location_name,
    severity, affected_people, injured_people, vulnerable_people, required_resources,
    status, priority_level, priority_score, reported_by, verified_by,
    reported_at, verified_at, resolved_at
)
SELECT
    'ACCIDENT', 'Road accident near Kulithalai',
    'A multi-vehicle collision has blocked one lane. Several people have reported minor injuries.',
    10.9333, 78.4167, 'Kulithalai',
    5, 12, 4, 2, 'AMBULANCE,RESCUE_VEHICLE',
    'REPORTED', 'MEDIUM', 54.0, reporter.id, NULL,
    NOW() - INTERVAL 20 MINUTE, NULL, NULL
FROM users reporter
WHERE reporter.email = 'karthik@example.com'
  AND NOT EXISTS (
      SELECT 1 FROM incidents WHERE title = 'Road accident near Kulithalai'
  );

INSERT INTO incidents (
    disaster_type, title, description, latitude, longitude, location_name,
    severity, affected_people, injured_people, vulnerable_people, required_resources,
    status, priority_level, priority_score, reported_by, verified_by,
    reported_at, verified_at, resolved_at
)
SELECT
    'STORM', 'Storm damage in Aravakurichi',
    'A fallen tree damaged a home. The occupants have been assisted and the area is safe.',
    10.7833, 78.0000, 'Aravakurichi',
    3, 8, 0, 2, 'RESCUE_VEHICLE',
    'RESOLVED', 'LOW', 24.0, reporter.id, verifier.id,
    NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 22 HOUR
FROM users reporter
JOIN users verifier ON verifier.email = 'ravi.coordinator@resqnet.in'
WHERE reporter.email = 'divya@example.com'
  AND NOT EXISTS (
      SELECT 1 FROM incidents WHERE title = 'Storm damage in Aravakurichi'
  );

INSERT INTO incident_timeline (incident_id, event, timestamp)
SELECT i.id, timeline.event, timeline.event_time
FROM (
    SELECT 'Flooding near Karur riverbank' AS incident_title,
           'Incident reported' AS event, NOW() - INTERVAL 2 HOUR AS event_time
    UNION ALL
    SELECT 'Flooding near Karur riverbank', 'Incident verified', NOW() - INTERVAL 90 MINUTE
    UNION ALL
    SELECT 'Warehouse fire in Manmangalam', 'Incident reported', NOW() - INTERVAL 3 HOUR
    UNION ALL
    SELECT 'Warehouse fire in Manmangalam', 'Incident verified', NOW() - INTERVAL 150 MINUTE
    UNION ALL
    SELECT 'Warehouse fire in Manmangalam', 'Resources assigned', NOW() - INTERVAL 2 HOUR
    UNION ALL
    SELECT 'Road accident near Kulithalai', 'Incident reported', NOW() - INTERVAL 20 MINUTE
    UNION ALL
    SELECT 'Storm damage in Aravakurichi', 'Incident reported', NOW() - INTERVAL 1 DAY
    UNION ALL
    SELECT 'Storm damage in Aravakurichi', 'Incident verified', NOW() - INTERVAL 23 HOUR
    UNION ALL
    SELECT 'Storm damage in Aravakurichi', 'Status changed to RESOLVED', NOW() - INTERVAL 22 HOUR
) timeline
JOIN incidents i ON i.title = timeline.incident_title
WHERE NOT EXISTS (
    SELECT 1
    FROM incident_timeline existing
    WHERE existing.incident_id = i.id
      AND existing.event = timeline.event
);

INSERT INTO notifications (title, message, severity, incident_id, read_status, created_at)
SELECT
    'CRITICAL INCIDENT',
    'FLOOD reported at Karur Riverbank. 180 people affected.',
    'CRITICAL',
    i.id,
    b'0',
    NOW() - INTERVAL 90 MINUTE
FROM incidents i
WHERE i.title = 'Flooding near Karur riverbank'
  AND NOT EXISTS (
      SELECT 1
      FROM notifications n
      WHERE n.incident_id = i.id
        AND n.title = 'CRITICAL INCIDENT'
  );
