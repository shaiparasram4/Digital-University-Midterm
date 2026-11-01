package Business;

import Business.Person.Person;
import Business.Person.PersonDirectory;
import Business.Profiles.EmployeeDirectory;
import Business.Profiles.EmployeeProfile;
import Business.Profiles.FacultyProfile;
import Business.Profiles.Profile;
import Business.Profiles.StaffProfile;
import Business.Profiles.StudentDirectory;
import Business.Profiles.StudentProfile;
import Business.UserAccounts.UserAccount;
import Business.UserAccounts.UserAccountDirectory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level container adapted from the professor's access-control skeleton.
 * Supported GUI writes go through methods which check the logged-in role.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public class Business {
    private final String name;
    private final PersonDirectory people = new PersonDirectory();
    private final EmployeeDirectory employees = new EmployeeDirectory(this);
    private final StudentDirectory students = new StudentDirectory();
    private final UserAccountDirectory accounts = new UserAccountDirectory();
    private final Map<String, Profile> profiles = new LinkedHashMap<>();
    private final UniversityModelBridge university = new UniversityModelBridge();
    private final AuthService auth = new AuthService(this);

    public Business(String name) { this.name = name; }
    public String getName() { return name; }
    public AuthService getAuthService() { return auth; }
    public UniversityModelBridge getUniversity() { return university; }
    public PersonDirectory getPersonDirectory() { return people; }
    public EmployeeDirectory getEmployeeDirectory() { return employees; }
    public StudentDirectory getStudentDirectory() { return students; }
    public UserAccountDirectory getUserAccountDirectory() { return accounts; }

    /** Called only at program startup to seed the local demo accounts. */
    void seedPerson(String id, String name, String email, Person.Category type,
                           String department, String detail, int credits, String username, String password) {
        Person person = people.newPerson(id, name, email, type, department, detail, credits);
        Profile profile = createProfile(person);
        profiles.put(person.getPersonId(), profile);
        if (username != null) accounts.newUserAccount(profile, username, password);
    }

    private Profile createProfile(Person person) {
        switch (person.getCategory()) {
            case ADMIN: return employees.newEmployeeProfile(person);
            case STUDENT: return students.newStudentProfile(person);
            case FACULTY: return new FacultyProfile(person);
            case STAFF: return new StaffProfile(person);
            default: throw new IllegalArgumentException("Unsupported person category.");
        }
    }

    public synchronized Person registerPerson(String token, String id, String name,
                    String email, Person.Category category, String department,
                    String detail, int credits) {
        auth.requireAdmin(token);
        Person person = people.newPerson(id, name, email, category, department, detail, credits);
        profiles.put(person.getPersonId(), createProfile(person));
        return person;
    }

    public synchronized void editPerson(String token, String id, String name, String email,
                                        String department, String detail, int credits) {
        auth.requireAdmin(token);
        requirePerson(id).update(name, email, department, detail, credits);
    }

    public synchronized void deletePerson(String token, String id) {
        UserAccount actor = auth.requireAdmin(token);
        Person person = requirePerson(id);
        if (actor.getPersonId().equalsIgnoreCase(id)) {
            throw new IllegalArgumentException("You cannot delete your own profile.");
        }
        if (person.getCategory() == Person.Category.ADMIN && countEnabledAdmins() <= 1) {
            throw new IllegalArgumentException("At least one enabled administrator must remain.");
        }
        UserAccount account = accounts.findUserAccount(id);
        if (account != null) { auth.invalidate(account); accounts.remove(account); }
        profiles.remove(person.getPersonId());
        people.remove(person.getPersonId());
        // The directory references in the skeleton are cleaned when the person is deleted.
        employees.remove(id);
        students.remove(id);
    }

    public synchronized List<Person> listPeople(String token) {
        auth.requireAdmin(token);
        return new ArrayList<>(people.getPeople());
    }

    public synchronized List<UserAccount> listAccounts(String token) {
        auth.requireAdmin(token);
        return accounts.getUserAccountList();
    }

    public synchronized UserAccount createAccount(String token, String id,
                                                  String username, String password) {
        auth.requireAdmin(token);
        Profile profile = profiles.get(requirePerson(id).getPersonId());
        return accounts.newUserAccount(profile, username, password);
    }

    public synchronized void editAccount(String token, String username, String newUsername,
                                         String newPassword, boolean enabled) {
        UserAccount actor = auth.requireAdmin(token);
        UserAccount target = requireAccount(username);
        UserAccount collision = accounts.findByUsername(newUsername);
        if (collision != null && collision != target) throw new IllegalArgumentException("Username already exists.");
        if (!enabled && actor == target) throw new IllegalArgumentException("You cannot disable your own account.");
        if (!enabled && isLastEnabledAdmin(target)) {
            throw new IllegalArgumentException("At least one enabled administrator must remain.");
        }
        // Validate all new values before touching the stored record.
        if (newUsername == null || !newUsername.trim().matches("[A-Za-z0-9._-]{3,30}")) {
            throw new IllegalArgumentException("Username must contain 3–30 valid characters.");
        }
        if (newPassword != null && !newPassword.isEmpty()) UserAccount.validatePassword(newPassword);
        target.changeUsername(newUsername);
        if (newPassword != null && !newPassword.isEmpty()) target.changePassword(newPassword);
        target.setEnabled(enabled);
        if (!enabled) auth.invalidate(target);
    }

    public synchronized void deleteAccount(String token, String username) {
        UserAccount actor = auth.requireAdmin(token);
        UserAccount target = requireAccount(username);
        if (actor == target) throw new IllegalArgumentException("You cannot delete your own account.");
        if (isLastEnabledAdmin(target)) throw new IllegalArgumentException("At least one administrator must remain.");
        auth.invalidate(target);
        accounts.remove(target);
    }

    public synchronized void updateOwnProfile(String token, String name,
                            String email, String detail, String newPassword) {
        UserAccount actor = auth.requireUser(token);
        Person person = actor.getAssociatedPersonProfile().getPerson();
        if (newPassword != null && !newPassword.isEmpty()) UserAccount.validatePassword(newPassword);
        person.update(name, email, person.getDepartment(), detail, person.getCompletedCredits());
        if (newPassword != null && !newPassword.isEmpty()) actor.changePassword(newPassword);
    }

    public Person getMyPerson(String token) { return auth.requireUser(token).getAssociatedPersonProfile().getPerson(); }
    public UserAccount getMyAccount(String token) { return auth.requireUser(token); }

    private Person requirePerson(String id) {
        Person person = people.findPerson(id);
        if (person == null) throw new IllegalArgumentException("Person ID was not found.");
        return person;
    }
    private UserAccount requireAccount(String username) {
        UserAccount account = accounts.findByUsername(username);
        if (account == null) throw new IllegalArgumentException("Username was not found.");
        return account;
    }
    private boolean isLastEnabledAdmin(UserAccount account) {
        return account.isEnabled() && "Admin".equalsIgnoreCase(account.getRole()) && countEnabledAdmins() <= 1;
    }
    private long countEnabledAdmins() {
        return accounts.getUserAccountList().stream().filter(a -> a.isEnabled() && "Admin".equalsIgnoreCase(a.getRole())).count();
    }
}
