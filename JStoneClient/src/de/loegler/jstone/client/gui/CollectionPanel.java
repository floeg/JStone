package de.loegler.jstone.client.gui;

import de.loegler.jstone.core.CollectionKarte;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Fragment, welches 15 Karten übersichtlich darstellt.
 */
public class CollectionPanel extends JPanel {
    private HashMap<JButton, Integer> panelNummer = new HashMap<>();
    private ListX<KartenPanel> karten = new ListX<>();

    /**
     * Erstellt ein Fragment, welches aus bis zu 15 Karten bestehen kann.
     * Mehr als 15 Karten in der Liste werden ignoriert. Weniger als 15 Karten werden mit Platzhaltern gefüllt.
     *
     * @param cards
     */
    public CollectionPanel(ListX<CollectionKarte> cards) {
        setLayout(new GridLayout(3, 5));
        cards.forEachIndexed((it, i) -> {
            System.out.println(it.getKarte());
            KartenPanel k = new KartenPanel(it.getKarte());
            add(k);
            this.karten.append(k);
            panelNummer.put(k.getButton(), i);

            if (it.getKarteAnzahl() <= 0) {
                k.getButton().setBackground(new Color(69, 66, 66));

            } else if (it.getKarteAnzahl() == 1) {
                k.getButton().setBackground(new Color(120, 255, 15));
            } else {
                k.getButton().setBackground(new Color(47, 104, 2));
            }
        });
    }

    public ListX<KartenPanel> getKarten() {
        return karten;
    }

    public int getPanelNumber(JButton source) {
        return panelNummer.get(source);
    }


}
