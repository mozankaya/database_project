import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddCharacterPanel extends JPanel {
    private int playerId;
    private JTextField nameField;
    private JComboBox<String> raceComboBox;
    private JButton addButton;

    public AddCharacterPanel(int playerId) {
        this.playerId = playerId;
        setLayout(new GridLayout(4, 1, 10, 10));

        nameField = new JTextField();
        raceComboBox = new JComboBox<>();
        addButton = new JButton("Create Character");

        add(new JLabel("Character Name:"));
        add(nameField);
        add(new JLabel("Race:"));
        add(raceComboBox);
        add(addButton);

        loadRaceNames();

        addButton.addActionListener(e -> addCharacter());
    }

    private void loadRaceNames() {
        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM Race")) {
            while (rs.next()) {
                raceComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCharacter() {
        String name = nameField.getText().trim();
        String raceName = (String) raceComboBox.getSelectedItem();

        if (name.isEmpty() || raceName == null) {
            JOptionPane.showMessageDialog(this, "Please enter name and select race.");
            return;
        }

        try (Connection conn = DB.getConnection();
             PreparedStatement raceStmt = conn.prepareStatement("SELECT race_id FROM Race WHERE name = ?");
             PreparedStatement insertStmt = conn.prepareStatement("""
                 INSERT INTO `Character` (name, level, player_id, race_id, clan_id, health_point, mana_point)
                 VALUES (?, 1, ?, ?, NULL, 100, 100)
             """)) {

            raceStmt.setString(1, raceName);
            ResultSet rs = raceStmt.executeQuery();
            if (rs.next()) {
                int raceId = rs.getInt("race_id");

                insertStmt.setString(1, name);
                insertStmt.setInt(2, playerId);
                insertStmt.setInt(3, raceId);

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Character added!");

                }
            } else {
                JOptionPane.showMessageDialog(this, "Race not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding character.");
        }
    }
}