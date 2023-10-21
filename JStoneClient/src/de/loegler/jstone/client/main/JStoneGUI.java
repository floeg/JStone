package de.loegler.jstone.client.main;

import de.loegler.jstone.client.gui.*;
import de.loegler.jstone.core.event.ChangeStateEvent;

import javax.swing.*;

/**
 * Die MainGUI - wird nach der Serverauswahl & dem Login angezeigt
 */
public class JStoneGUI {
    private JFrame frame;
    private MainMenuPanel mainMenuPanel;
    private JStoneClient client;
    private DeckManagerGUI deckGUI;
    private PackopeningPanel packopeningPanel;


    public JStoneGUI(JStoneClient client) {
        frame = new JFrame("JStone");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setSize(1900, 1050);
        frame.setLocationRelativeTo(null);
        this.client = client;
        frame.setVisible(true);
    }

    public BattlefieldPanel getBattlefieldPanel() {
        if (frame.getContentPane() instanceof BattlefieldPanel)
            return (BattlefieldPanel) frame.getContentPane();
        return null;
    }

    public void changeState(String state) {
        switch (state) {
            case ChangeStateEvent
                    .MAINMENU: {
                mainMenuPanel = new MainMenuPanel(this.client);
                frame.setContentPane(mainMenuPanel);
            }
            break;
            case ChangeStateEvent.FIGHTQUEUE: {
                JPanel tmpPanel = new JPanel();
                tmpPanel.add(new JLabel("FightQueue, bitte warten. Wartespiel wird ergänzt."));
                frame.setContentPane(tmpPanel);
            }
            break;
            case ChangeStateEvent.BATTLEFIELD: {
                frame.setContentPane(new BattlefieldPanel(this.frame));
            }
            break;
            case ChangeStateEvent.COLLECTION: {
                deckGUI = new DeckManagerGUI(client);
                frame.setContentPane(deckGUI);


            }
            break;
            case ChangeStateEvent.PACKOPENING: {
                this.packopeningPanel = new PackopeningPanel(client);
                frame.setContentPane(packopeningPanel);
            }
            break;
        }
        frame.revalidate();
    }


    public DeckManagerGUI getDeckGUI() {
        return deckGUI;
    }

    public PackopeningPanel getPackopeningPanel() {
        return packopeningPanel;
    }
}

