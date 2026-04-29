-- Add user_id column to claims table
ALTER TABLE claims ADD COLUMN user_id BIGINT;
ALTER TABLE claims ADD CONSTRAINT fk_claims_user_id FOREIGN KEY (user_id) REFERENCES users(user_id);
CREATE INDEX idx_claims_user_id ON claims(user_id);

-- Add user_id column to payments table
ALTER TABLE payments ADD COLUMN user_id BIGINT;
ALTER TABLE payments ADD CONSTRAINT fk_payments_user_id FOREIGN KEY (user_id) REFERENCES users(user_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
