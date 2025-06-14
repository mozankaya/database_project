import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QueryFrame extends JFrame {
    private int playerId;

    public QueryFrame(int playerId) {
        this.playerId = playerId;

        setTitle("MMORPG Player Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("SQL Queries", new QueryPanel());
        tabbedPane.addTab("My Characters", new CharacterInfoPanel(playerId));

        add(tabbedPane);
    }
}
