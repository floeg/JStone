package de.loegler.jstone.client.gui;

import de.loegler.jstone.client.main.ClientBattle;
import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.Karte;
import de.loegler.schule.datenstrukturen.List;
import de.loegler.schule.datenstrukturen.Queue;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Darstellung des Spielfeldes.
 * Spieler 1 (der Gegner, oben) wird ggf. durch die Zahl 0 präsentiert, Spieler 2 als 1.
 */
public class BattlefieldPanel extends JPanel {

    private ClientBattle clientBattle;
    private KartenPanel currentSelected = null, currentHovered;
    private JButton currentHoveredHero;
    private Thread messageChannelThread;
    private ListX<KartenPanel> s1Hand, s2Hand;
    private Positionsverwalter positionsverwalter;
    private JLabel s1Name = new JLabel("Spieler123"), s2Name = new JLabel("Gegner123");
    /**
     * Knöpfe für Helden/Heldenfähigkeit
     */
    private JButton s1Held, s2Held, s1HeldenF, s2HeldenF;
    private JFrame parentFrame;
    /**
     * AktionsSchlange beinhaltet entweder Diener oder Aktionen welche zum aktuellen
     * Zeitpunkt nicht ausgeführt werden können --> So benötigt ein Diener (und
     * bestimmte Aktionen) ein Ziel welches der Spieler durch anklicken auswählen
     * kann
     */
    //Aus dem alten Projekt - ggf. entfernen?
    private Queue<Object> aktionsSchlange;
    /**
     * Die Manaleisten der Spieler --> Grafische Darstellung von spieler.getMana();
     */
    private JLabel[] manaleisten;
    private JButton zugEnde;
    private JLabel messageChannel;
    private KartenPanel[][] diener = new KartenPanel[2][7];

    public BattlefieldPanel(JFrame parentFrame) {
        this.setLayout(null);
        this.parentFrame = parentFrame;
        HeldMouseAdapter heldAdapter = new HeldMouseAdapter();
        positionsverwalter = new Positionsverwalter();
        s1Held = new JButton("S1Held");
        s1Held.setBounds(positionsverwalter.s1Held);
        s1Held.addMouseListener(heldAdapter);

        this.add(s1Held);
        s2Held = new JButton("S2Held");
        s2Held.setBounds(positionsverwalter.s2Held);
        this.s2Held.addMouseListener(heldAdapter);
        this.add(s2Held);
        this.zugEnde = new JButton("Zug beenden");
        zugEnde.setBounds(positionsverwalter.zugBeenden);
        add(zugEnde);
        zugEnde.setEnabled(false); //Wird bei TurnEvent des Spielers aktiviert
        zugEnde.addActionListener(e -> clientBattle.endTurnClient());
        manaleisten = new JLabel[2];
        manaleisten[0] = new JLabel();
        manaleisten[1] = new JLabel();
        changeMana(0, 0, 0);
        changeMana(0, 0, 1);
        manaleisten[0].setBounds(positionsverwalter.manaleisten[0]);
        manaleisten[1].setBounds(positionsverwalter.manaleisten[1]);
        add(manaleisten[0]);
        add(manaleisten[1]);
        positionsverwalter.s1Spielfeld.toFirst();
        s1Hand = new ListX<>();
        s2Hand = new ListX<>();
        messageChannel = new JLabel("Bitte wähle einen anderen Diener aus!");
        messageChannel.setVisible(false);
        messageChannel.setForeground(Color.DARK_GRAY);
        messageChannel.setBounds(new Rectangle(760, 470, 650, 20));
        this.add(messageChannel);
        this.s2Name.setBounds(816, 200, 150, 30);
        this.s1Name.setBounds(816, 770, 150, 30);
        this.add(this.s1Name);
        this.add(this.s2Name);
    }
    public void setClientBattle(ClientBattle clientBattle) {
        this.clientBattle = clientBattle;
    }

    public void onBFDienerSelected(KartenPanel diener) {
        currentSelected = diener;
    }

