package de.loegler.jstone.client.gui;

import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.Karte;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Zeigt in einem eigenem Fenster einige Informationen einer Karte an
 */
public class KartenInformationsFenster {

    private JFrame frame;
    private JPanel panel;

    public KartenInformationsFenster(Karte karte) {
        frame = new JFrame( karte.getName());
        panel = new JPanel();
        frame.setContentPane(panel);
        panel.setLayout(new GridLayout(0, 2));
        addPair("Manakosten", "" + karte.getManakosten());
        panel.add(new JLabel("Beschreibung"));
        JTextArea textArea = new JTextArea(karte.getBeschreibung());
        textArea.setBackground(panel.getBackground());
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Arial", Font.ITALIC, 13));
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        panel.add(textArea);
        if (karte instanceof Diener) {
            Diener diener = (Diener) karte;
            addPair("Angriff: ", diener.getAngriff() + "");
            addPair("Leben: ", diener.getLeben() + "");
            panel.add(new JLabel("Effekte: "));
            int count = 0;
            for (Diener.DienerEffekt value : Diener.DienerEffekt.values()) {
                if (diener.hasEffect(value)) {
                    count++;
                    panel.add(new JLabel(value.toString()));
                }
            }
            if (count % 2 == 0) {
                panel.add(new JLabel("")); //Platzhalter für das Layout --> Immer in 2er Gruppen
            }
        }
        addPair("Seltenheit", karte.getSeltenheit().toString().toLowerCase(Locale.ROOT));
        frame.setSize(350, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void addPair(String desc, String val) {
        panel.add(new JLabel(desc));
        panel.add(new JLabel(val));
    }
}
