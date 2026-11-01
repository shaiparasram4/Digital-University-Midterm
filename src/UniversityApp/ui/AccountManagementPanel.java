package UniversityApp.ui;

import Business.Person.Person;
import Business.UserAccounts.UserAccount;
import UniversityApp.DigitalUniversityApp;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Admin-only user account management, including enable/disable and passwords.
 * Midterm: Shai Rashada-Parasram. */
public class AccountManagementPanel extends JPanel implements DigitalUniversityApp.Refreshable, DigitalUniversityApp.AdminOnlyPanel {
    private final DigitalUniversityApp app;
    private final DefaultTableModel model = UiKit.model("Username", "Person", "Role", "Enabled", "Last login", "Last modified");
    private final JTable table = new JTable(model);
    private final List<UserAccount> rows = new ArrayList<>();
    public AccountManagementPanel(DigitalUniversityApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        JPanel page = UiKit.page("User Account Administration", "Create, modify, enable, disable, and delete logins. Every action is role-checked.");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        toolbar.setOpaque(false);
        JButton create = UiKit.button("Create login");
        JButton edit = UiKit.button("Edit login");
        JButton remove = UiKit.button("Delete login");
        toolbar.add(create); toolbar.add(edit); toolbar.add(remove);
        JPanel center = new JPanel(new BorderLayout());
        center.add(toolbar, BorderLayout.NORTH);
        center.add(UiKit.table(table), BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        add(page);
        create.addActionListener(e -> editDialog(null));
        edit.addActionListener(e -> { UserAccount account = selected(); if (account != null) editDialog(account); });
        remove.addActionListener(e -> removeSelected());
        refreshData();
    }
    @Override public void refreshData() {
        rows.clear(); model.setRowCount(0);
        for (UserAccount user : app.getBusiness().listAccounts(app.getSessionToken())) {
            rows.add(user);
            model.addRow(new Object[]{user.getUserLoginName(), user.getAssociatedPersonProfile().getPerson().getFullName(),
                user.getRole(), user.isEnabled() ? "Yes" : "No", timestamp(user.getLastLoginAt()), timestamp(user.getLastUpdatedAt())});
        }
    }
    private String timestamp(long value) {
        return value <= 0 ? "Never" : DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(value));
    }
    private UserAccount selected() {
        int index = table.getSelectedRow();
        if (index < 0) { JOptionPane.showMessageDialog(this, "Select an account first."); return null; }
        return rows.get(table.convertRowIndexToModel(index));
    }
    private void editDialog(UserAccount account) {
        boolean creating = account == null;
        JComboBox<Person> person = new JComboBox<>();
        for (Person p : app.getBusiness().listPeople(app.getSessionToken())) {
            if (app.getBusiness().getUserAccountDirectory().findUserAccount(p.getPersonId()) == null) person.addItem(p);
        }
        JTextField username = UiKit.field(creating ? "" : account.getUserLoginName());
        JPasswordField password = new JPasswordField();
        JCheckBox enabled = new JCheckBox("Account enabled", creating || account.isEnabled());
        JPanel form = UiKit.form();
        if (creating) {
            UiKit.fieldRow(form, "Person without login:", person);
            if (person.getItemCount() == 0) {
                app.report(new IllegalArgumentException("Register a person without a login first."));
                return;
            }
        }
        UiKit.fieldRow(form, "Username:", username);
        UiKit.fieldRow(form, creating ? "Password (8+ chars):" : "New password (optional):", password);
        if (!creating) UiKit.fieldRow(form, "Status:", enabled);
        if (JOptionPane.showConfirmDialog(this, form, creating ? "Create login" : "Edit login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        char[] secret = password.getPassword();
        try {
            if (creating) {
                Person selectedPerson = (Person) person.getSelectedItem();
                if (selectedPerson == null) throw new IllegalArgumentException("Choose a person.");
                app.getBusiness().createAccount(app.getSessionToken(), selectedPerson.getPersonId(), username.getText(), new String(secret));
            } else {
                app.getBusiness().editAccount(app.getSessionToken(), account.getUserLoginName(), username.getText(),
                        new String(secret), enabled.isSelected());
            }
            refreshData();
            app.success(creating ? "Account created." : "Account updated.");
        } catch (Exception error) { app.report(error); }
        finally { java.util.Arrays.fill(secret, '\0'); }
    }
    private void removeSelected() {
        UserAccount account = selected();
        if (account == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete account '" + account.getUserLoginName() + "'?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            app.getBusiness().deleteAccount(app.getSessionToken(), account.getUserLoginName());
            refreshData();
            app.success("Account deleted. Its person record remains available.");
        } catch (Exception error) { app.report(error); }
    }
}
