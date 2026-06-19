import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class MemberFrame extends JFrame {
    private JTextField nameField, phoneField;
    private JTable table;
    private DefaultTableModel tableModel;

    public MemberFrame() {
        setTitle("Manage Members");
        setSize(650, 450);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(new Color(30, 30, 30));

        nameField  = addField(form, "Full Name:");
        phoneField = addField(form, "Cell Phone:");

        JButton addBtn = new JButton("Add Member");
        styleButton(addBtn);
        addBtn.addActionListener(e -> addMember());
        form.add(new JLabel());
        form.add(addBtn);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Full Name", "Cell Phone", "Subscribed"}, 0);
        table = new JTable(tableModel);
        styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(30, 30, 30));

        JButton subBtn = new JButton("Toggle Subscribe");
        styleButton(subBtn);
        subBtn.addActionListener(e -> toggleSubscription());
        btnPanel.add(subBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteMember());
        btnPanel.add(deleteBtn);

        main.add(form, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
        loadMembers();
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

    private void loadMembers() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Member");
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt("memberID"),
                    rs.getString("fullName"),
                    rs.getString("cellPhone"),
                    rs.getBoolean("subscribed") ? "Yes" : "No"
                });
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addMember() {
        String name  = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields."); return;
        }
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Member (fullName, cellPhone, subscribed) VALUES (?, ?, false)");
            stmt.setString(1, name); stmt.setString(2, phone);
            stmt.executeUpdate();
            conn.close();
            nameField.setText(""); phoneField.setText("");
            loadMembers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void toggleSubscription() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String current = (String) tableModel.getValueAt(row, 3);
        boolean nowSubscribed = current.equals("No"); // toggle
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Member SET subscribed = ? WHERE memberID = ?");
            stmt.setBoolean(1, nowSubscribed);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            conn.close();
            loadMembers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Member WHERE memberID = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.close();
            loadMembers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
