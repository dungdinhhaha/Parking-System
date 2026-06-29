CREATE TABLE IF NOT EXISTS parking_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    subscription_code VARCHAR(50) NOT NULL,
    rfid_card_id VARCHAR(100) NOT NULL,
    subscription_type VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    notes VARCHAR(255),
    vehicle_id BIGINT NOT NULL,
    assigned_zone_id BIGINT,
    assigned_slot_id BIGINT,
    created_by_user_id BIGINT,
    CONSTRAINT uk_parking_subscriptions_subscription_code UNIQUE (subscription_code),
    CONSTRAINT fk_parking_subscriptions_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT fk_parking_subscriptions_zone FOREIGN KEY (assigned_zone_id) REFERENCES parking_zones(id),
    CONSTRAINT fk_parking_subscriptions_slot FOREIGN KEY (assigned_slot_id) REFERENCES parking_slots(id),
    CONSTRAINT fk_parking_subscriptions_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

ALTER TABLE parking_sessions
    ADD COLUMN IF NOT EXISTS subscription_id BIGINT;

ALTER TABLE parking_sessions
    ADD CONSTRAINT fk_parking_sessions_subscription
    FOREIGN KEY (subscription_id) REFERENCES parking_subscriptions(id);

CREATE INDEX IF NOT EXISTS idx_parking_subscriptions_rfid_card_id_status
    ON parking_subscriptions (rfid_card_id, status);

CREATE INDEX IF NOT EXISTS idx_parking_subscriptions_vehicle_id_status
    ON parking_subscriptions (vehicle_id, status);
