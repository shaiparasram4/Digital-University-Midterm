package Business.Profiles;

import Business.Person.Person;
import java.util.ArrayList;
import java.util.List;

/** Student role registry adapted from skeleton. Shai Rashada-Parasram. */
public class StudentDirectory {
    private final List<StudentProfile> students = new ArrayList<>();
    public StudentProfile newStudentProfile(Person person) {
        StudentProfile profile = new StudentProfile(person);
        students.add(profile);
        return profile;
    }
    public StudentProfile findStudent(String id) {
        for (StudentProfile profile : students) if (profile.isMatch(id)) return profile;
        return null;
    }
    public boolean remove(String id) { return students.remove(findStudent(id)); }
}
