import javax.swing.*;

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
        CharacterInfoPanel infoPanel = new CharacterInfoPanel(playerId);
        tabbedPane.addTab("My Characters", infoPanel);

        tabbedPane.addTab("Add Character", new AddCharacterPanel(playerId,infoPanel));

        add(tabbedPane);
    }
}