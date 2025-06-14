import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CharacterInfoPanel extends JPanel {
    private int playerId;
    private int characterId;
    private JLabel nameLabel;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<CharacterItem> characterSelector;
    private JButton dungeonButton, mountButton, weaponButton, questButton, raceButton, clanButton;

    public CharacterInfoPanel(int playerId) {
        this.playerId = playerId;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.decode("#f4f4f4"));


        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameLabel = new JLabel("Character:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(nameLabel, BorderLayout.WEST);

        characterSelector = new JComboBox<>();
        characterSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        characterSelector.addActionListener(e -> updateSelectedCharacter());
        topPanel.add(characterSelector, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);


        model = new DefaultTableModel();
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(getBackground());

        dungeonButton = createButton("Dungeons");
        mountButton = createButton("Mounts");
        weaponButton = createButton("Weapons");
        questButton = createButton("Quests");
        raceButton = createButton("Race Info");
        clanButton = createButton("Clan Info");

        buttonPanel.add(dungeonButton);
        buttonPanel.add(mountButton);
        buttonPanel.add(weaponButton);
        buttonPanel.add(questButton);
        buttonPanel.add(raceButton);
        buttonPanel.add(clanButton);
        add(buttonPanel, BorderLayout.SOUTH);


        dungeonButton.addActionListener(e -> loadDungeons());
        mountButton.addActionListener(e -> loadMounts());
        weaponButton.addActionListener(e -> loadWeapons());
        questButton.addActionListener(e -> loadQuests());
        raceButton.addActionListener(e -> loadRaceInfo());
        clanButton.addActionListener(e -> loadClanInfo());

        loadCharactersForPlayer();
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void loadCharactersForPlayer() {
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                 SELECT character_id, name, level FROM `Character` WHERE player_id = ?
             """)) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("character_id");
                String name = rs.getString("name");
                int level = rs.getInt("level");
                characterSelector.addItem(new CharacterItem(id, name, level));
            }

            if (characterSelector.getItemCount() > 0) {
                characterSelector.setSelectedIndex(0);
                updateSelectedCharacter();
            } else {
                nameLabel.setText("No character found.");
                disableButtons();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSelectedCharacter() {
        CharacterItem selected = (CharacterItem) characterSelector.getSelectedItem();
        if (selected != null) {
            characterId = selected.id;
            nameLabel.setText("Character: " + selected.name + " (Level " + selected.level + ")");
        }
    }

    private void disableButtons() {
        dungeonButton.setEnabled(false);
        mountButton.setEnabled(false);
        weaponButton.setEnabled(false);
        questButton.setEnabled(false);
        raceButton.setEnabled(false);
        clanButton.setEnabled(false);
    }

    private void loadDungeons() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Dungeon");
        model.addColumn("Result");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT d.name, r.result
                FROM Runs r
                JOIN Dungeon d ON r.dungeon_id = d.dungeon_id
                WHERE r.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("name"), rs.getString("result")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMounts() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Type");
        model.addColumn("Speed");
        model.addColumn("Health");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT m.type, m.speed, m.health
                FROM character_mounts cm
                JOIN Mount m ON cm.mount_id = m.mount_id
                WHERE cm.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("type"),
                        rs.getInt("speed"),
                        rs.getInt("health")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWeapons() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Type");
        model.addColumn("Damage");
        model.addColumn("Weight");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT w.type, w.damage, w.weight
                FROM character_weapon cw
                JOIN Weapon w ON cw.weapon_id = w.weapon_id
                WHERE cw.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("type"),
                        rs.getInt("damage"),
                        rs.getInt("weight")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuests() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Quest Description");
        model.addColumn("XP");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT q.description, q.xp
                FROM character_quest cq
                JOIN Quest q ON cq.quest_id = q.quest_id
                WHERE cq.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("description"),
                        rs.getInt("xp")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRaceInfo() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Race");
        model.addColumn("Strength");
        model.addColumn("Intelligence");
        model.addColumn("Agility");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT r.name, r.strength, r.intelligence, r.agility
                FROM `Character` c
                JOIN Race r ON c.race_id = r.race_id
                WHERE c.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("strength"),
                        rs.getInt("intelligence"),
                        rs.getInt("agility")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClanInfo() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Clan");
        model.addColumn("Description");
        model.addColumn("Member Limit");

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                SELECT cl.name, cl.description, cl.member_limit
                FROM `Character` c
                JOIN Clan cl ON c.clan_id = cl.clan_id
                WHERE c.character_id = ?
            """)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("member_limit")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static class CharacterItem {
        int id;
        String name;
        int level;

        CharacterItem(int id, String name, int level) {
            this.id = id;
            this.name = name;
            this.level = level;
        }

        public String toString() {
            return name;
        }
    }


}
