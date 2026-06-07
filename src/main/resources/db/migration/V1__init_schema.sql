CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    full_name VARCHAR(255),
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    role VARCHAR(50),
    status VARCHAR(50),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    plate_number VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    color VARCHAR(100),
    brand VARCHAR(100),
    owner_id BIGINT,
    CONSTRAINT uk_vehicles_plate_number UNIQUE (plate_number),
    CONSTRAINT fk_vehicles_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE parking_buildings (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    open_time TIME,
    close_time TIME,
    status VARCHAR(50),
    description VARCHAR(1000)
);

CREATE TABLE parking_floors (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    building_id BIGINT NOT NULL,
    floor_code VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    description VARCHAR(1000),
    CONSTRAINT uk_parking_floors_building_floor_code UNIQUE (building_id, floor_code),
    CONSTRAINT fk_parking_floors_building FOREIGN KEY (building_id) REFERENCES parking_buildings (id)
);

CREATE TABLE parking_zones (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    floor_id BIGINT NOT NULL,
    zone_code VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    vehicle_type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    current_count INTEGER NOT NULL,
    reserved_count INTEGER NOT NULL,
    status VARCHAR(50),
    CONSTRAINT uk_parking_zones_floor_zone_code UNIQUE (floor_id, zone_code),
    CONSTRAINT fk_parking_zones_floor FOREIGN KEY (floor_id) REFERENCES parking_floors (id)
);

CREATE TABLE parking_slots (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    zone_id BIGINT NOT NULL,
    slot_code VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    status VARCHAR(50),
    distance_from_gate DOUBLE PRECISION,
    CONSTRAINT uk_parking_slots_zone_slot_code UNIQUE (zone_id, slot_code),
    CONSTRAINT fk_parking_slots_zone FOREIGN KEY (zone_id) REFERENCES parking_zones (id)
);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    reservation_code VARCHAR(50) NOT NULL,
    plate_number VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50),
    used_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    assigned_zone_id BIGINT NOT NULL,
    assigned_slot_id BIGINT,
    CONSTRAINT uk_reservations_reservation_code UNIQUE (reservation_code),
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reservations_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT fk_reservations_zone FOREIGN KEY (assigned_zone_id) REFERENCES parking_zones (id),
    CONSTRAINT fk_reservations_slot FOREIGN KEY (assigned_slot_id) REFERENCES parking_slots (id)
);

CREATE TABLE parking_sessions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    ticket_code VARCHAR(50) NOT NULL,
    plate_number VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    entry_gate VARCHAR(100),
    exit_gate VARCHAR(100),
    status VARCHAR(50),
    vehicle_id BIGINT,
    assigned_zone_id BIGINT NOT NULL,
    assigned_slot_id BIGINT,
    reservation_id BIGINT UNIQUE,
    CONSTRAINT uk_parking_sessions_ticket_code UNIQUE (ticket_code),
    CONSTRAINT fk_parking_sessions_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT fk_parking_sessions_zone FOREIGN KEY (assigned_zone_id) REFERENCES parking_zones (id),
    CONSTRAINT fk_parking_sessions_slot FOREIGN KEY (assigned_slot_id) REFERENCES parking_slots (id),
    CONSTRAINT fk_parking_sessions_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    method VARCHAR(50),
    status VARCHAR(50),
    paid_at TIMESTAMP,
    transaction_code VARCHAR(100),
    parking_session_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_payments_session FOREIGN KEY (parking_session_id) REFERENCES parking_sessions (id)
);

CREATE TABLE pricing_policies (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    vehicle_type VARCHAR(50),
    policy_type VARCHAR(50),
    base_fee NUMERIC(19,2),
    hourly_rate NUMERIC(19,2),
    lost_ticket_fee NUMERIC(19,2),
    overnight_fee NUMERIC(19,2),
    wrong_zone_penalty_fee NUMERIC(19,2),
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    status VARCHAR(50)
);

CREATE TABLE plate_recognition_logs (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    image_url VARCHAR(500),
    original_file_name VARCHAR(255),
    detected_plate_number VARCHAR(30),
    confidence DOUBLE PRECISION,
    provider VARCHAR(100),
    recognition_type VARCHAR(50),
    status VARCHAR(50),
    confirmed_plate_number VARCHAR(30),
    is_confirmed BOOLEAN DEFAULT FALSE,
    parking_session_id BIGINT,
    vehicle_id BIGINT,
    uploaded_by_id BIGINT,
    CONSTRAINT fk_plate_logs_session FOREIGN KEY (parking_session_id) REFERENCES parking_sessions (id),
    CONSTRAINT fk_plate_logs_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT fk_plate_logs_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES users (id)
);

CREATE TABLE parking_incidents (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    incident_type VARCHAR(50),
    description VARCHAR(1000),
    status VARCHAR(50),
    resolved_at TIMESTAMP,
    resolution_note VARCHAR(1000),
    parking_session_id BIGINT,
    reported_by_id BIGINT,
    resolved_by_id BIGINT,
    plate_recognition_log_id BIGINT,
    CONSTRAINT fk_incidents_session FOREIGN KEY (parking_session_id) REFERENCES parking_sessions (id),
    CONSTRAINT fk_incidents_reported_by FOREIGN KEY (reported_by_id) REFERENCES users (id),
    CONSTRAINT fk_incidents_resolved_by FOREIGN KEY (resolved_by_id) REFERENCES users (id),
    CONSTRAINT fk_incidents_plate_log FOREIGN KEY (plate_recognition_log_id) REFERENCES plate_recognition_logs (id)
);

CREATE TABLE feedbacks (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    content VARCHAR(2000),
    rating INTEGER,
    status VARCHAR(50),
    user_id BIGINT NOT NULL,
    parking_session_id BIGINT,
    CONSTRAINT fk_feedbacks_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_feedbacks_session FOREIGN KEY (parking_session_id) REFERENCES parking_sessions (id)
);

CREATE INDEX idx_parking_floors_building_id ON parking_floors (building_id);
CREATE INDEX idx_parking_zones_floor_id ON parking_zones (floor_id);
CREATE INDEX idx_parking_slots_zone_id ON parking_slots (zone_id);
CREATE INDEX idx_reservations_user_id ON reservations (user_id);
CREATE INDEX idx_parking_sessions_zone_id ON parking_sessions (assigned_zone_id);
CREATE INDEX idx_parking_sessions_vehicle_id ON parking_sessions (vehicle_id);
CREATE INDEX idx_plate_logs_session_id ON plate_recognition_logs (parking_session_id);
CREATE INDEX idx_feedbacks_user_id ON feedbacks (user_id);
