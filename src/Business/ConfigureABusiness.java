package Business;

import Business.Person.Person;

/**
 * Builds the in-memory demonstration university, including one account for
 * each role. Demo credentials are documented in the README.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public final class ConfigureABusiness {
    private ConfigureABusiness() { }

    public static Business initialize() {
        Business university = new Business("Digital University System");
        university.seedPerson("A1001", "University Administrator", "administrator@university.edu",
                Person.Category.ADMIN, "University Administration", "System administration", 0,
                "admin", "Admin123!");
        university.seedPerson("S2001", "Jordan Lee", "jordan.lee@university.edu",
                Person.Category.STUDENT, "Information Systems", "MS Information Systems", 16,
                "student", "Student123!");
        university.seedPerson("F3001", "Dr. Morgan Chen", "morgan.chen@university.edu",
                Person.Category.FACULTY, "Information Systems", "Senior Lecturer", 0,
                "faculty", "Faculty123!");
        university.seedPerson("T4001", "Taylor Rivera", "taylor.rivera@university.edu",
                Person.Category.STAFF, "Academic Services", "Registrar assistant", 0,
                "staff", "Staff123!");
        return university;
    }
}
