package UniversityApp.ui;

import Business.Person.Person;
import UniversityApp.DigitalUniversityApp;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Admin dashboard summarizes university people and access responsibilities.
 * Midterm: Shai Rashada-Parasram. */
public class OverviewPanel extends JPanel implements DigitalUniversityApp.Refreshable, DigitalUniversityApp.AdminOnlyPanel {
    private final DigitalUniversityApp app;
    private final JLabel totals = UiKit.heading("");
    public OverviewPanel(DigitalUniversityApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        JPanel page = UiKit.page("Administrator overview", "Manage people, student and faculty records, and login permissions.");
        JPanel middle = new JPanel(new GridLayout(0,1,5,12));
        middle.setOpaque(false);
        middle.add(totals);
        JTextArea info = new JTextArea("ADMINISTRATOR RESPONSIBILITIES\n\n" +
                "• Register students, faculty, academic staff, and administrators\n" +
                "• View, update, or remove student and faculty records\n" +
                "• Create, edit, disable, and delete user accounts\n" +
                "• Manage your own profile and password\n" +
                "• Review reference university courses\n\n" +
                "Access checks run inside the Business service, not only in the interface.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
        middle.add(info);
        page.add(middle, BorderLayout.CENTER);
        add(page);
        refreshData();
    }
    @Override public void refreshData() {
        int people = app.getBusiness().listPeople(app.getSessionToken()).size();
        long students = app.getBusiness().listPeople(app.getSessionToken()).stream()
                .filter(p -> p.getCategory() == Person.Category.STUDENT).count();
        long faculty = app.getBusiness().listPeople(app.getSessionToken()).stream()
                .filter(p -> p.getCategory() == Person.Category.FACULTY).count();
        totals.setText("" + people + " people     |     " + students + " students     |     " + faculty + " faculty");
    }
}
