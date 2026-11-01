package UniversityApp.ui;

import Business.Person.Person;
import UniversityApp.DigitalUniversityApp;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Shared Admin CRUD screen for registering and updating all university people.
 * Students have a program/credits field; faculty have a title; staff an office.
 * Midterm implementation: Shai Rashada-Parasram.
 */
public class PersonManagementPanel extends JPanel implements DigitalUniversityApp.Refreshable, DigitalUniversityApp.AdminOnlyPanel {
    private final DigitalUniversityApp app;
    private final Person.Category filter;
    private final DefaultTableModel model = UiKit.model("ID", "Full name", "Email", "Role", "Department", "Program / title", "Credits");
    private final JTable table = new JTable(model);
    private final JTextField search = UiKit.field("");
    private final List<Person> rows = new ArrayList<>();

    public PersonManagementPanel(DigitalUniversityApp app, Person.Category filter) {
        this.app = app;
        this.filter = filter;
        setLayout(new BorderLayout());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {95, 175, 230, 95, 175, 185, 65};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        String title = filter == null ? "University People Directory" : filter == Person.Category.STUDENT ? "Student Records"
                : filter == Person.Category.FACULTY ? "Faculty Records" : filter == Person.Category.STAFF ? "Academic Staff Records" : "Administrator Records";
        JPanel page = UiKit.page(title, "Register, search, view, update and delete people. Changes stay in memory until restart.");
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        tools.setOpaque(false);
        tools.add(new JLabel("Search:"));
        tools.add(search);
        JButton add = UiKit.button("Register");
        JButton edit = UiKit.button("Edit selected");
        JButton delete = UiKit.button("Delete selected");
        tools.add(add); tools.add(edit); tools.add(delete);
        JPanel center = new JPanel(new BorderLayout());
        center.add(tools, BorderLayout.NORTH);
        center.add(UiKit.table(table), BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        add(page);
        add.addActionListener(e -> editDialog(null));
        edit.addActionListener(e -> {
            Person selected = selected();
            if (selected != null) editDialog(selected);
        });
        delete.addActionListener(e -> removeSelected());
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshData(); }
            public void removeUpdate(DocumentEvent e) { refreshData(); }
            public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        refreshData();
    }
    @Override public void refreshData() {
        model.setRowCount(0);
        rows.clear();
        String keyword = search.getText().trim().toLowerCase();
        for (Person person : app.getBusiness().listPeople(app.getSessionToken())) {
            if (filter != null && filter != person.getCategory()) continue;
            String haystack = (person.getPersonId() + " " + person.getFullName() + " " + person.getEmail()
                    + " " + person.getDepartment() + " " + person.getDetail()).toLowerCase();
            if (!haystack.contains(keyword)) continue;
            rows.add(person);
            model.addRow(new Object[]{person.getPersonId(), person.getFullName(), person.getEmail(),
                    person.getCategory(), person.getDepartment(), person.getDetail(), person.getCompletedCredits()});
        }
    }
    private Person selected() {
        int index = table.getSelectedRow();
        if (index < 0) { JOptionPane.showMessageDialog(this, "Select a person first."); return null; }
        return rows.get(table.convertRowIndexToModel(index));
    }
    private void editDialog(Person person) {
        boolean creating = person == null;
        JTextField id = UiKit.field(creating ? "" : person.getPersonId());
        id.setEditable(creating);
        JTextField name = UiKit.field(creating ? "" : person.getFullName());
        JTextField email = UiKit.field(creating ? "" : person.getEmail());
        JComboBox<Person.Category> category = new JComboBox<>(Person.Category.values());
        category.setSelectedItem(creating ? (filter == null ? Person.Category.STUDENT : filter) : person.getCategory());
        category.setEnabled(creating && filter == null);
        JTextField dept = UiKit.field(creating ? "Information Systems" : person.getDepartment());
        JTextField detail = UiKit.field(creating ? "" : person.getDetail());
        JSpinner credits = new JSpinner(new SpinnerNumberModel(creating ? 0 : person.getCompletedCredits(), 0, 300, 1));
        JPanel form = UiKit.form();
        UiKit.fieldRow(form, "Person ID:", id);
        UiKit.fieldRow(form, "Full name:", name);
        UiKit.fieldRow(form, "Email:", email);
        UiKit.fieldRow(form, "Role / type:", category);
        UiKit.fieldRow(form, "Department:", dept);
        UiKit.fieldRow(form, "Program / title:", detail);
        UiKit.fieldRow(form, "Student credits:", credits);
        if (JOptionPane.showConfirmDialog(this, form, creating ? "Register person" : "Edit person",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            if (creating) {
                app.getBusiness().registerPerson(app.getSessionToken(), id.getText(), name.getText(), email.getText(),
                        (Person.Category) category.getSelectedItem(), dept.getText(), detail.getText(), (Integer) credits.getValue());
            } else {
                app.getBusiness().editPerson(app.getSessionToken(), id.getText(), name.getText(), email.getText(),
                        dept.getText(), detail.getText(), (Integer) credits.getValue());
            }
            refreshData();
            app.success(creating ? "Person registered. You can now create their login under User Accounts." : "Person record updated.");
        } catch (Exception exception) { app.report(exception); }
    }
    private void removeSelected() {
        Person person = selected();
        if (person == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + person.getFullName() + " and any linked login account?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        try {
            app.getBusiness().deletePerson(app.getSessionToken(), person.getPersonId());
            refreshData();
            app.success("Person deleted.");
        } catch (Exception exception) { app.report(exception); }
    }
}
