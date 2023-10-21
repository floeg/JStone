package de.loegler.jstone.client.gui;

import de.loegler.jstone.core.Karte;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DeckCreatorGUI {
    private ListX<Karte> selectedCard = new ListX<>();
    private JPanel cardsPanel;
    private JScrollPane scrollPane;

    public DeckCreatorGUI() {
        cardsPanel = new JPanel(new GridLayout(0, 1));
        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setPreferredSize(new Dimension(150, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void addCard(KartenPanel selected) {
        selectedCard.append(selected.getKarte());
        KartenPanel selectedCopy = new KartenPanel(selected.getKarte());
        selectedCopy.setSize(100, 200);
        selectedCopy.setBorder(new EmptyBorder(220, 0, 0, 0));
        cardsPanel.add(selectedCopy);
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}
