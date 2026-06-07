ALTER TABLE parking_buildings
    ADD COLUMN IF NOT EXISTS building_code VARCHAR(50);

WITH ordered AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM parking_buildings
)
UPDATE parking_buildings pb
SET building_code = 'BLD-' || LPAD(ordered.rn::text, 3, '0')
FROM ordered
WHERE pb.id = ordered.id
  AND pb.building_code IS NULL;

ALTER TABLE parking_buildings
    ALTER COLUMN building_code SET NOT NULL;

ALTER TABLE parking_buildings
    ADD CONSTRAINT uk_parking_buildings_building_code UNIQUE (building_code);
