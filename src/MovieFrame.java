import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class MovieFrame extends JFrame {

    // Form fields
    private JTextField movieCodeField, titleField, lengthField, actorsField;
    private JTextField fullContentField, highlight1Field, highlight2Field;
    private JComboBox<String> typeCombo, producerCombo, categoryCombo;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;
    private java.util.List<Integer> producerIDs  = new ArrayList<>();
    private java.util.List<Integer> categoryIDs  = new ArrayList<>();

    public MovieFrame() {
        setTitle("Manage Movies");
        setSize(700, 750);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Admin - Manage Movies", buildAdminPanel());
        tabs.addTab("Member Gallery View",   buildGalleryPanel());
        add(tabs);

        loadProducers();
        loadCategories();
        loadMovies();
    }

    // ══════════════════════════════════════════════════
    //  TAB 1 – Admin panel: form on top, table below
    // ══════════════════════════════════════════════════
    private JPanel buildAdminPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ── Form (vertical, line by line) ──
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(new Color(30, 30, 30));

        movieCodeField   = addRow(form, "Movie ID (e.g. M001):");
        titleField       = addRow(form, "Title:");
        lengthField      = addRow(form, "Length (hours):");
        actorsField      = addRow(form, "Number of Actors:");

        addComboRow(form, "Type:");
        typeCombo = new JComboBox<>(new String[]{
            "Adventure","Romantic","Comedy","Action","Drama","Horror","Sci-Fi"});
        styleCombo(typeCombo);
        form.add(typeCombo);
        form.add(Box.createVerticalStrut(8));

        addComboRow(form, "Producer:");
        producerCombo = new JComboBox<>();
        styleCombo(producerCombo);
        form.add(producerCombo);
        form.add(Box.createVerticalStrut(8));

        addComboRow(form, "Category:");
        categoryCombo = new JComboBox<>();
        styleCombo(categoryCombo);
        form.add(categoryCombo);
        form.add(Box.createVerticalStrut(8));

        fullContentField  = addRow(form, "Full Content Quality (e.g. 1080p):");
        highlight1Field   = addRow(form, "Highlight 1 (trailer description):");
        highlight2Field   = addRow(form, "Highlight 2 (behind the scenes):");

        JButton addBtn = new JButton("Add Movie");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        styleButton(addBtn, new Color(60, 140, 60));
        addBtn.addActionListener(e -> addMovie());
        form.add(Box.createVerticalStrut(10));
        form.add(addBtn);

        // ── Table ──
        tableModel = new DefaultTableModel(
            new String[]{"ID","Code","Title","Type","Length","Actors","Producer","Category"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 200));

        // ── Bottom buttons ──
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setBackground(new Color(30, 30, 30));

        JButton playBtn = new JButton("Play Full Version");
        styleButton(playBtn, new Color(50, 100, 180));
        playBtn.addActionListener(e -> playFullVersion());
        bottom.add(playBtn);

        JButton delBtn = new JButton("Delete Selected");
        styleButton(delBtn, new Color(180, 60, 60));
        delBtn.addActionListener(e -> deleteMovie());
        bottom.add(delBtn);

        main.add(form,   BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        return main;
    }

    // ══════════════════════════════════════════════════
    //  TAB 2 – Member gallery: read-only poster tiles
    // ══════════════════════════════════════════════════
    private JPanel buildGalleryPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setBackground(new Color(20, 20, 20));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Member selector
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(new Color(20, 20, 20));

        JLabel lbl = new JLabel("Select Member:");
        lbl.setForeground(Color.WHITE);
        topBar.add(lbl);

        JComboBox<String> memberCombo = new JComboBox<>();
        java.util.List<Integer> memberIDs = new ArrayList<>();
        memberCombo.setPreferredSize(new Dimension(200, 28));
        topBar.add(memberCombo);

        JButton loadBtn = new JButton("Load Gallery");
        styleButton(loadBtn, new Color(220, 180, 80));
        loadBtn.setForeground(Color.BLACK);
        topBar.add(loadBtn);

        wrapper.add(topBar, BorderLayout.NORTH);

        // Tile area
        JPanel tilesPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        tilesPanel.setBackground(new Color(20, 20, 20));
        JScrollPane scroll = new JScrollPane(tilesPanel);
        scroll.getViewport().setBackground(new Color(20, 20, 20));
        scroll.setBorder(null);
        wrapper.add(scroll, BorderLayout.CENTER);

        // Load members into combo on first show
        loadMembersIntoCombo(memberCombo, memberIDs);

        loadBtn.addActionListener(e -> {
            if (memberCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(wrapper, "No members found. Add members first.");
                return;
            }
            int idx = memberCombo.getSelectedIndex();
            if (idx < 0) return;
            int memberID = memberIDs.get(idx);
            buildTiles(tilesPanel, memberID, scroll);
        });

        return wrapper;
    }

    private void loadMembersIntoCombo(JComboBox<String> combo, java.util.List<Integer> ids) {
        combo.removeAllItems(); ids.clear();
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT memberID, fullName FROM Member ORDER BY fullName");
            while (rs.next()) { ids.add(rs.getInt("memberID")); combo.addItem(rs.getString("fullName")); }
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void buildTiles(JPanel tilesPanel, int memberID, JScrollPane scroll) {
        tilesPanel.removeAll();

        // Get member info
        boolean subscribed = false; String memberName = "";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(
                "SELECT fullName, subscribed FROM Member WHERE memberID = ?");
            s.setInt(1, memberID);
            ResultSet rs = s.executeQuery();
            if (rs.next()) { subscribed = rs.getBoolean("subscribed"); memberName = rs.getString("fullName"); }
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); return; }

        final boolean isSub   = subscribed;
        final String  memName = memberName;

        // Status label
        JLabel statusLbl = new JLabel("  " + memName + " — " + (isSub ? "Subscribed" : "Not Subscribed"));
        statusLbl.setForeground(isSub ? new Color(80, 200, 80) : new Color(220, 100, 100));
        statusLbl.setFont(new Font("Arial", Font.BOLD, 13));
        tilesPanel.add(statusLbl);

        // Filler to push tiles to next row
        for (int i = 0; i < 20; i++) {
            JLabel spacer = new JLabel();
            spacer.setPreferredSize(new Dimension(0, 0));
            tilesPanel.add(spacer);
        }

        // Load movies with content
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT m.movieID, m.movieCode, m.title, m.type, " +
                "c.fullVersion, c.highlight1, c.highlight2 " +
                "FROM Movie m LEFT JOIN MovieContent c ON m.movieID = c.movieID");

            while (rs.next()) {
                int    movieID   = rs.getInt("movieID");
                String code      = rs.getString("movieCode");
                String title     = rs.getString("title");
                String type      = rs.getString("type");
                String fullVer   = rs.getString("fullVersion");
                String hl1       = rs.getString("highlight1");
                String hl2       = rs.getString("highlight2");
                if (fullVer == null) fullVer = "1080p";
                if (hl1     == null) hl1 = "Official Trailer";
                if (hl2     == null) hl2 = "Behind the Scenes";

                tilesPanel.add(buildTile(memberID, movieID, code, title, type,
                    fullVer, hl1, hl2, isSub, memName, tilesPanel, scroll));
            }
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }

        tilesPanel.revalidate();
        tilesPanel.repaint();
        scroll.revalidate();
    }

    private JPanel buildTile(int memberID, int movieID, String code, String title, String type,
                              String fullVer, String hl1, String hl2,
                              boolean isSub, String memName,
                              JPanel tilesPanel, JScrollPane scroll) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(40, 40, 40));
        card.setBorder(BorderFactory.createLineBorder(new Color(65, 65, 65)));
        card.setPreferredSize(new Dimension(200, 290));

        // Poster
        JLabel poster = new JLabel("", SwingConstants.CENTER);
        poster.setPreferredSize(new Dimension(200, 195));
        poster.setOpaque(true);
        poster.setBackground(new Color(55, 55, 55));

        java.io.File img = new java.io.File("src/pics/" + code + ".jpg");
        if (img.exists()) {
            Image scaled = new ImageIcon(img.getPath()).getImage()
                .getScaledInstance(200, 195, Image.SCALE_SMOOTH);
            poster.setIcon(new ImageIcon(scaled));
        } else {
            poster.setText("<html><center><font color='gray'>" + code + ".jpg<br>not found</font></center></html>");
        }
        card.add(poster, BorderLayout.CENTER);

        // Info + buttons
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(new Color(40, 40, 40));
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 12));
        bottom.add(titleLbl);

        JLabel typeLbl = new JLabel(type + "  [" + code + "]");
        typeLbl.setForeground(new Color(150, 150, 150));
        typeLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        bottom.add(typeLbl);

        bottom.add(Box.createVerticalStrut(5));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 5, 0));
        btnRow.setBackground(new Color(40, 40, 40));

        JButton watchBtn = new JButton("Watch");
        watchBtn.setFont(new Font("Arial", Font.BOLD, 11));
        watchBtn.setBackground(isSub ? new Color(50, 140, 50) : new Color(160, 70, 70));
        watchBtn.setForeground(Color.WHITE);
        watchBtn.setFocusPainted(false);

        final String fv = fullVer, h1 = hl1, h2 = hl2;
        watchBtn.addActionListener(e -> {
            if (isSub) {
                System.out.println(title + " now playing with " + fv + " quality");
                JOptionPane.showMessageDialog(this,
                    title + " now playing with " + fv + " quality",
                    "Now Playing", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("playing the highlight (trailer)");
                JOptionPane.showMessageDialog(this,
                    "playing the highlight (trailer)\n\nHighlight 1: " + h1 + "\nHighlight 2: " + h2,
                    "Highlight / Trailer", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton reviewBtn = new JButton("Review");
        reviewBtn.setFont(new Font("Arial", Font.BOLD, 11));
        reviewBtn.setBackground(new Color(220, 180, 80));
        reviewBtn.setForeground(Color.BLACK);
        reviewBtn.setFocusPainted(false);
        reviewBtn.addActionListener(e -> {
            if (!isSub) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "You are not subscribed.\nSubscribe now to watch full movies and write reviews?",
                    "Subscribe", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION)
                    subscribeNow(memberID, memName, tilesPanel, scroll);
            } else {
                openReviewDialog(memberID, movieID, title, memName);
            }
        });

        btnRow.add(watchBtn);
        btnRow.add(reviewBtn);
        bottom.add(btnRow);

        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void subscribeNow(int memberID, String name, JPanel tilesPanel, JScrollPane scroll) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(
                "UPDATE Member SET subscribed = true WHERE memberID = ?");
            s.setInt(1, memberID); s.executeUpdate(); conn.close();
            JOptionPane.showMessageDialog(this, name + " is now subscribed!");
            buildTiles(tilesPanel, memberID, scroll);
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void openReviewDialog(int memberID, int movieID, String movieTitle, String memberName) {
        JDialog dlg = new JDialog(this, "Reviews: " + movieTitle, true);
        dlg.setSize(500, 430);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.getContentPane().setBackground(new Color(30, 30, 30));

        JTextArea existing = new JTextArea();
        existing.setEditable(false);
        existing.setBackground(new Color(45, 45, 45));
        existing.setForeground(Color.WHITE);
        existing.setFont(new Font("Arial", Font.PLAIN, 13));
        existing.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        existing.setWrapStyleWord(true);
        existing.setLineWrap(true);

        StringBuilder sb = new StringBuilder();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement s = conn.prepareStatement(
                "SELECT r.reviewText, r.reviewDate, m.fullName " +
                "FROM Review r JOIN Member m ON r.memberID = m.memberID " +
                "WHERE r.movieID = ? ORDER BY r.reviewDate DESC");
            s.setInt(1, movieID); ResultSet rs = s.executeQuery();
            while (rs.next())
                sb.append("[").append(rs.getString("reviewDate")).append("] ")
                  .append(rs.getString("fullName")).append(":\n")
                  .append(rs.getString("reviewText")).append("\n\n");
            conn.close();
        } catch (SQLException e) { sb.append("Error: ").append(e.getMessage()); }
        existing.setText(sb.length() > 0 ? sb.toString() : "No reviews yet.");
        dlg.add(new JScrollPane(existing), BorderLayout.CENTER);

        JPanel writePanel = new JPanel(new BorderLayout(5, 5));
        writePanel.setBackground(new Color(30, 30, 30));
        writePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel wlbl = new JLabel("Write your review (" + memberName + "):");
        wlbl.setForeground(Color.LIGHT_GRAY);
        writePanel.add(wlbl, BorderLayout.NORTH);

        JTextArea input = new JTextArea(3, 38);
        input.setBackground(new Color(55, 55, 55));
        input.setForeground(Color.WHITE); input.setCaretColor(Color.WHITE);
        input.setLineWrap(true); input.setWrapStyleWord(true);
        writePanel.add(new JScrollPane(input), BorderLayout.CENTER);

        JButton submit = new JButton("Submit Review");
        submit.setBackground(new Color(220, 180, 80));
        submit.setForeground(Color.BLACK);
        submit.setFont(new Font("Arial", Font.BOLD, 13));
        submit.setFocusPainted(false);
        submit.addActionListener(e -> {
            String txt = input.getText().trim();
            if (txt.isEmpty()) { JOptionPane.showMessageDialog(dlg, "Please write a review first."); return; }
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement s = conn.prepareStatement(
                    "INSERT INTO Review (memberID, movieID, reviewText) VALUES (?, ?, ?)");
                s.setInt(1, memberID); s.setInt(2, movieID); s.setString(3, txt);
                s.executeUpdate(); conn.close();
                JOptionPane.showMessageDialog(dlg, "Review submitted!");
                dlg.dispose();
            } catch (SQLException ex) { JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage()); }
        });
        writePanel.add(submit, BorderLayout.SOUTH);
        dlg.add(writePanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════
    //  DB helpers
    // ══════════════════════════════════════════════════
    private void loadProducers() {
        producerCombo.removeAllItems(); producerIDs.clear();
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Producer");
            while (rs.next()) { producerIDs.add(rs.getInt("producerID")); producerCombo.addItem(rs.getString("fullName")); }
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void loadCategories() {
        categoryCombo.removeAllItems(); categoryIDs.clear();
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Category");
            while (rs.next()) { categoryIDs.add(rs.getInt("categoryID")); categoryCombo.addItem(rs.getString("categoryName")); }
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT m.movieID, m.movieCode, m.title, m.type, m.lengthHours, " +
                "m.numberOfActors, p.fullName, c.categoryName " +
                "FROM Movie m " +
                "JOIN Producer p ON m.producerID = p.producerID " +
                "JOIN Category c ON m.categoryID = c.categoryID");
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt("movieID"), rs.getString("movieCode"), rs.getString("title"),
                    rs.getString("type"), rs.getFloat("lengthHours"), rs.getInt("numberOfActors"),
                    rs.getString("fullName"), rs.getString("categoryName")});
            conn.close();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addMovie() {
        String code      = movieCodeField.getText().trim();
        String title     = titleField.getText().trim();
        String lenStr    = lengthField.getText().trim();
        String actStr    = actorsField.getText().trim();
        String type      = (String) typeCombo.getSelectedItem();
        String quality   = fullContentField.getText().trim();
        String hl1       = highlight1Field.getText().trim();
        String hl2       = highlight2Field.getText().trim();

        if (code.isEmpty() || title.isEmpty() || lenStr.isEmpty() || actStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Movie ID, Title, Length and Actors are required."); return; }
        if (!code.matches("M\\d+")) {
            JOptionPane.showMessageDialog(this, "Movie ID must be M followed by digits, e.g. M001"); return; }
        if (producerCombo.getItemCount() == 0 || categoryCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Add at least one Producer and Category first."); return; }

        try {
            float len  = Float.parseFloat(lenStr);
            int   acts = Integer.parseInt(actStr);
            int   pID  = producerIDs.get(producerCombo.getSelectedIndex());
            int   cID  = categoryIDs.get(categoryCombo.getSelectedIndex());

            Connection conn = DBConnection.getConnection();
            PreparedStatement chk = conn.prepareStatement("SELECT movieID FROM Movie WHERE movieCode=?");
            chk.setString(1, code);
            if (chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Movie ID " + code + " already exists."); conn.close(); return; }

            PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO Movie (movieCode,title,type,lengthHours,numberOfActors,producerID,categoryID) VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ins.setString(1,code); ins.setString(2,title); ins.setString(3,type);
            ins.setFloat(4,len); ins.setInt(5,acts); ins.setInt(6,pID); ins.setInt(7,cID);
            ins.executeUpdate();

            ResultSet keys = ins.getGeneratedKeys();
            if (keys.next()) {
                int newID = keys.getInt(1);
                if (quality.isEmpty()) quality = "1080p";
                if (hl1.isEmpty()) hl1 = "Official Trailer";
                if (hl2.isEmpty()) hl2 = "Behind the Scenes";
                PreparedStatement cnt = conn.prepareStatement(
                    "INSERT INTO MovieContent (movieID,fullVersion,highlight1,highlight2) VALUES(?,?,?,?)");
                cnt.setInt(1,newID); cnt.setString(2,quality); cnt.setString(3,hl1); cnt.setString(4,hl2);
                cnt.executeUpdate();
                System.out.println(title + " now playing with " + quality + " quality");
            }
            conn.close();

            for (JTextField f : new JTextField[]{movieCodeField,titleField,lengthField,actorsField,
                                                  fullContentField,highlight1Field,highlight2Field})
                f.setText("");
            loadMovies();
            JOptionPane.showMessageDialog(this, "Movie added! Poster: src/pics/" + code + ".jpg");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Length must be a decimal, Actors must be a whole number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void playFullVersion() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a movie row first."); return; }
        String title  = (String) tableModel.getValueAt(row, 2);
        int    mID    = (int)    tableModel.getValueAt(row, 0);
        String quality = "1080p";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement s = conn.prepareStatement("SELECT fullVersion FROM MovieContent WHERE movieID=?");
            s.setInt(1, mID); ResultSet rs = s.executeQuery();
            if (rs.next() && rs.getString("fullVersion") != null) quality = rs.getString("fullVersion");
            conn.close();
        } catch (SQLException ignored) {}
        System.out.println(title + " now playing with " + quality + " quality");
        JOptionPane.showMessageDialog(this, title + " now playing with " + quality + " quality",
            "Now Playing", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteMovie() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Connection conn = DBConnection.getConnection();
            conn.createStatement().executeUpdate("DELETE FROM Review       WHERE movieID=" + id);
            conn.createStatement().executeUpdate("DELETE FROM MovieContent WHERE movieID=" + id);
            conn.createStatement().executeUpdate("DELETE FROM Movie        WHERE movieID=" + id);
            conn.close(); loadMovies();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════
    //  Style helpers
    // ══════════════════════════════════════════════════
    /** Adds a label + text field as one vertical row and returns the field. */
    private JTextField addRow(JPanel panel, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(2));

        JTextField field = new JTextField();
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        field.setBackground(new Color(55, 55, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90)),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        panel.add(field);
        panel.add(Box.createVerticalStrut(8));
        return field;
    }

    private void addComboRow(JPanel panel, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(2));
    }

    private void styleCombo(JComboBox<?> c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        c.setBackground(new Color(55, 55, 55));
        c.setForeground(Color.WHITE);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(70, 70, 70));
        t.getTableHeader().setBackground(new Color(220, 180, 80));
        t.getTableHeader().setForeground(Color.BLACK);
        t.setRowHeight(24);
        t.setSelectionBackground(new Color(70, 110, 160));
    }
}
