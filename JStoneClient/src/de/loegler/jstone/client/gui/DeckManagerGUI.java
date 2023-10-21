package de.loegler.jstone.client.gui;

import de.loegler.jstone.client.main.JStoneClient;
import de.loegler.jstone.core.CollectionKarte;
import de.loegler.jstone.core.event.AddCardToDeckEvent;
import de.loegler.jstone.core.event.RespondCollection;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import java.awt.*;

/**
 * Menu des Punktes Collection
 */
public class DeckManagerGUI extends JPanel {
    private DeckCreatorGUI deckCreatorGUI;
    private JStoneClient client;
    private int collectionPage = 0;
    private CollectionPanel currentCollection;

    public DeckManagerGUI(JStoneClient client) {
        super(new BorderLayout());
        this.client = client;
        JButton next = new JButton("Weiter");
        JButton pref = new JButton("Zurück");
        JButton mainMenu = new JButton("Hauptmenü");
        GridLayout gridLayout = new GridLayout(1, 0);
        gridLayout.setHgap(150);
        JPanel barPanel = new JPanel(gridLayout);
        barPanel.add(pref);
        barPanel.add(mainMenu);
        barPanel.add(next);
        next.addActionListener(e -> client.requestCollectionPage(collectionPage + 1));
        pref.addActionListener(e -> {
            if (collectionPage > 0)
                client.requestCollectionPage(collectionPage - 1);
        });
        mainMenu.addActionListener(it -> client.betreteMainMenu());
        add(barPanel, BorderLayout.SOUTH);
        deckCreatorGUI = new DeckCreatorGUI();
        add(deckCreatorGUI.getScrollPane(), BorderLayout.EAST);
    }

    public void onCollectionRespond(RespondCollection respondCollection) {
        if (currentCollection != null)
            remove(currentCollection);
        this.collectionPage = respondCollection.getPage();
        ListX<CollectionKarte> karten = respondCollection.getKarten();
        CollectionPanel collectionPanel = new CollectionPanel(karten);
        collectionPanel.getKarten().forEach(it -> it.getButton().addActionListener(e -> {
            int pNumber = collectionPanel.getPanelNumber((JButton) e.getSource());
            client.requestAddToDeck(this.collectionPage, pNumber);
        }));
        add(collectionPanel, BorderLayout.CENTER);
        currentCollection = collectionPanel;
        revalidate();
        repaint();
    }


    public void onAddCardToDeckRespond(AddCardToDeckEvent event) {
        int index = event.getIndex();
        ListX<KartenPanel> karten = this.currentCollection.getKarten();
        KartenPanel toAdd = karten.get(index);
        this.deckCreatorGUI.addCard(toAdd);
    }
}
