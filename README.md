# Digital University System — Access Control Midterm

INFO 5100
Shai Rashada-Parasram  
NUID:003946578   
Team: 7 

## Project overview

This NetBeans Java Swing project integrates the instructor's ProfileWorkareaExample Access Control skeleton with the Digital University Model reference Java sources. The delivered, fully implemented use case is Administrator. Faculty, Student, and academic Staff demo accounts can log in, view their own profile, update it, and read a course catalog; their additional workflows such as grading and course registration.
### Implemented Administrator responsibilities

| Responsibility | Demonstration |
|---|---|
| User account administration | Create an account for a registered person; edit username or password, enable/disable accounts, delete accounts, see last login and last update |
| Register people | Create an Administrator, Student, Faculty, or Staff person and associated role profile |
| Student records | Search, view, update and delete students; track program, department, and completed credits |
| Faculty records | Search, view, update and delete faculty; track department and academic title |
| Academic staff records | Register and manage staff records |
| Own profile | Update display name, email, descriptive role details and password |
| University model integration | Read-only course catalog backed by the instructor's `College`, `Department`, and `Course` reference classes |

### Authentication and authorization

- Login checks a username/password through the skeleton's `UserAccountDirectory` and creates a random in-memory session token.
- A password is stored as a PBKDF2-HMAC-SHA256 salted hash in memory, not as a raw string.
- Each administrator action calls `AuthService.requireAdmin(token)` in the Business service layer, not merely a UI visibility check.
- Faculty, Student, and Staff logins cannot invoke administrator service operations.
- Disabled or deleted accounts lose active sessions; logout invalidates the session immediately.
- The app blocks invalid IDs, duplicate usernames, duplicate person logins, weak passwords, blank names, invalid email, negative credits, deletion of self, and disabling/deleting the last admin.


## Requirements

- Apache NetBeans 16 or newer
- Java Development Kit 11 or newer (tested using JDK 21; Java source compatibility 11)
- Standard Ant build (NetBeans project) and included skeleton `lib/` folder
- No database or external services needed

## Open and run in NetBeans

1. Extract the complete ZIP to a normal writable folder.
2. Open NetBeans → File → Open Project**.
3. Select the `Digital_University_Midterm` folder (the folder containing `build.xml` and `nbproject`).
4. Right-click DigitalUniversityMidterm → Clean and Build.
5. Right-click DigitalUniversityMidterm → Run.
6. The startup class is `UniversityApp.DigitalUniversityApp` (set in `nbproject/project.properties`).
7. Choose one of the credentials below and select Sign in.

### Demonstration accounts (do not use these passwords elsewhere)

| Role | Username | Password |
|---|---|---|
| Administrator | `admin` | `Admin123!` |
| Student | `student` | `Student123!` |
| Faculty | `faculty` | `Faculty123!` |
| Academic Staff | `staff` | `Staff123!` |

## Usage walkthrough

Scenario A: Administrator creates and manages a student

1. Log in as `admin`.
2. Open Students → Register.
3. Enter a unique ID such as `S9001`, full name, email, department, program, and nonnegative credits.
4. Select the student record, choose Edit selected, and update its program or credits.
5. Open User Accounts → Create login, select this person, and set a unique username and password (at least 8 characters).
6. Log out; sign in with the new student's username and password. Observe that administrator functions are absent.
7. Log back in as administrator to disable or delete the student's account. You can delete the student record, which also deletes its linked account.

Scenario B: Faculty records and your own profile

1. Sign in as administrator and select Faculty.
2. Register a new faculty member with a department and title, then edit or delete that record.
3. Open My Profile to change your own name, email, or password.
4. Sign out and sign in with the new password.

Scenario C: Authorization rejection

1. Sign in as `student` or `faculty`.
2. Confirm that only My Workspace, Course Catalog, and My Profile are visible.
3. Check the automated tests for proof that service-layer calls to `listAccounts`, `registerPerson`, and `deleteAccount` throw `SecurityException` for non-admin users.
4. Log out. Reusing the previous token is rejected.

