import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ProducerFrame extends JFrame {
    private JTextField nameField, phoneField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProducerFrame() {
        setTitle("Manage Producers");
        setSize(650, 500);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(new Color(30, 30, 30));

        nameField  = addField(form, "Full Name:");
        phoneField = addField(form, "Phone Number:");

        JButton addBtn = new JButton("Add Producer");
        styleButton(addBtn);
        addBtn.addActionListener(e -> addProducer());
        form.add(new JLabel());
        form.add(addBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Phone"}, 0);
        table = new JTable(tableModel);
        styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(30, 30, 30));
        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteProducer());
        btnPanel.add(deleteBtn);

        main.add(form, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
        loadProducers();
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

    private void loadProducers() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Producer");
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt("producerID"), rs.getString("fullName"), rs.getString("phoneNumber")});
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addProducer() {
        String name  = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "please fill all fields"); return;
        }
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Producer (fullName, phoneNumber) VALUES (?, ?)");
            stmt.setString(1, name); stmt.setString(2, phone);
            stmt.executeUpdate();
            conn.close();
            nameField.setText(""); phoneField.setText("");
            loadProducers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteProducer() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Producer WHERE producerID = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.close();
            loadProducers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
