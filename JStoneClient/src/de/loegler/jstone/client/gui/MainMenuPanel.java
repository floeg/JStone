package de.loegler.jstone.client.gui;

import de.loegler.jstone.client.main.JStoneClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class MainMenuPanel extends JPanel {
    private JButton enterQueue, createDeck, openPack, selectDeck,openFriendFrame;
    private ActionListener listener;
    private JStoneClient client;

    public MainMenuPanel(JStoneClient client) {
        listener = new MainMenuListener();
        setLayout(new GridLayout(0, 1));
        enterQueue = createAndAdd("Betrete Warteschlange");
        createDeck = createAndAdd("Erstelle ein Deck");
        selectDeck = createAndAdd("Wähle dein Deck");
        openPack = createAndAdd("Öffne Kartenpackungen");
        openFriendFrame = createAndAdd("Öffne Freundschaftsfenster");
        this.client = client;
    }

    private JButton createAndAdd(String text) {
        JButton toReturn = new JButton(text);
        toReturn.addActionListener(listener);
        this.add(toReturn);
        return toReturn;
    }

    private class MainMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Random r = new Random();
            enterQueue.setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
            for (JButton jButton : Arrays.asList(openPack, createDeck, openFriendFrame, selectDeck)) {
                jButton.setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
            }
            if (e.getSource() == enterQueue) {
                client.betreteQueue();
            } else if (e.getSource() == createDeck) {
                client.oeffneCollection();
            } else if (e.getSource() == openPack) {
                client.oeffnePackungen();
            } else if (e.getSource() == selectDeck) {
                client.requestDeckCount();
            }
            else if(e.getSource() == openFriendFrame){
                client.onShowFriendFrame();
            }
        }
    }
}
