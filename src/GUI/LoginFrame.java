package GUI;

import Resources.DB;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("MMORPG Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> login());

        setLayout(new GridLayout(3, 2,10,10));
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection connection = DB.getConnection()) {
            PreparedStatement login = connection.prepareStatement("SELECT login(?,?)");
            PreparedStatement getUser = connection.prepareStatement("SELECT * FROM player WHERE username = ? AND password = ?");
            login.setString(1, username);
            login.setString(2, password);
            getUser.setString(1, username);
            getUser.setString(2, password);

            ResultSet rs = login.executeQuery();
            if (rs.next() && rs.getBoolean(1)) {
                rs = getUser.executeQuery();
                if (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    JOptionPane.showMessageDialog(this, "Login successful!");

                    dispose();
                    new QueryFrame(playerId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
