package Business.Profiles;

import Business.Person.Person;

/** Faculty account profile used for role-based access. Shai Rashada-Parasram. */
public class FacultyProfile extends Profile {
    public FacultyProfile(Person person) { super(person); }
    @Override public String getRole() { return "Faculty"; }
}
