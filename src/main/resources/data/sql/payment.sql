USE haus;

INSERT INTO orders (
    id,
    created_at,
    updated_at,
    order_date,
    order_number,
    shipping_fee,
    status,
    total_amount,
    user_id
) VALUES (
    2,                       -- hoặc tự sinh UUID cho order
    NOW(),                        -- created_at
    NOW(),                        -- updated_at
    CURDATE(),                        -- order_date
    'ORD-20251003-0002',          -- order_number
    30000,                        -- shipping_fee
    'PENDING',                    -- status
    600000,                       -- total_amount
    '36f38f38-6fd6-4b96-8d44-04b0c3aba061' -- user_id (tồn tại trong bảng users)
);

DELETE FROM orders WHERE id = 2;
DELETE FROM payments WHERE id = '1';

SHOW CREATE TABLE orders;


INSERT INTO payments (
    id,
    created_at,
    updated_at,
    amount,
    gateway,
    type,
    status,
    expire_at,
    order_id
) VALUES (
    1,                       -- id dạng UUID
    NOW(),                        -- created_at
    NOW(),                        -- updated_at
    500000,                       -- amount
    'VNPAY',
    'BANK_TRANSFER',
    'PENDING',
    DATE_ADD(NOW(), INTERVAL 20 MINUTE), -- expire_at
    2                             -- order_id (ID của order đã tạo)
);