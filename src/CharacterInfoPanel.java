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
    private JButton dungeonButton, mountButton, weaponButton, questButton ,raceButton, clanButton;


    public CharacterInfoPanel(int playerId) {
        this.playerId = playerId;
        setLayout(new BorderLayout());


        nameLabel = new JLabel("Character: ");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(nameLabel, BorderLayout.NORTH);


        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        dungeonButton = new JButton("Dungeons");
        mountButton = new JButton("Mounts");
        weaponButton = new JButton("Weapons");
        questButton = new JButton("Quests");
        raceButton = new JButton("Race Info");
        clanButton = new JButton("Clan Info");
        buttonPanel.add(dungeonButton);
        buttonPanel.add(mountButton);
        buttonPanel.add(weaponButton);
        buttonPanel.add(questButton);
        buttonPanel.add(raceButton);
        buttonPanel.add(clanButton);
        add(buttonPanel, BorderLayout.SOUTH);


        loadFirstCharacter();


        dungeonButton.addActionListener(e -> loadDungeons());
        mountButton.addActionListener(e -> loadMounts());
        weaponButton.addActionListener(e -> loadWeapons());
        questButton.addActionListener(e -> loadQuests());
        raceButton.addActionListener(e -> loadRaceInfo());
        clanButton.addActionListener(e -> loadClanInfo());
    }

    private void loadFirstCharacter() {
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
             SELECT character_id, name, level
             FROM `Character`
             WHERE player_id = ?
             LIMIT 1
         """)) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                characterId = rs.getInt("character_id");
                String name = rs.getString("name");
                int level = rs.getInt("level");

                nameLabel.setText("Character: " + name + " (Level " + level + ")");
            } else {
                nameLabel.setText("No character found.");
                disableButtons();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disableButtons() {
        dungeonButton.setEnabled(false);
        mountButton.setEnabled(false);
        weaponButton.setEnabled(false);
    }

    private void loadDungeons() {
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Dungeon");
        model.addColumn("Result");

        try (Connection connection = DB.getConnection();
             PreparedStatement ps = connection.prepareStatement("""
                SELECT d.name, r.result
                FROM Runs r
                JOIN Dungeon d ON r.dungeon_id = d.dungeon_id
                WHERE r.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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
             PreparedStatement ps = conn.prepareStatement("""
                SELECT m.type, m.speed, m.health
                FROM character_mounts cm
                JOIN Mount m ON cm.mount_id = m.mount_id
                WHERE cm.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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
             PreparedStatement ps = conn.prepareStatement("""
                SELECT w.type, w.damage, w.weight
                FROM character_weapon cw
                JOIN Weapon w ON cw.weapon_id = w.weapon_id
                WHERE cw.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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
             PreparedStatement ps = conn.prepareStatement("""
                SELECT q.description, q.xp
                FROM character_quest cq
                JOIN Quest q ON cq.quest_id = q.quest_id
                WHERE cq.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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
             PreparedStatement ps = conn.prepareStatement("""
                SELECT r.name, r.strength, r.intelligence, r.agility
                FROM `Character` c
                JOIN Race r ON c.race_id = r.race_id
                WHERE c.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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
             PreparedStatement ps = conn.prepareStatement("""
                SELECT cl.name, cl.description, cl.member_limit
                FROM `Character` c
                JOIN Clan cl ON c.clan_id = cl.clan_id
                WHERE c.character_id = ?
            """)) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
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



}
