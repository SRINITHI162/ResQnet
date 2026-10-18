CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    active BIT(1) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE incidents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    disaster_type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    location_name VARCHAR(255),
    severity INT,
    affected_people INT,
    injured_people INT,
    vulnerable_people INT,
    required_resources VARCHAR(1000),
    status VARCHAR(255),
    priority_level VARCHAR(255),
    priority_score DOUBLE,
    image_url VARCHAR(255),
    reported_by BIGINT,
    verified_by BIGINT,
    reported_at DATETIME(6),
    verified_at DATETIME(6),
    resolved_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_incidents_reported_by FOREIGN KEY (reported_by) REFERENCES users (id),
    CONSTRAINT fk_incidents_verified_by FOREIGN KEY (verified_by) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE resources (
    id BIGINT NOT NULL AUTO_INCREMENT,
    resource_name VARCHAR(255) NOT NULL,
    resource_type VARCHAR(255) NOT NULL,
    quantity INT,
    available_quantity INT,
    location VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    status VARCHAR(255),
    assigned_incident_id BIGINT,
    last_updated DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_resources_assigned_incident FOREIGN KEY (assigned_incident_id) REFERENCES incidents (id)
) ENGINE=InnoDB;

CREATE TABLE rescue_teams (
    id BIGINT NOT NULL AUTO_INCREMENT,
    team_name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255),
    members_count INT,
    location VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    availability_status VARCHAR(255),
    contact_number VARCHAR(255),
    assigned_incident_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_rescue_teams_assigned_incident FOREIGN KEY (assigned_incident_id) REFERENCES incidents (id)
) ENGINE=InnoDB;

CREATE TABLE shelters (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    total_capacity INT,
    occupied_capacity INT,
    food_available BIT(1),
    water_available BIT(1),
    medical_support BIT(1),
    accessibility_support BIT(1),
    status VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE roads (
    id BIGINT NOT NULL AUTO_INCREMENT,
    source VARCHAR(255) NOT NULL,
    source_lat DOUBLE,
    source_lng DOUBLE,
    destination VARCHAR(255) NOT NULL,
    dest_lat DOUBLE,
    dest_lng DOUBLE,
    distance_km DOUBLE,
    estimated_time_min DOUBLE,
    traffic_level INT,
    road_condition VARCHAR(255),
    flood_level INT,
    blocked BIT(1),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE incident_timeline (
    id BIGINT NOT NULL AUTO_INCREMENT,
    incident_id BIGINT NOT NULL,
    event VARCHAR(255) NOT NULL,
    timestamp DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_timeline_incident FOREIGN KEY (incident_id) REFERENCES incidents (id)
) ENGINE=InnoDB;

CREATE TABLE resource_allocations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    incident_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    allocation_score DOUBLE,
    distance_km DOUBLE,
    estimated_travel_time_min DOUBLE,
    allocated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_allocations_incident FOREIGN KEY (incident_id) REFERENCES incidents (id),
    CONSTRAINT fk_allocations_resource FOREIGN KEY (resource_id) REFERENCES resources (id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000),
    severity VARCHAR(255),
    incident_id BIGINT,
    read_status BIT(1) NOT NULL DEFAULT b'0',
    created_at DATETIME(6),
    PRIMARY KEY (id),
    KEY idx_notifications_created_at (created_at),
    CONSTRAINT fk_notifications_incident FOREIGN KEY (incident_id) REFERENCES incidents (id)
) ENGINE=InnoDB;
