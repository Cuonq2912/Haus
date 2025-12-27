ALTER TABLE order_items ADD COLUMN snapshot_product_code VARCHAR(255) NOT NULL;
ALTER TABLE order_items ADD COLUMN snapshot_product_name VARCHAR(255) NOT NULL;
ALTER TABLE order_items ADD COLUMN snapshot_description TEXT;
ALTER TABLE order_items ADD COLUMN snapshot_material VARCHAR(100);
ALTER TABLE order_items ADD COLUMN snapshot_color VARCHAR(255) NOT NULL;
ALTER TABLE order_items ADD COLUMN snapshot_size VARCHAR(255) NOT NULL;
ALTER TABLE order_items ADD COLUMN snapshot_image_url VARCHAR(500);

ALTER TABLE order_items MODIFY COLUMN product_variant_id BIGINT NULL;

UPDATE order_items oi
INNER JOIN product_variations pv ON oi.product_variant_id = pv.id
INNER JOIN products p ON pv.product_id = p.id
SET 
    oi.snapshot_product_code = p.product_code,
    oi.snapshot_product_name = p.product_name,
    oi.snapshot_description = p.description,
    oi.snapshot_material = p.material,
    oi.snapshot_color = pv.color,
    oi.snapshot_size = pv.size,
    oi.snapshot_image_url = (
        SELECT m.url 
        FROM medias m 
        WHERE m.product_variation_id = pv.id 
        LIMIT 1
    )
WHERE oi.snapshot_product_code IS NULL;