    public void onBFDienerReleased(KartenPanel diener) {
        if (currentHoveredHero != null) {
            Diener current = (Diener) currentSelected.getKarte();
            boolean canAttack = current.canAttackEnemyHero(clientBattle.getSchlachtfeld(), clientBattle.getGameNumber());
            if (canAttack) {
                clientBattle.requestAttackHero((Diener) currentSelected.getKarte());

            } else
                sendToMessageChannel("Das kann ich nicht machen!");

        } else if (currentHovered != null && currentHovered != currentSelected) {
            if (currentSelected.getKarte() instanceof Diener) {
                if (currentHovered.getKarte() instanceof Diener) {
                    boolean canAttack = ((Diener) currentSelected.getKarte()).canAttack(clientBattle.getSchlachtfeld(), (Diener) currentHovered.getKarte(), clientBattle.getGameNumber());

                    if (canAttack) {
                        clientBattle.requestAttackMinion((Diener) currentSelected.getKarte(), (Diener) currentHovered.getKarte());
                    }
                }
            }
        }
        currentSelected = null;
    }

    public void onBFDienerHovered(KartenPanel diener) {
        if (currentSelected != null) {
            if (zugEnde.isEnabled()) {
                Diener target = (Diener) diener.getKarte();
                Diener source = (Diener) currentSelected.getKarte();
                if (source.canAttack(clientBattle.getSchlachtfeld(), target, clientBattle.getGameNumber())) {
                    diener.setBorder(new LineBorder(Color.GREEN));
                    currentHovered = diener;
                    if (target.wouldDie(source)) {
                        diener.setBorder(new LineBorder(new Color(102, 0, 25), 2));
                    }
                    if (source.wouldDie(target))
                        currentSelected.setBorder(new LineBorder(new Color(102, 0, 25), 2));
                } else {
                    diener.setBorder(new LineBorder(Color.RED));
                }
            }
        }
    }


    public void onBFDienerLeft(KartenPanel diener) {
        diener.setBorder(null);
        currentHovered = null;
        if (currentSelected != null)
            currentSelected.setBorder(null);
    }

