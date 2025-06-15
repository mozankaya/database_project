package GUI;

import Resources.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QueryPanel extends JPanel {
    private JComboBox<String> querySelector;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    private final String[] queryTitles = {
            "Characters with Axe in Assasins Clan",
            "Dungeons that beaten by characters",
            "Characters Who Completed All Quests",
            "Clans With Avg Level > 10",
            "Characters Who Lost But Level > 10",
            "Characters With >1 Weapon and >1 Mount",
            "Upcoming Events",
            "Most Popular Dungeon",
            "Show All Loots",
            "Show All Bosses",
            "Easy Dungeons and Bosses",
            "Normal Dungeons and Bosses",
            "Hard Dungeons and Bosses"
    };

    private final String[] queries = {
            "CALL CharactersWithAxeInAssasinsClan()",
            "CALL DungeonsBeatenbyCharacters",
            "CALL CharactersCompletedAllQuests()",
            "CALL ClansWithAvgLevelGreaterThan10()",
            "CALL CharactersLostWithHighLevel()",
            "CALL CharactersWithManyWeaponsAndMounts()",
            "CALL UpcomingEvents()",
            "CALL MostPopularDungeon()",
            "CALL ShowAllLoots()",
            "CALL ShowAllBosses()",

            "SELECT d.name AS dungeon_name, d.difficulty, b.name AS boss_name " +
                    "FROM view_easy_dungeons d JOIN Boss b ON b.dungeon_id = d.dungeon_id",

            "SELECT d.name AS dungeon_name, d.difficulty, b.name AS boss_name " +
                    "FROM view_normal_dungeons d JOIN Boss b ON b.dungeon_id = d.dungeon_id",

            "SELECT d.name AS dungeon_name, d.difficulty, b.name AS boss_name " +
                    "FROM view_hard_dungeons d JOIN Boss b ON b.dungeon_id = d.dungeon_id"
    };

    public QueryPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        querySelector = new JComboBox<>(queryTitles);
        JButton runButton = new JButton("Run Query");
        topPanel.add(querySelector, BorderLayout.CENTER);
        topPanel.add(runButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        runButton.addActionListener(e -> runSelectedQuery());
    }

    private void runSelectedQuery() {
        int index = querySelector.getSelectedIndex();
        String sql = queries[index];

        // Eğer sorgu "CALL" ile başlıyorsa CallableStatement, değilse PreparedStatement kullan
        boolean isCallable = sql.trim().toUpperCase().startsWith("CALL");

        try (Connection connection = DB.getConnection()) {
            ResultSet rs;

            if (isCallable) {
                try (CallableStatement stmt = connection.prepareCall(sql)) {
                    rs = stmt.executeQuery();
                    populateTable(rs);
                }
            } else {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    rs = stmt.executeQuery();
                    populateTable(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not run query:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (int i = 1; i <= columnCount; i++) {
            tableModel.addColumn(meta.getColumnLabel(i));
        }

        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            tableModel.addRow(row);
        }
    }
}
