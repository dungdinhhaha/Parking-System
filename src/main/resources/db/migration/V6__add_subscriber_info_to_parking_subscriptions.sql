ALTER TABLE parking_subscriptions
    ADD COLUMN IF NOT EXISTS subscriber_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS subscriber_phone VARCHAR(50);
