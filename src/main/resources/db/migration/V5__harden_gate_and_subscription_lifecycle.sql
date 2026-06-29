ALTER TABLE parking_sessions
    ADD COLUMN IF NOT EXISTS check_in_request_id VARCHAR(100),
    ADD COLUMN IF NOT EXISTS check_out_request_id VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_sessions_check_in_request_id
    ON parking_sessions (check_in_request_id)
    WHERE check_in_request_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_sessions_check_out_request_id
    ON parking_sessions (check_out_request_id)
    WHERE check_out_request_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_sessions_active_plate
    ON parking_sessions (upper(trim(plate_number)))
    WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_sessions_active_rfid
    ON parking_sessions (upper(trim(rfid_card_id)))
    WHERE status = 'ACTIVE' AND rfid_card_id IS NOT NULL AND trim(rfid_card_id) <> '';

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_subscriptions_active_rfid
    ON parking_subscriptions (upper(trim(rfid_card_id)))
    WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX IF NOT EXISTS uk_parking_subscriptions_active_vehicle
    ON parking_subscriptions (vehicle_id)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_reservations_status_end_time
    ON reservations (status, end_time);

CREATE INDEX IF NOT EXISTS idx_parking_subscriptions_status_end_at
    ON parking_subscriptions (status, end_at);

CREATE TABLE IF NOT EXISTS parking_subscription_history (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    subscription_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_rfid_card_id VARCHAR(100),
    new_rfid_card_id VARCHAR(100),
    old_vehicle_id BIGINT,
    new_vehicle_id BIGINT,
    old_zone_id BIGINT,
    new_zone_id BIGINT,
    old_slot_id BIGINT,
    new_slot_id BIGINT,
    changed_by_user_id BIGINT,
    notes VARCHAR(500),
    CONSTRAINT fk_subscription_history_subscription
        FOREIGN KEY (subscription_id) REFERENCES parking_subscriptions(id),
    CONSTRAINT fk_subscription_history_changed_by
        FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_subscription_history_subscription_created
    ON parking_subscription_history (subscription_id, created_at DESC);
