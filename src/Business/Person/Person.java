package Business.Person;

/**
 * One person in the university directory. The identifier never changes so
 * linked user accounts and role profiles remain valid after an edit.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public class Person {
    public enum Category { ADMIN, FACULTY, STUDENT, STAFF }

    private final String id;
    private String fullName;
    private String email;
    private Category category;
    private String department;
    private String detail; // Student program / faculty title / staff responsibility
    private int completedCredits;

    public Person(String id) {
        this(id, id, "unknown@example.edu", Category.STAFF, "Unassigned", "", 0);
    }

    public Person(String id, String name, String email, Category category,
                  String department, String detail, int completedCredits) {
        if (id == null || !id.trim().matches("[A-Za-z0-9-]{3,24}")) {
            throw new IllegalArgumentException("Person ID must contain 3–24 letters, numbers, or hyphens.");
        }
        this.id = id.trim();
        this.category = java.util.Objects.requireNonNull(category, "Person category is required.");
        update(name, email, department, detail, completedCredits);
    }

    /** Validate before mutation so invalid edits cannot corrupt existing data. */
    public void update(String name, String email, String department, String detail, int credits) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            throw new IllegalArgumentException("Name must be between 2 and 100 characters.");
        }
        if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty.");
        }
        if (credits < 0 || credits > 300) {
            throw new IllegalArgumentException("Completed credits must be between 0 and 300.");
        }
        this.fullName = name.trim();
        this.email = email.trim();
        this.department = department.trim();
        this.detail = detail == null ? "" : detail.trim();
        this.completedCredits = category == Category.STUDENT ? credits : 0;
    }

    public String getPersonId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Category getCategory() { return category; }
    public String getDepartment() { return department; }
    public String getDetail() { return detail; }
    public int getCompletedCredits() { return completedCredits; }
    public boolean isMatch(String value) { return value != null && id.equalsIgnoreCase(value); }
    @Override public String toString() { return id + " — " + fullName; }
}
