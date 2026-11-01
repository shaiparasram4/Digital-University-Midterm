# Digital University System — Testing Guide

**Course:** INFO 5100  
**Project:** Digital University System — Access Control Midterm  
**Implementation Focus:** Administrator Use Case

## Testing Environment

- IDE: Apache NetBeans 16
- Language: Java
- Interface: Java Swing
- Build System: Apache Ant

## Tests Performed

| Test | Expected Result | Observed Result |
|---|---|---|
| Clean and Build | Application compiles successfully | Passed |
| Administrator Login | Valid Administrator credentials grant access | Passed |
| Student Registration | Administrator can register a student | Passed |
| Account Creation | Administrator can create a Student login | Passed |
| Student Login | Student account opens Student workspace | Passed |
| Course Catalog | Student can view available courses | Passed |
| Profile Update | Student can update their own profile | Passed |
| Logout | User returns to login screen | Passed |
| Invalid Password | Incorrect credentials are rejected | Passed |
| UI Navigation | Administrator dashboard and navigation display correctly | Passed |

## Authentication and Authorization

The application uses role-based authentication and authorization.

Administrator accounts have access to university record management and user account administration.

Student accounts display a restricted workspace without Administrator management controls.

Additional security testing should verify that unauthorized operations are rejected by the underlying business logic, not only hidden in the interface.

## Additional Tests Recommended

- Reject duplicate usernames and student IDs
- Reject invalid or missing required fields
- Test faculty record modifications
- Test account disabling and deletion
- Verify students cannot modify other users' records
- Verify authorization after logout

## Known Limitations

- Demo records are stored in memory and reset after restarting the application.
- The completed use case focuses on Administrator functionality.
- Full Student and Faculty workflows are outside the implemented scope.

## Build Verification

The repository was downloaded as a ZIP, extracted, opened in NetBeans 16, and successfully compiled and launched.

The Administrator login and updated user interface were verified using the downloaded GitHub version.
