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
            "Characters with Spear in Assasins Clan",
            "Dungeons and Characters Who Won",
            "Characters Who Completed All Quests",
            "Clans With Avg Level > 10",
            "Characters Who Lost But Level > 10",
            "Characters With >1 Weapon and >1 Mount",
            "Upcoming Events",
            "Most Popular Dungeon"
    };

    private final String[] queries = {
            "SELECT c.name AS character_name FROM `Character` c JOIN Clan cl ON c.clan_id = cl.clan_id WHERE cl.name = 'Assasins' AND c.character_id IN ( SELECT character_id FROM character_weapon cw JOIN Weapon w ON cw.weapon_id = w.weapon_id WHERE w.type = 'Spear')",

            "SELECT d.name AS dungeon_name FROM Runs r JOIN Dungeon d ON r.dungeon_id = d.dungeon_id WHERE r.result = 'Win'",

            "SELECT p.username FROM Player p JOIN `Character` c ON p.player_id = c.player_id WHERE NOT EXISTS ( SELECT q.quest_id FROM Quest q WHERE NOT EXISTS ( SELECT 1 FROM character_quest cq WHERE cq.character_id = c.character_id AND cq.quest_id = q.quest_id))",

            "SELECT cl.name AS clan_name FROM Clan cl JOIN `Character` c ON cl.clan_id = c.clan_id GROUP BY cl.clan_id HAVING AVG(c.level) > 10",

            "SELECT DISTINCT c.name FROM `Character` c JOIN Runs r ON c.character_id = r.character_id WHERE r.result = 'Lose' AND c.level > 10",

            "SELECT c.name FROM `Character` c JOIN character_weapon cw ON c.character_id = cw.character_id WHERE c.character_id IN ( SELECT cm.character_id FROM character_mounts cm GROUP BY cm.character_id HAVING COUNT(DISTINCT cm.mount_id) > 1) GROUP BY c.character_id HAVING COUNT(DISTINCT cw.weapon_id) > 1",

            "SELECT ge.title AS event_title FROM GuildEvent ge JOIN Clan cl ON ge.clan_id = cl.clan_id WHERE ge.date >= CURDATE() ORDER BY ge.date",

            "SELECT d.name AS dungeon_name FROM Runs r JOIN Dungeon d ON r.dungeon_id = d.dungeon_id GROUP BY d.dungeon_id ORDER BY COUNT(*) DESC LIMIT 1"
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

        try (Connection connection = DB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not run query", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
