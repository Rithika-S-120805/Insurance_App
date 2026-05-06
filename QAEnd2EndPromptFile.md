# End-to-End QA Workflow with Natural Language

## Workflow Overview

This prompt guides you through a complete 7-step QA workflow using MCP servers and AI agents to go from user story to committed automated test scripts for the Insurance POS User Management module.

---

## STEP 1: Read User Story

### Prompt

I need to start a new testing workflow. Please read the user story from the file:

user-stories/SCRUM-102-insurance-user-management.md

Summarize the key requirements, acceptance criteria, and testing scope.

### Expected Output

- Summary of the user story
- List of acceptance criteria
- Application details and test credentials
- Key features to test

---

## STEP 2: Create Test Plan

### Prompt

Based on the user story SCRUM-102 that we just reviewed, use the playwright-test-planner agent to:

1. Read the application details and credentials from the user story  
2. Explore the Insurance POS application and understand workflows related to:
   - User creation
   - User update
   - Role assignment
   - Login and authentication
   - Activation/deactivation  

3. Create a comprehensive test plan that covers:
   - Happy path scenarios
   - Negative scenarios (invalid input, empty fields, duplicate users)
   - Edge cases (role conflicts, inactive users login)
   - Navigation and access control tests
   - UI validation and form behavior  

4. Save the test plan as:
specs/insurance-user-management-test-plan.md

Ensure each test scenario includes:
- Test case title  
- Steps  
- Expected results  
- Test data  

### Expected Output

- Complete test plan in specs/
- Structured test scenarios
- Full coverage

---

## STEP 3: Perform Exploratory Testing

### Prompt

Read the test plan from:
specs/insurance-user-management-test-plan.md

Then:

1. Execute test scenarios using Playwright browser tools  
2. Validate:
   - User creation  
   - Form validations  
   - Role-based access  
   - Login/logout  
3. Capture screenshots for:
   - Success cases  
   - Errors  
4. Document:
   - Results  
   - UI issues  
   - Bugs  

### Expected Output

- Manual test results  
- Screenshots  
- Observations  

---

## STEP 4: Generate Automation Scripts

### Prompt

Use the playwright-test-generator agent.

Inputs:
- specs/insurance-user-management-test-plan.md  
- Exploratory testing results  

Generate scripts for:
- User creation  
- Update  
- Role assignment  
- Login/logout  
- Activation/deactivation  

Save in:
tests/insurance-user-management/

Requirements:
- Use expect() assertions  
- Use stable selectors  
- Add hooks (beforeEach, afterEach)  
- Follow Playwright best practices  

### Expected Output

- Scripts created  
- Organized test suites  
- Initial execution  

---

## STEP 5: Execute and Heal Automation Tests

### Prompt

1. Run tests from:
tests/insurance-user-management/

2. Identify failures  
3. Use playwright-test-healer to:
   - Fix selectors  
   - Add waits  
   - Adjust assertions  

4. Re-run until stable  

### Expected Output

- All tests passing  
- Updated scripts  
- Healing summary  

---

## STEP 6: Create Test Report

### Prompt

Save report as:
test-results/SCRUM-102-user-management-test-report.md

Include:

1. Executive Summary  
2. Manual Test Results  
3. Automation Results  
4. Defect Log  
5. Coverage Analysis  
6. Recommendations  

### Expected Output

- Complete QA report  
- Bug details  
- Coverage insights  

---

## STEP 7: Commit to Git Repository

### Prompt

1. Initialize Git  
2. Stage files  
3. Commit with message:

feat(tests): Add test suite for SCRUM-102 user management module

Add user story documentation  
Add test plan  
Add exploratory testing results  
Add automation scripts  
Include validation and role-based tests  
Resolves SCRUM-102  

4. Push to repository  

### Expected Output

- Files committed  
- Push successful  
- Summary of changes  

---

## Complete Workflow Execution

### Combined Prompt

- Read: user-stories/SCRUM-102-insurance-user-management.md  
- Create test plan → specs/insurance-user-management-test-plan.md  
- Perform exploratory testing  
- Generate scripts → tests/insurance-user-management/  
- Execute & heal tests  
- Create report → test-results/SCRUM-102-user-management-test-report.md  
- Commit and push  

Provide updates after each step.

---

## Notes

- Paths are relative  
- Follow Playwright best practices  
- Ensure full acceptance criteria coverage  
- Focus on validation and role-based access  