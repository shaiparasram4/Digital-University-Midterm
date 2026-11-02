package UniversityApp.tests;

import Business.Business;
import Business.ConfigureABusiness;
import Business.Person.Person;
import Business.UserAccounts.UserAccount;

/**
 * Repeatable functional/security smoke tests (no external libraries).
 * Run: java -cp build/classes UniversityApp.tests.MidtermModelTest
 * Test implementation: Shai Rashada-Parasram.
 */
public final class MidtermModelTest {
    private static int passed = 0;
    private MidtermModelTest() { }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("FAILED: " + description);
        passed++;
        System.out.println("PASS " + passed + " - " + description);
    }
    private static void rejects(Runnable action, String description) {
        try { action.run(); }
        catch (IllegalArgumentException | SecurityException expected) {
            check(true, description);
            return;
        }
        throw new AssertionError("FAILED - must reject: " + description);
    }

    public static void main(String[] args) {
        Business business = ConfigureABusiness.initialize();
        check(business.getUniversity().getCourses().size() == 4, "Original university course model is loaded");
        check(business.getUniversity().getCollege().getDepartments().size() == 1, "College contains integrated department");
        check(business.getAuthService().login("admin", "wrong-pass") == null, "Wrong password denied");
        check(business.getAuthService().login("not-a-user", "Admin123!") == null, "Unknown user denied");
        check(business.getAuthService().login("", "") == null, "Blank login denied");
        String student = business.getAuthService().login("student", "Student123!");
        check(student != null, "Student login succeeds");
        rejects(() -> business.listAccounts(student), "Student cannot read all accounts");
        rejects(() -> business.listPeople(student), "Student cannot read university directory");
        rejects(() -> business.registerPerson(student, "S9999", "Some Student", "some@example.edu",
                Person.Category.STUDENT, "Engineering", "BS", 0), "Student cannot register people");
        String faculty = business.getAuthService().login("faculty", "Faculty123!");
        check(faculty != null, "Faculty login succeeds");
        rejects(() -> business.deleteAccount(faculty, "admin"), "Faculty cannot delete admin");
        String staff = business.getAuthService().login("staff", "Staff123!");
        check(staff != null, "Staff login succeeds");
        rejects(() -> business.editPerson(staff, "S2001", "Bad Name", "bad@x.edu", "Data", "X", 1), "Staff cannot modify student");
        // Additional role-based authorization tests.
        // Verify that non-administrators cannot change protected records.
        rejects(() -> business.editPerson(student, "F3001",
                "Unauthorized Change", "change@example.edu",
                "Other", "Other", 0),
                "Student cannot edit faculty records");

        rejects(() -> business.deletePerson(student, "S2001"),
                "Student cannot delete student records");

        rejects(() -> business.listAccounts(staff),
                "Staff cannot view all user accounts");

        rejects(() -> business.deletePerson(faculty, "S2001"),
                "Faculty cannot delete student records");

        String admin = business.getAuthService().login("admin", "Admin123!");
        check(admin != null, "Administrator login succeeds");
        check(business.listPeople(admin).size() == 4, "Seed people present");
        rejects(() -> business.registerPerson(admin, "S2001", "Duplicate", "duplicate@example.edu",
                Person.Category.STUDENT, "Engineering", "BS", 0), "Duplicate person ID prevented");
        rejects(() -> business.registerPerson(admin, "S8010", "New Student", "bad-email",
                Person.Category.STUDENT, "Engineering", "BS", 0), "Invalid email rejected");
        rejects(() -> business.registerPerson(admin, "S8010", "New Student", "new@example.edu",
                Person.Category.STUDENT, "Engineering", "BS", -3), "Negative credits rejected");
        
        // Test department length validation.
        // Departments longer than 80 characters must be rejected.
        String longDepartment =
                new String(new char[81]).replace('\0', 'A');

        rejects(() -> business.registerPerson(
                admin, "S8010", "New Student",
                "new@example.edu",
                Person.Category.STUDENT,
                longDepartment, "BS", 0),
                "Department over 80 characters rejected");

        Person newStudent = business.registerPerson(admin, "S8010", "Alex Turner", "alex.turner@example.edu",
                Person.Category.STUDENT, "Engineering", "BS Data Science", 12);
        check(newStudent.getCategory() == Person.Category.STUDENT, "Create student profile");
        check(business.listPeople(admin).size() == 5, "Student appears in directory");
        business.editPerson(admin, "S8010", "Alex T. Turner", "alex.t@example.edu", "Information Systems", "MS Analytics", 24);
        check(newStudent.getCompletedCredits() == 24 && "Alex T. Turner".equals(newStudent.getFullName()), "Update student academic record");
        business.registerPerson(admin, "F8011", "Dr. Helen Wu", "helen.wu@example.edu",
                Person.Category.FACULTY, "Computer Science", "Associate Professor", 0);
        check(business.getPersonDirectory().findPerson("F8011").getCategory() == Person.Category.FACULTY,
                "Create faculty profile");
        business.registerPerson(admin, "T8012", "Alex Cooper", "alex.cooper@example.edu",
                Person.Category.STAFF, "Registrar", "Office support", 0);
        check(business.listPeople(admin).size() == 7, "Create academic staff profile");
        UserAccount account = business.createAccount(admin, "S8010", "alext", "ValidPass123!");
        check("Student".equals(account.getRole()), "Role derived from person's profile");
        rejects(() -> business.createAccount(admin, "S8010", "another", "ValidPass123!"), "Second account per person rejected");
        rejects(() -> business.createAccount(admin, "F8011", "alext", "ValidPass123!"), "Duplicate username rejected");
        rejects(() -> business.createAccount(admin, "F8011", "helen", "x"), "Weak password rejected");
        business.editAccount(admin, "alext", "alexturner", "UpdatedPass123!", true);
        check(business.getAuthService().login("alext", "ValidPass123!") == null, "Old login/password no longer works");
        String temp = business.getAuthService().login("alexturner", "UpdatedPass123!");
        check(temp != null, "Updated username/password works");
        rejects(() -> business.editAccount(admin, "admin", "admin", "", false), "Cannot disable self");
        rejects(() -> business.deleteAccount(admin, "admin"), "Cannot delete own admin account");
        rejects(() -> business.deletePerson(admin, "A1001"), "Cannot delete own admin record");
        business.editAccount(admin, "alexturner", "alexturner", "", false);
        check(business.getAuthService().login("alexturner", "UpdatedPass123!") == null, "Disabled user cannot log in");
        rejects(() -> business.getAuthService().requireUser(temp), "Disabled user's session invalidated");
        business.editAccount(admin, "alexturner", "alexturner", "", true);
        check(business.getAuthService().login("alexturner", "UpdatedPass123!") != null, "Account re-enable works");
        business.deletePerson(admin, "S8010");
        check(business.getPersonDirectory().findPerson("S8010") == null, "Delete student record");
        check(business.getUserAccountDirectory().findByUsername("alexturner") == null, "Delete person cascades to account");
        business.deleteAccount(admin, "faculty");
        check(business.getPersonDirectory().findPerson("F3001") != null, "Deleting login preserves person");
        business.updateOwnProfile(student, "Jordan Updated", "jordan.updated@university.edu", "MS Information Systems", "NewStudentPassword!");
        check("Jordan Updated".equals(business.getMyPerson(student).getFullName()), "Student may modify own profile");
        check(business.getAuthService().login("student", "Student123!") == null, "Old profile password denied");
        business.getAuthService().logout(student);
        rejects(() -> business.getMyPerson(student), "Logout invalidates session");
        business.getAuthService().logout(admin);
        rejects(() -> business.listPeople(admin), "Admin access denied after logout");
        System.out.println("ALL " + passed + " TESTS PASSED");
    }
}
