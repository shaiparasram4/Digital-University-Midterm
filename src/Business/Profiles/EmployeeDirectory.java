package Business.Profiles;

import Business.Business;
import Business.Person.Person;
import java.util.ArrayList;
import java.util.List;

/** Administrator role registry adapted from skeleton. Shai Rashada-Parasram. */
public class EmployeeDirectory {
    private final List<EmployeeProfile> employees = new ArrayList<>();
    public EmployeeDirectory(Business business) { }
    public EmployeeProfile newEmployeeProfile(Person person) {
        EmployeeProfile profile = new EmployeeProfile(person);
        employees.add(profile);
        return profile;
    }
    public EmployeeProfile findEmployee(String id) {
        for (EmployeeProfile profile : employees) if (profile.isMatch(id)) return profile;
        return null;
    }
    public boolean remove(String id) { return employees.remove(findEmployee(id)); }
}
