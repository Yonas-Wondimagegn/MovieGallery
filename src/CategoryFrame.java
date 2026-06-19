import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class CategoryFrame extends JFrame {
    private JTextField nameField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CategoryFrame() {
        setTitle("Manage Categories");
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBackground(new Color(30, 30, 30));

        JLabel lbl = new JLabel("Category Name:");
        lbl.setForeground(Color.WHITE);
        form.add(lbl);
        nameField = new JTextField();
        form.add(nameField);

        JButton addBtn = new JButton("Add Category");
        styleButton(addBtn);
        addBtn.addActionListener(e -> addCategory());
        form.add(new JLabel());
        form.add(addBtn);

        tableModel = new DefaultTableModel(new String[]{"ID", "Category Name"}, 0);
        table = new JTable(tableModel);
        styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(30, 30, 30));
        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteCategory());
        btnPanel.add(deleteBtn);

        main.add(form, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
        loadCategories();
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

    private void loadCategories() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Category");
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt("categoryID"), rs.getString("categoryName")});
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a category name."); return; }
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Category (categoryName) VALUES (?)");
            stmt.setString(1, name);
            stmt.executeUpdate();
            conn.close();
            nameField.setText("");
            loadCategories();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Category WHERE categoryID = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.close();
            loadCategories();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
