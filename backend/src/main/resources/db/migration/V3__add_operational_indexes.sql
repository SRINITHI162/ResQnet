CREATE INDEX idx_incidents_status_priority ON incidents (status, priority_score);
CREATE INDEX idx_resources_status_type ON resources (status, resource_type);
CREATE INDEX idx_rescue_teams_availability ON rescue_teams (availability_status);
CREATE INDEX idx_timeline_incident_timestamp ON incident_timeline (incident_id, timestamp);
