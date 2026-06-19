import java.awt.*;
import javax.swing.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame(String adminName) {
        setTitle("movies gallery");
        setSize(400, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("movies gallery", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(220, 180, 80));//gold
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        main.add(titleLabel, BorderLayout.NORTH);

        // buttons v
        JPanel btnPanel = new JPanel();//5 btn
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(new Color(30, 30, 30));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 10, 60));

        String[] labels = {"Movies", "Producers", "Categories", "Members", "Admins"};
        for (String label : labels) {
            JButton btn = makeMenuButton(label);
            btnPanel.add(btn);
            btnPanel.add(Box.createVerticalStrut(12));
        }

        main.add(btnPanel, BorderLayout.CENTER);

        //admin name + logout
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(20, 20, 20));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel adminLabel = new JLabel("Logged in as: " + adminName);
        adminLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        adminLabel.setForeground(Color.LIGHT_GRAY);
        bottom.add(adminLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(180, 60, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        logoutBtn.addActionListener(e -> {
            dispose();//close dashboard
            new LoginFrame().setVisible(true);
        });
        bottom.add(logoutBtn, BorderLayout.EAST);

        main.add(bottom, BorderLayout.SOUTH);
        add(main);
    }

    private JButton makeMenuButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(new Color(220, 180, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90), 1));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 5)
        ));

        switch (label) {
            case "Movies"     -> btn.addActionListener(e -> new MovieFrame().setVisible(true));
            case "Producers"  -> btn.addActionListener(e -> new ProducerFrame().setVisible(true));
            case "Categories" -> btn.addActionListener(e -> new CategoryFrame().setVisible(true));
            case "Members"    -> btn.addActionListener(e -> new MemberFrame().setVisible(true));
            case "Admins"     -> btn.addActionListener(e -> new AdminFrame().setVisible(true));
        }
        return btn;
    }
}
