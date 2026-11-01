package UniversityApp.ui;

import Business.Person.Person;
import UniversityApp.DigitalUniversityApp;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Self-service profile editing for any authenticated role.
 * Midterm: Shai Rashada-Parasram. */
public class ProfilePanel extends JPanel implements DigitalUniversityApp.Refreshable {
    private final DigitalUniversityApp app;
    private final JLabel accountDetails = UiKit.caption("");
    private final JTextField name = UiKit.field("");
    private final JTextField email = UiKit.field("");
    private final JTextField details = UiKit.field("");
    private final JPasswordField newPassword = new JPasswordField();
    public ProfilePanel(DigitalUniversityApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        JPanel page = UiKit.page("My Profile", "Edit your own name, email, profile details, or password.");
        JPanel form = UiKit.form();
        UiKit.fieldRow(form, "Account:", accountDetails);
        UiKit.fieldRow(form, "Full name:", name);
        UiKit.fieldRow(form, "Email:", email);
        UiKit.fieldRow(form, "Program / title:", details);
        UiKit.fieldRow(form, "New password (optional):", newPassword);
        JPanel contents = new JPanel(new BorderLayout(6, 10));
        contents.setOpaque(false);
        contents.add(form, BorderLayout.NORTH);
        JPanel action = new JPanel(new FlowLayout(FlowLayout.LEFT));
        action.setOpaque(false);
        JButton save = UiKit.button("Save my profile");
        action.add(save);
        contents.add(action, BorderLayout.CENTER);
        page.add(contents, BorderLayout.CENTER);
        add(page);
        save.addActionListener(e -> {
            char[] secret = newPassword.getPassword();
            try {
                app.getBusiness().updateOwnProfile(app.getSessionToken(), name.getText(), email.getText(),
                        details.getText(), new String(secret));
                newPassword.setText("");
                app.success("Your profile has been updated.");
            } catch (Exception problem) { app.report(problem); }
            finally { java.util.Arrays.fill(secret, '\0'); }
        });
        refreshData();
    }
    @Override public void refreshData() {
        Person person = app.getBusiness().getMyPerson(app.getSessionToken());
        accountDetails.setText(person.getPersonId() + " / " + person.getCategory());
        name.setText(person.getFullName());
        email.setText(person.getEmail());
        details.setText(person.getDetail());
        newPassword.setText("");
    }
}
