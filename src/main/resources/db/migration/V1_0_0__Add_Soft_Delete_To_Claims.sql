-- Add soft delete columns to claims table
ALTER TABLE claims ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE claims ADD COLUMN deleted_at TIMESTAMP NULL;

-- Create index for faster queries filtering out deleted records
CREATE INDEX idx_is_deleted ON claims(is_deleted);
