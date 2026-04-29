-- Insert test user data (passwords are BCrypt hashed)
INSERT INTO users (username, password, full_name, email, role) VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin User', 'admin@example.com', 'ADMIN');
INSERT INTO users (username, password, full_name, email, role, agent_id) VALUES ('john_doe', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Doe', 'john@example.com', 'CUSTOMER', 1);
INSERT INTO users (username, password, full_name, email, role, agent_id) VALUES ('jane_smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Smith', 'jane@example.com', 'AGENT', 1);

-- Insert test customer data
INSERT INTO customers (name, email, status, password, phone_number, address, date_of_birth, policy_count) VALUES ('John Doe', 'john@example.com', 'ACTIVE', 'password123', '9123456780', '123 Main St, City', '1990-05-15', 1);
INSERT INTO customers (name, email, status, password, phone_number, address, date_of_birth, policy_count) VALUES ('Jane Smith', 'jane@example.com', 'ACTIVE', 'password456', '9234567890', '456 Oak Ave, Town', '1985-08-22', 2);
INSERT INTO customers (name, email, status, password, phone_number, address, date_of_birth, policy_count) VALUES ('Test User', 'test@example.com', 'ACTIVE', 'test123', '9345678901', '789 Pine Rd, Village', '1995-12-10', 0);

-- Insert test policy data
INSERT INTO policies (policy_number, user_id, coverage_type, premium_amount, policy_type, start_date, end_date, status, sum_insured, term_in_months) VALUES ('POL-2024-001', 1, 'Health', 5000.00, 'Family', '2024-01-01', '2025-12-31', 'ACTIVE', 500000.00, 24);
INSERT INTO policies (policy_number, user_id, coverage_type, premium_amount, policy_type, start_date, end_date, status, sum_insured, term_in_months) VALUES ('POL-2024-002', 2, 'Life', 3000.00, 'Individual', '2024-02-01', '2034-01-31', 'ACTIVE', 1000000.00, 120);
INSERT INTO policies (policy_number, user_id, coverage_type, premium_amount, policy_type, start_date, end_date, status, sum_insured, term_in_months) VALUES ('POL-2024-003', 2, 'Auto', 2000.00, 'Full Coverage', '2024-03-01', '2025-02-28', 'ACTIVE', 200000.00, 12);

-- Insert test claim data
INSERT INTO claims (claim_number, policy_id, user_id, date_filed, claim_status, claim_amount, approved_amount, description, approval_date) VALUES ('CLM-2024-001', 1, 2, '2024-06-15', 'APPROVED', 50000.00, 45000.00, 'Hospitalization claim', '2024-06-25');
INSERT INTO claims (claim_number, policy_id, user_id, date_filed, claim_status, claim_amount, approved_amount, description, approval_date) VALUES ('CLM-2024-002', 2, 3, '2024-07-10', 'PENDING', 100000.00, NULL, 'Life insurance claim', NULL);

-- Insert test payment data
INSERT INTO payments (payment_reference, policy_id, user_id, payment_type, amount, payment_method, payment_status, payment_date, reference_number) VALUES ('PAY-2024-001', 1, 2, 'Premium', 5000.00, 'Credit Card', 'COMPLETED', '2024-01-15', 'REF-CC-2024-001');
INSERT INTO payments (payment_reference, policy_id, claim_id, user_id, payment_type, amount, payment_method, payment_status, payment_date, reference_number) VALUES ('PAY-2024-002', 1, 1, 2, 'Claim Settlement', 45000.00, 'Bank Transfer', 'COMPLETED', '2024-06-26', 'REF-BT-2024-001');
INSERT INTO payments (payment_reference, policy_id, user_id, payment_type, amount, payment_method, payment_status, payment_date, reference_number) VALUES ('PAY-2024-003', 2, 3, 'Premium', 3000.00, 'Net Banking', 'COMPLETED', '2024-02-10', 'REF-NB-2024-001');
