# SCRUM-102: Insurance POS – User Management

## User Story

As an admin, I want to manage users (create, update, view, and deactivate accounts) so that I can control access to the insurance POS system efficiently and securely.

---

## Acceptance Criteria

### AC1: Admin can create a new user
- **Given** the admin is on the user management page  
- **When** they enter valid user details (name, email, role, phone, password)  
- **Then** a new user account should be created successfully  

### AC2: Admin can view all users
- **Given** users exist in the system  
- **When** the admin navigates to the user list  
- **Then** all users should be displayed with details like name, role, status, and contact information  

### AC3: Admin can update user details
- **Given** an existing user account  
- **When** the admin edits user information  
- **Then** the updated details should be saved and reflected in the system  

### AC4: Admin can assign roles to users
- **Given** a user account exists  
- **When** the admin assigns a role (e.g., Agent, Manager, Admin)  
- **Then** the user should have access permissions based on the assigned role  

### AC5: Admin can activate/deactivate users
- **Given** an existing user account  
- **When** the admin changes the user status (active/inactive)  
- **Then** the user’s access should be enabled or restricted accordingly  

### AC6: User can log in with valid credentials
- **Given** a registered and active user  
- **When** they enter valid login credentials  
- **Then** they should be successfully logged into the system  

### AC7: User cannot log in if deactivated
- **Given** a user account is inactive  
- **When** the user attempts to log in  
- **Then** access should be denied with an appropriate error message  

### AC8: Validation errors should be shown
- **Given** invalid or incomplete user data  
- **When** the admin submits the form  
- **Then** appropriate validation messages should be displayed  

---

## Application Details

- **Application Type:** Insurance POS Web Application  
- **User Roles:** Admin, Agent, Manager  
- **Platform:** Web-based system  

---

## Testing Scope

### In Scope
- User creation and management  
- Role assignment and access control  
- Login and authentication  
- User activation/deactivation  
- Form validation and error handling  

### Out of Scope
- Third-party authentication (OAuth, SSO)  
- Password recovery via email/SMS  
- External identity provider integration  

---

## Key Features to Test

### 1. User Creation
- Mandatory field validation  
- Unique email enforcement  
- Role assignment during creation  

### 2. User Management
- View user list  
- Edit user details  
- Activate/deactivate users  

### 3. Authentication
- Login with valid credentials  
- Error handling for invalid login  
- Access restriction for inactive users  

### 4. Role-Based Access Control
- Admin full access  
- Agent limited access  
- Manager supervisory access  

### 5. Validation & Errors
- Required fields validation  
- Invalid email format  
- Duplicate user handling  

---

## Test Data Requirements

### Valid Test Data
- Name: `Rithika S`  
- Email: `rithika@test.com`  
- Role: `Agent`  
- Phone: `9876543210`  
- Password: `Test@123`  

### Invalid Test Data
- Email: `rithika.com` (invalid format)  
- Empty fields  
- Duplicate email  
- Weak password: `12345`  

---

## Environment Details

- **Browser:** Chrome (latest)  
- **Operating System:** Windows 10  
- **Test Environment:** Local / Staging server  
- **Network:** Stable connectivity  

---

## Success Criteria

- All acceptance criteria are met  
- No critical defects in user management flow  
- Login response time < 2 seconds  
- Proper role-based access enforced  

---

## Dependencies

- Admin account access  
- Database connectivity  
- Authentication service availability  

---

## Estimated Effort

- QA Planning: 2 hours  
- Test Execution: 6 hours  
- Defect Reporting: 2 hours  
- **Total: 10 hours**

---

## Notes

- Ensure role-based access is strictly enforced  
- Passwords should be securely stored (hashed)  
- Audit logs for user actions can be considered for future enhancement  