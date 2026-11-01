package UniversityApp.ui;

import Business.Person.Person;
import UniversityApp.DigitalUniversityApp;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Non-admin landing page. It deliberately provides no admin CRUD functions.
 * Faculty/student workflows beyond login/profile/catalog are out of scope.
 * Midterm: Shai Rashada-Parasram. */
public class RoleWorkspacePanel extends JPanel {
    public RoleWorkspacePanel(DigitalUniversityApp app) {
        setLayout(new BorderLayout());
        Person person = app.getBusiness().getMyPerson(app.getSessionToken());
        JPanel page = UiKit.page("Welcome, " + person.getFullName(),
                "Your role is " + person.getCategory() + ". Administrator functions are restricted.");
        JTextArea info = new JTextArea("UNIVERSITY ACCESS\n\n" +
                "You are authenticated with the " + person.getCategory() + " role.\n\n" +
                "Available:\n" +
                "  • View the course catalog from the university reference model\n" +
                "  • View and update your own profile\n" +
                "  • Change your own password and log out\n\n" +
                "Administrator-only actions (user accounts, student/faculty records)\n" +
                "are not accessible with this role.\n\n" +
                "Note: this midterm focuses on completing the Administrator use case.\n" +
                "Course enrollment, faculty grading, and transcripts are not implemented.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
        page.add(info, BorderLayout.CENTER);
        add(page);
    }
}
