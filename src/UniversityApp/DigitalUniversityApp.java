package UniversityApp;

import Business.AuthService;
import Business.Business;
import Business.Person.Person;
import Business.ConfigureABusiness;
import Business.UserAccounts.UserAccount;
import UniversityApp.ui.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * INFO 5100 Digital University: login, session, role navigation, and CardLayout.
 * The UI is a new shell around the supplied Business/Profile/UserAccount code.
 * Implemented by Shai Rashada-Parasram.
 */
public final class DigitalUniversityApp extends JFrame {
    private final Business business;
    private final JPanel root = new JPanel(new CardLayout());
    private String sessionToken;

    public DigitalUniversityApp() { this(ConfigureABusiness.initialize()); }

    public DigitalUniversityApp(Business business) {
        super("Digital University | Access Control Midterm");
        this.business = business;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 650));
        setSize(1140, 770);
        setLocationRelativeTo(null);
        setContentPane(root);
        displayLogin();
    }

    public Business getBusiness() { return business; }
    public String getSessionToken() { return sessionToken; }

    /** Show validation and permission messages instead of crashing the EDT. */
    public void report(Exception problem) {
        JOptionPane.showMessageDialog(this, problem.getMessage(), "Action not completed",
                JOptionPane.WARNING_MESSAGE);
    }
    public void success(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setRoot(String name, JPanel panel) {
        root.removeAll();
        root.add(panel, name);
        ((CardLayout) root.getLayout()).show(root, name);
        root.revalidate();
        root.repaint();
    }

    public void displayLogin() {
        sessionToken = null;
        JPanel view = new JPanel(new java.awt.GridBagLayout());
        view.setBackground(UiKit.BG);
        JPanel card = new JPanel(new GridLayout(0, 1, 8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 225, 235)),
                BorderFactory.createEmptyBorder(30, 35, 30, 35)));
        card.setPreferredSize(new Dimension(410, 425));
        JLabel brand = UiKit.heading("Digital University System");
        JLabel note = UiKit.caption("INFO 5100  ·  Secure role-based access demo");
        card.add(brand);
        card.add(note);
        card.add(UiKit.caption("Username"));
        JTextField user = UiKit.field("");
        card.add(user);
        card.add(UiKit.caption("Password"));
        JPasswordField password = new JPasswordField();
        card.add(password);
        JButton login = UiKit.button("Sign in");
        card.add(login);
        card.add(UiKit.caption("Try admin / Admin123!"));
        card.add(UiKit.caption("Other demo accounts are listed in README.md"));
        login.addActionListener(e -> {
            char[] secret = password.getPassword();
            try {
                String token = business.getAuthService().login(user.getText().trim(), new String(secret));
                if (token == null) {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login denied", JOptionPane.WARNING_MESSAGE);
                    password.setText("");
                    return;
                }
                sessionToken = token;
                displayWorkspace();
            } finally { java.util.Arrays.fill(secret, '\0'); }
        });
        password.addActionListener(e -> login.doClick());
        view.add(card);
        setRoot("login", view);
        SwingUtilities.invokeLater(user::requestFocusInWindow);
    }

    /** Rebuild work areas for the authenticated role; no admin controls for others. */
    public void displayWorkspace() {
        UserAccount current;
        try { current = business.getAuthService().requireUser(sessionToken); }
        catch (SecurityException denied) { displayLogin(); return; }
        JPanel shell = new JPanel(new BorderLayout());
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UiKit.NAVY);
        banner.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JLabel title = new JLabel("DIGITAL UNIVERSITY  /  " + current.getRole().toUpperCase() + " WORKSPACE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        banner.add(title, BorderLayout.WEST);
        JLabel user = new JLabel("Signed in: " + current.getUserLoginName());
        user.setForeground(Color.WHITE);
        banner.add(user, BorderLayout.EAST);
        shell.add(banner, BorderLayout.NORTH);

        JPanel side = new JPanel(new GridLayout(0, 1, 0, 9));
        side.setBackground(UiKit.NAVY);
        side.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        JPanel sideContainer = new JPanel(new BorderLayout());
        sideContainer.setBackground(UiKit.NAVY);
        sideContainer.setPreferredSize(new Dimension(215, 0));
        sideContainer.add(side, BorderLayout.NORTH);
        JPanel body = new JPanel(new CardLayout());
        body.setBackground(UiKit.BG);

        if ("Admin".equalsIgnoreCase(current.getRole())) {
            // Server-side business operations independently check the admin role.
            addNav(side, body, "Dashboard", "overview", new OverviewPanel(this));
            addNav(side, body, "All People", "people", new PersonManagementPanel(this, null));
            addNav(side, body, "Students", "students", new PersonManagementPanel(this, Person.Category.STUDENT));
            addNav(side, body, "Faculty", "faculty", new PersonManagementPanel(this, Person.Category.FACULTY));
            addNav(side, body, "Academic Staff", "staff", new PersonManagementPanel(this, Person.Category.STAFF));
            addNav(side, body, "User Accounts", "accounts", new AccountManagementPanel(this));
            addNav(side, body, "Course Reference", "courses", new CourseReferencePanel(this));
        } else {
            addNav(side, body, "My Workspace", "my-workspace", new RoleWorkspacePanel(this));
            addNav(side, body, "Course Catalog", "courses", new CourseReferencePanel(this));
        }
        addNav(side, body, "My Profile", "profile", new ProfilePanel(this));
        JButton logout = UiKit.button("Log out");
        logout.addActionListener(e -> {
            business.getAuthService().logout(sessionToken);
            displayLogin();
        });
        side.add(logout);
        shell.add(sideContainer, BorderLayout.WEST);
        shell.add(body, BorderLayout.CENTER);
        setRoot("workspace", shell);
    }

    private void addNav(JPanel side, JPanel body, String label, String name, JPanel panel) {
        body.add(panel, name);
        JButton button = UiKit.button(label);
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.addActionListener(e -> {
            try {
                business.getAuthService().requireUser(sessionToken);
                if (panel instanceof AdminOnlyPanel) business.getAuthService().requireAdmin(sessionToken);
                if (panel instanceof Refreshable) ((Refreshable) panel).refreshData();
                ((CardLayout) body.getLayout()).show(body, name);
            } catch (Exception problem) {
                report(problem);
                if (problem instanceof SecurityException) displayLogin();
            }
        });
        side.add(button);
    }

    public interface Refreshable { void refreshData(); }
    public interface AdminOnlyPanel { }

    public static void main(String[] args) {
        try { javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { /* Platform look-and-feel is optional. */ }
        SwingUtilities.invokeLater(() -> new DigitalUniversityApp().setVisible(true));
    }
}