## Automated tests

Run from the project directory (after Clean and Build):

```bash
java -cp build/classes UniversityApp.tests.MidtermModelTest
```

The test suite verifies the university model, invalid credentials, role restrictions, people CRUD, username/password changes, enable/disable, duplicate prevention, self-protection, and logout behavior. It reports 42 passing checks in the tested build.

The optional `UniversityApp.tests.UiLaunchSmokeTest` checks login, key Administrator navigation, form availability, and logout. It requires a graphical display and saves a local preview if run from the developer test environment; it is not required to run the application.

Manual regression checks: register Student/Faculty/Staff; modify Student credits; create and disable an account; attempt an invalid email/negative credits; try duplicate IDs and usernames; verify student login cannot access admin navigation; sign out and sign in again.

## Java architecture

- `Business/Person`: University person record and searchable person directory
- `Business/Profiles`: Role profiles (`EmployeeProfile` for Admin, `FacultyProfile`, `StudentProfile`, `StaffProfile`)
- `Business/UserAccounts`: User accounts, password hashes, authentication directory
- `Business/Business.java`, `Business/AuthService.java`: All supported application CRUD/authorization operations and session lifecycle
- `Business/UniversityModelBridge.java`: Connects the access-control skeleton to the separate university reference classes
- `info5100/university/example`: Original Digital University reference package, provided by the instructor
- `UniversityApp/DigitalUniversityApp.java`: Main Swing shell, login/logout, CardLayout, role navigation
- `UniversityApp/ui`: Dedicated Administrator panels, My Profile, catalog, shared styling and restricted non-admin landing page
- `UniversityApp/tests`: Repeatable security/functional smoke tests
- `UserInterface/WorkAreas` and `Business/ProfileWorkAreaMainFrame.java`: Original instructor skeleton UI classes retained for reference; the new startup uses `UniversityApp.DigitalUniversityApp`.

All substantial new and modified classes contain explanatory comments and Shai Rashada-Parasram's implementation attribution. Pre-existing professor/reference classes retain their original author credit; the university course and college classes have explicitly labeled integration additions.

## Challenges and solutions

- Legacy access-control skeleton did not have a Faculty profile: added a faculty profile while preserving inherited `Profile` structure.
- Student profile shadowed its own `Person` field, creating a null-reference bug: removed the duplicate field and used `Profile.getPerson()`.
- No secure session and logout support: introduced an in-memory token session service with authorization checks performed by Business operations.
- University reference model was a separate project: imported its original Java packages and connected College → Department → Course for a real catalog view.
- Sparse or unfinished skeleton Swing forms: added a consistent multi-panel CardLayout application rather than claiming the original unfinished screens were complete.
- Unreadable native Windows buttons: the Windows look-and-feel displayed white button text against a light button face. `UiKit.button()` now paints a high-contrast teal background itself and includes hover, pressed, and disabled states for legible navigation and form actions.

## Future enhancements

Persist records in a relational database, implement full Faculty and Student responsibilities, apply formal role/permission policies, add a password reset flow, add account lockout and audit logs, and implement comprehensive automated Swing UI tests.

## Individual contributions and submission notes

Shai Rashada-Parasram: architecture/integration of the given skeleton and reference model, Administrator CRUD, login/logout, authorization, validation, UI design, code comments, README, and tests. No team members or external contributions are claimed for this individual build.

GitHub: This downloaded ZIP does not fabricate commits, contributors, or pull requests. If your professor applies the team GitHub requirements, you must create real commits and PRs in your own repository. The original rubric calls for at least 10 commits and 5 PRs per student and 30 commits on main; as an individual you should request an exception or adjusted requirement. The `main` branch should always remain runnable.

Canvas: Upload the full NetBeans project ZIP. Presentation slides and MP4/YouTube video are intentionally not included at the student's request. The presentation is 10 rubric points; the missing video might also affect grading. The project must be tested again in the student's local environment before submitting. Enter your NUID in this file before submitting; it was not provided.
