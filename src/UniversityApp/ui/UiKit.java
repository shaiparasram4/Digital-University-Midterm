package UniversityApp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/** Consistent accessible, compact Swing styles. Shai Rashada-Parasram. */
public final class UiKit {
    public static final Color NAVY = new Color(20, 40, 64);
    public static final Color TEAL = new Color(0, 115, 122);
    public static final Color BG = new Color(245, 248, 252);
    public static final Color TEXT = new Color(33, 49, 68);
    private UiKit() { }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 23));
        label.setForeground(NAVY);
        return label;
    }
    public static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT);
        return label;
    }
    /**
     * Paint buttons ourselves so the native Windows look-and-feel cannot replace
     * the dark background with white while leaving the label white-on-white.
     * This applies to sidebar navigation and form actions throughout the app.
     * The hover, pressed, and disabled states retain legible contrast.
     * Midterm UI accessibility improvement: Shai Rashada-Parasram.
     */
    public static JButton button(String label) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D canvas = (Graphics2D) graphics.create();
                try {
                    canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    Color fill;
                    if (!isEnabled()) {
                        fill = new Color(91, 108, 120); // Muted but readable.
                    } else if (getModel().isPressed()) {
                        fill = new Color(0, 79, 86);
                    } else if (getModel().isRollover()) {
                        fill = new Color(0, 99, 108);
                    } else {
                        fill = TEAL;
                    }
                    canvas.setColor(fill);
                    canvas.fillRoundRect(1, 1, Math.max(0, getWidth() - 2),
                            Math.max(0, getHeight() - 2), 10, 10);
                } finally {
                    canvas.dispose();
                }
                // Swing still paints the text, focus and keyboard behavior.
                super.paintComponent(graphics);
            }
        };
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setRolloverEnabled(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 33));
        return button;
    }
    public static JPanel page(String heading, String explanation) {
        JPanel panel = new JPanel(new BorderLayout(12, 14));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.add(heading(heading));
        header.add(caption(explanation));
        panel.add(header, BorderLayout.NORTH);
        return panel;
    }
    public static DefaultTableModel model(String... names) {
        return new DefaultTableModel(names, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
    public static JScrollPane table(JTable table) {
        table.setRowHeight(27);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(228, 237, 245));
        return new JScrollPane(table);
    }
    public static JTextField field(String value) {
        JTextField input = new JTextField(value == null ? "" : value);
        input.setColumns(19);
        return input;
    }
    public static JPanel form() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return grid;
    }
    public static void fieldRow(JPanel grid, String name, java.awt.Component input) {
        grid.add(new JLabel(name, SwingConstants.RIGHT));
        grid.add(input);
    }
}
