package Business;

import info5100.university.example.College.College;
import info5100.university.example.Department.Department;
import info5100.university.example.CourseCatalog.Course;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Integrates the professor's Digital University Model classes into the
 * access-control skeleton. Course lists are reference data in this midterm.
 * Implementation: Shai Rashada-Parasram.
 */
public final class UniversityModelBridge {
    private final College college;
    private final Department department;
    private final List<Course> courses = new ArrayList<>();

    public UniversityModelBridge() {
        college = new College("College of Engineering and Computing");
        department = new Department("Information Systems");
        college.addDepartment(department);
        // Original Department.newCourse(name, courseNumber, credits)
        courses.add(department.newCourse("Application Engineering", "INFO 5100", 4));
        courses.add(department.newCourse("Database Management", "INFO 6210", 4));
        courses.add(department.newCourse("Computer Systems", "CS 5001", 4));
        courses.add(department.newCourse("Data Analytics", "INFO 7390", 4));
    }

    public Department getDepartment() { return department; }
    public College getCollege() { return college; }
    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }
}