    /**
     * @param side  0: Gegner, 1: Du
     * @param karte
     */
    public void cardDrawn(int side, Karte karte) {
        List<Rectangle> pos;
        List<Rectangle> pos2;
        List<KartenPanel> hand;
        if (side == 0) {
            pos = positionsverwalter.s1Hand1;
            pos2 = positionsverwalter.s1Hand2;
            hand = s1Hand;
        } else {
            pos = positionsverwalter.s2Hand1;
            pos2 = positionsverwalter.s2Hand2;
            hand = s2Hand;
        }
        pos.concat(pos2);
        pos.toFirst();
        boolean found = false;
        for (hand.toFirst(); pos.hasAccess() && !found; hand.next(), pos.next()) {
            if (hand.getContent() == null) {
                KartenPanel toAdd = getK(pos, karte);
                hand.append(toAdd);
                add(toAdd);
                toAdd.revalidate();
                found = true;
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Sendet eine Nachricht an den MessageChannel des Spielers.
     *
     * @param message
     */
    public void sendToMessageChannel(String message) {
        messageChannel.setVisible(true);
        messageChannel.setText(message);
    }


    public void updateNames(String ownName, String otherName) {
        this.s1Name.setText(ownName);
        this.s2Name.setText(otherName);
    }


    public void changeEndTurnState(boolean enabled) {
        zugEnde.setEnabled(enabled);
    }

    /**
     * @param maxMana
     * @param currentMana
     * @param player      1: Der Client; 0: Der Gegner
     */
    public void changeMana(int maxMana, int currentMana, int player) {
        manaleisten[player].setText("Mana: " + currentMana + " / " + maxMana);
    }

    public void onHandCardClicked(KartenPanel kartenPanel) {
        int playerMana = clientBattle.getSchlachtfeld().getCurrentMana(clientBattle.getGameNumber());
        int dienerAmount = clientBattle.getSchlachtfeld().getDienerCount(clientBattle.getGameNumber());
        if (kartenPanel.getKarte().canPlay(playerMana, dienerAmount)) {
            int index = -1;
            int i = 0;
            for (s2Hand.toFirst(); s2Hand.hasAccess(); s2Hand.next(), i++) {
                if (s2Hand.getContent().equals(kartenPanel))
                    index = i;
            }
            if (index != -1) {
                clientBattle.requestCardPlay(index);

            }//Ansonsten: Karte des Gegners
        } else {
            sendToMessageChannel("Diese Karte kann ich nicht ausspielen!");
        }
    }


    public void zeichneHandNeu(Karte[] hand, int guiSide) {
        if (guiSide == 0) {
            s1Hand.forEach(this::remove);
        } else {
            s2Hand.forEach(this::remove);
        }
        List<Rectangle> posList;
        if (guiSide == 0) {
            posList = positionsverwalter.s1HandFull;
        } else {
            posList = positionsverwalter.s2HandFull;
        }
        ListX<KartenPanel> newHand = new ListX<>();
        posList.toFirst();
        for (int i = 0; i < hand.length && hand[i] != null; i++, posList.next()) {
            KartenPanel tmp = getK(posList, hand[i]);
            newHand.append(tmp);
            add(tmp);
        }
        if (guiSide == 0)
            s1Hand = newHand;
        else
            s2Hand = newHand;

        revalidate();
        repaint();
    }

    public void zeichneDienerNeu(ListX<Diener> newDiener, int guiSide) {
        List<Rectangle> pos;
        if (guiSide == 0) {
            pos = positionsverwalter.s1Spielfeld;
        } else {
            pos = positionsverwalter.s2Spielfeld;
        }
        for (int i = 0; i < diener[guiSide].length && diener[guiSide][i] != null; i++) {
            remove(diener[guiSide][i]);
            diener[guiSide][i] = null;
        }
        int i = 0;
        pos.toFirst();
        for (newDiener.toFirst(); newDiener.hasAccess(); newDiener.next(), pos.next(), i++) {
            KartenPanel k = getK(pos, newDiener.getContent());
            add(k);
            k.setCurrentPosition(k.BFIELD);
            diener[guiSide][i] = k;
            if (newDiener.getContent().isCanAttack() && clientBattle.isAmZug()) {
                k.setBorder(new LineBorder(Color.BLUE));
            }
        }
        revalidate();
        repaint();
    }

    private KartenPanel getK(List<Rectangle> pos, Karte content) {
        KartenPanel tmp= new KartenPanel(content, pos.getContent());
        tmp.getButton().addMouseListener(new PanelListener(tmp));
        return tmp;
    }

    public void updateHeroHealth(int guiNumber, int leben) {
        if (guiNumber == 0)
            s1Held.setText("S1 Leben: " + leben);
        else
            s2Held.setText("S2 Leben: " + leben);
    }


    private class HeldMouseAdapter extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (e.getSource() instanceof JButton) {
                currentHoveredHero = (JButton) e.getSource();
                if (BattlefieldPanel.this.currentSelected != null) {
                    boolean t = ((Diener) currentSelected.getKarte()).canAttackEnemyHero(clientBattle.getSchlachtfeld(), clientBattle.getGameNumber());
                    if (t) {
                        currentHoveredHero.setBorder(new LineBorder(Color.GREEN));
                    } else
                        currentHoveredHero.setBorder(new LineBorder(Color.RED));
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (e.getSource() instanceof JButton) {
                currentHoveredHero.setBorder(UIManager.getBorder("Button.border"));
                currentHoveredHero = null;
            }
        }
    }

    public class PanelListener extends MouseAdapter {
        private KartenPanel it;

        public PanelListener(KartenPanel it){
            this.it=it;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (SwingUtilities.isRightMouseButton(e)) {
                //Erfolgt im Panel, da unabhängig von this
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                if (it.currentPosition.equals(it.HAND)) {
                    onHandCardClicked(it);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
                if (it.currentPosition.equals(it.BFIELD)) {
                    onBFDienerSelected(it);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
                if (it.currentPosition.equals(it.BFIELD))
                    onBFDienerReleased(it);
            }


        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (it.currentPosition.equals(it.BFIELD)) {
                onBFDienerHovered(it);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (it.currentPosition.equals(it.BFIELD))
                onBFDienerLeft(it);
        }
    }
}

