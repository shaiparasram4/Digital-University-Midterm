package UniversityApp.ui;

import Business.UniversityModelBridge;
import UniversityApp.DigitalUniversityApp;
import info5100.university.example.CourseCatalog.Course;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Read-only course list uses original Digital University reference package.
 * Midterm integration: Shai Rashada-Parasram. */
public class CourseReferencePanel extends JPanel {
    public CourseReferencePanel(DigitalUniversityApp app) {
        setLayout(new BorderLayout());
        JPanel page = UiKit.page("Digital University Course Catalog", "Reference Department/Course/CourseCatalog model; viewable by signed-in roles.");
        DefaultTableModel model = UiKit.model("Course number", "Course title", "Credits", "Tuition (sample)");
        UniversityModelBridge reference = app.getBusiness().getUniversity();
        for (Course course : reference.getCourses()) {
            model.addRow(new Object[] {course.getCOurseNumber(), course.getName(), course.getCredits(),
                    "$" + course.getCoursePrice()});
        }
        page.add(UiKit.table(new JTable(model)), BorderLayout.CENTER);
        add(page);
    }
}
