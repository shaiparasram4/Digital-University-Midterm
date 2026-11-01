package Business.Profiles;

import Business.Person.Person;

/** Student role is linked to the inherited Person; avoids the skeleton's
 * shadowed/null person field. Midterm: Shai Rashada-Parasram. */
public class StudentProfile extends Profile {
    public StudentProfile(Person person) { super(person); }
    @Override public String getRole() { return "Student"; }
}
