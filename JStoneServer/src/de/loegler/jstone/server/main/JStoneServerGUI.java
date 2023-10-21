package de.loegler.jstone.server.main;

import de.loegler.core.gui.TextFrame;
import de.loegler.jstone.server.admin.gui.CardCreatorGUI;
import de.loegler.jstone.server.admin.gui.SQLSerialisierungGUI;
import de.loegler.jstone.server.admin.sql.CardCreator;
import de.loegler.jstone.server.admin.sql.SQLSerialisierung;

import javax.swing.*;
import java.awt.*;

public class JStoneServerGUI {
    private JStoneServer server;
    private JFrame frame;
    private JPanel panel;
    private JButton starteServer, starteCardCreator, starteAdminSerialisierung;

    public JStoneServerGUI() {
        frame = new JFrame("JFrame - Server");
        panel = new JPanel(new GridLayout(0, 2));
        starteServer = new JButton("Starte Server");
        starteServer.addActionListener(e -> this.starteServer());
        panel.add(starteServer);
        starteCardCreator = new JButton("Starte CardCreator");
        starteCardCreator.addActionListener(e -> this.starteCardCreator());

        starteAdminSerialisierung = new JButton("SQL-Serialisierung");
        starteAdminSerialisierung.addActionListener(e -> new SQLSerialisierungGUI(new SQLSerialisierung()));
        panel.add(starteCardCreator);
        panel.add(starteAdminSerialisierung);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 350);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Die Einstiegsmethode auf der Seite des Servers.
     *
     * @param args
     */
    public static void main(String[] args) {
        JStoneServerGUI gui = new JStoneServerGUI();
    }

    public void starteServer() {
        if (starteServer.getText().contains("Starte")) {
            starteServer.setText("Stoppe Server");
            server = new JStoneServer(9998);
            new TextFrame(new TextFrame.TextFrameOptions().enableAccept(), "Server gestartet!", "Server wurde gestartet!");
        } else {
            starteServer.setText("Starte Server");
            server.close();
            server = null;
        }
    }

    public void starteCardCreator() {
        CardCreator cardCreator = new CardCreator();
        CardCreatorGUI cardCreatorGUI = new CardCreatorGUI(cardCreator);
    }
}
