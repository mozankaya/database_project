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

        QueryPanel queryPanel = new QueryPanel();
        tabbedPane.addTab("SQL Queries", queryPanel);

        CharacterInfoPanel infoPanel = new CharacterInfoPanel(playerId);
        tabbedPane.addTab("My Characters", infoPanel);

        AddCharacterPanel addCharacterPanel = new AddCharacterPanel(playerId,infoPanel);
        tabbedPane.addTab("Add Character", addCharacterPanel);

        add(tabbedPane);
    }
}