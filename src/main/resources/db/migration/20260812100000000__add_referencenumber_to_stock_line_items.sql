ALTER TABLE stock_card_line_items
    ADD COLUMN IF NOT EXISTS referencenumber VARCHAR(255);

ALTER TABLE stock_event_line_items
    ADD COLUMN IF NOT EXISTS referencenumber VARCHAR(255);

CREATE INDEX IF NOT EXISTS stock_card_line_items_referencenumber_idx
    ON stock_card_line_items (referencenumber)
    WHERE referencenumber IS NOT NULL;