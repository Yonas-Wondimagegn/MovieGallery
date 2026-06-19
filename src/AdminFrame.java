import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class AdminFrame extends JFrame {
    private JTextField nameField, accountField, passwordField;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminFrame() {
        setTitle("Manage admins");
        setSize(650, 450);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBackground(new Color(30, 30, 30));

        nameField     = addField(form, "Full Name:");
        accountField  = addField(form, "User Account:");
        passwordField = addField(form, "Password:");

        JButton addBtn = new JButton("Add Admin");
        styleButton(addBtn);
        addBtn.addActionListener(e -> addAdmin());
        form.add(new JLabel());
        form.add(addBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Account"}, 0);
        table = new JTable(tableModel);
        styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(30, 30, 30));
        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteAdmin());
        btnPanel.add(deleteBtn);

        main.add(form, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
        loadAdmins();
    }

    private JTextField addField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        JTextField field = new JTextField();
        panel.add(field);
        return field;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(220, 180, 80));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(80, 80, 80));
        t.getTableHeader().setBackground(new Color(220, 180, 80));
        t.getTableHeader().setForeground(Color.BLACK);
        t.setRowHeight(25);
    }

    private void loadAdmins() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT adminID, fullName, userAccount FROM Admin");
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt("adminID"), rs.getString("fullName"), rs.getString("userAccount")});
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addAdmin() {
        String name     = nameField.getText().trim();
        String account  = accountField.getText().trim();
        String password = passwordField.getText().trim();
        if (name.isEmpty() || account.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields."); return;
        }
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Admin (fullName, userAccount, password) VALUES (?, ?, ?)");
            stmt.setString(1, name); stmt.setString(2, account); stmt.setString(3, password);
            stmt.executeUpdate();
            conn.close();
            nameField.setText(""); accountField.setText(""); passwordField.setText("");
            loadAdmins();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteAdmin() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Admin WHERE adminID = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.close();
            loadAdmins();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
