package de.loegler.jstone.client.gui;

import de.loegler.jstone.client.main.JStoneClient;
import de.loegler.jstone.core.Karte;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackopeningPanel extends JPanel {
    private JPanel selectPackPanel;
    private JPanel cardsPanel;
    private JPanel topBar;
    private JStoneClient client;
    private Color defaultBackground;

    public PackopeningPanel(JStoneClient client) {
        this.client = client;
        selectPackPanel = new JPanel(new GridLayout(0, 1));
        setLayout(new BorderLayout(20, 350));
        cardsPanel = new JPanel();
        topBar = new JPanel();
        topBar.add(new JLabel("PackOpening - Verbessere deine Sammlung!"));
        JButton button = new JButton("Zurück zum Hauptmenu");
        button.addActionListener(e -> client.betreteMainMenu());
        topBar.add(button);
        add(topBar, BorderLayout.NORTH);
        selectPackPanel.add(new JLabel("Wähle deine Kartenpackung aus!"));
        add(selectPackPanel, BorderLayout.WEST);
        cardsPanel.add(new JLabel("Bitte wähle eine Kartenpackung aus."));
        add(cardsPanel, BorderLayout.CENTER);
        defaultBackground = getBackground();
    }

    public void setPackNames(String[] packungenNamen) {
        PackListener packListener = new PackListener();
        for (String packing : packungenNamen) {
            JButton button = new JButton(packing);
            button.addActionListener(packListener);
            this.selectPackPanel.add(button);
        }
        revalidate();
        repaint();
    }


    public void showPackOpening(ListX<Karte> cards) {
        cardsPanel.removeAll();
        cardsPanel.setLayout(new GridLayout(1, 0));
        int cCount = cards.calculateSize();
        cards.toFirst();
        for (int i = 0; i != cCount; i++, cards.next()) {
            KartenPanel kartenPanel = new KartenPanel(Karte.getEmptyCardForOpening());
            cardsPanel.add(kartenPanel, i);
            int finalI = i;
            Karte content = cards.getContent();
            kartenPanel.getButton().addActionListener(e -> {
                KartenPanel clickedCard = new KartenPanel(content);
                cardsPanel.remove(finalI);
                cardsPanel.add(clickedCard, finalI);
                System.out.println(clickedCard.getKarte().getSeltenheit());

                if (clickedCard.getKarte().getSeltenheit() == Karte.Seltenheit.LEGENDAER) {
                    clickedCard.getButton().setBackground(Color.YELLOW);
                    Color c = new Color(255, 219, 78);
                    showAnimation(c, 3);
                } else if (clickedCard.getKarte().getSeltenheit() == Karte.Seltenheit.EPISCH) {
                    clickedCard.getButton().setBackground(Color.MAGENTA);
                    showAnimation(Color.MAGENTA, 2);
                } else if (clickedCard.getKarte().getSeltenheit() == Karte.Seltenheit.SELTEN) {
                    clickedCard.getButton().setBackground(Color.BLUE);
                    showAnimation(Color.BLUE, 1);
                }
                revalidate();
                repaint();
            });
        }
        revalidate();
        repaint();
    }

    private void showAnimation(Color c, int rep) {
        //Animation Legendäre Karte
        new Thread(() -> {
            try {
                for (int k = 0; k != rep; k++) {
                    setBackground(c);
                    cardsPanel.setBackground(c);
                    selectPackPanel.setBackground(c);
                    topBar.setBackground(c);
                    revalidate();
                    repaint();
                    Thread.sleep(1200);
                    setBackground(defaultBackground);
                    cardsPanel.setBackground(defaultBackground);
                    selectPackPanel.setBackground(defaultBackground);
                    topBar.setBackground(defaultBackground);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }).start();
        //Animation Legendäre Karte
    }

    public class PackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton b = (JButton) e.getSource();
                String packSelected = b.getText();
                client.requestOpenPack(packSelected);
            }
        }
    }
}
