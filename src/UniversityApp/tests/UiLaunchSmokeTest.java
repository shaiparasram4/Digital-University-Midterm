package UniversityApp.tests;

import UniversityApp.DigitalUniversityApp;
import java.awt.Component;
import java.awt.Container;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/** Interactive GUI smoke test for the NetBeans project, executed with Xvfb.
 * Written by Shai Rashada-Parasram. */
public final class UiLaunchSmokeTest {
    private static <T extends Component> T find(Container container, Class<T> type) {
        for (Component child : container.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container) {
                T found = find((Container) child, type);
                if (found != null) return found;
            }
        }
        return null;
    }
    private static JButton button(Container container, String text) {
        for (Component child : container.getComponents()) {
            if (child instanceof JButton && text.equals(((JButton)child).getText())) return (JButton)child;
            if (child instanceof Container) {
                JButton found = button((Container) child, text);
                if (found != null) return found;
            }
        }
        return null;
    }
    private static void assertPresent(Object item, String description) {
        if (item == null) throw new AssertionError(description + " is missing");
        System.out.println("UI PASS: " + description);
    }
    public static void main(String[] args) throws Exception {
        final DigitalUniversityApp[] app = new DigitalUniversityApp[1];
        SwingUtilities.invokeAndWait(() -> {
            app[0] = new DigitalUniversityApp();
            app[0].setVisible(true);
            JTextField login = find(app[0].getContentPane(), JTextField.class);
            JPasswordField pwd = find(app[0].getContentPane(), JPasswordField.class);
            JButton button = button(app[0].getContentPane(), "Sign in");
            assertPresent(login, "Login field");
            assertPresent(pwd, "Password field");
            assertPresent(button, "Sign in button");
            login.setText("admin"); pwd.setText("Admin123!"); button.doClick();
            assertPresent(button(app[0].getContentPane(), "User Accounts"), "Admin User Accounts navigation");
            assertPresent(button(app[0].getContentPane(), "Students"), "Admin Students navigation");
            assertPresent(button(app[0].getContentPane(), "Faculty"), "Admin Faculty navigation");
            assertPresent(button(app[0].getContentPane(), "My Profile"), "My Profile navigation");
            assertPresent(button(app[0].getContentPane(), "Log out"), "Logout button");
            button(app[0].getContentPane(), "User Accounts").doClick();
            assertPresent(button(app[0].getContentPane(), "Create login"), "Create account action");
            button(app[0].getContentPane(), "Students").doClick();
            assertPresent(button(app[0].getContentPane(), "Register"), "Register student action");
        });
        Thread.sleep(500);
        Robot robot = new Robot();
        Rectangle bounds = app[0].getBounds();
        java.awt.Point location = app[0].getLocationOnScreen();
        BufferedImage screenshot = robot.createScreenCapture(new Rectangle(location.x, location.y, bounds.width, bounds.height));
        File target = new File("Digital_University_Midterm_Preview.png");
        ImageIO.write(screenshot, "png", target);
        SwingUtilities.invokeAndWait(() -> {
            button(app[0].getContentPane(), "Log out").doClick();
            assertPresent(button(app[0].getContentPane(), "Sign in"), "Login screen after logout");
            app[0].dispose();
        });
        System.out.println("ALL UI SMOKE TESTS PASSED");
    }
}
