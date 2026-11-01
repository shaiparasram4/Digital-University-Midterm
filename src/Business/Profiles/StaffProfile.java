package Business.Profiles;

import Business.Person.Person;

/** Academic staff is a distinct non-admin role. Shai Rashada-Parasram. */
public class StaffProfile extends Profile {
    public StaffProfile(Person person) { super(person); }
    @Override public String getRole() { return "Staff"; }
}
