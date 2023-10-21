package de.loegler.jstone.server.admin.gui;

import de.loegler.core.gui.TextFrame;
import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.Karte;
import de.loegler.jstone.core.Klasse;
import de.loegler.jstone.server.admin.sql.CardCreator;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Darstellung einer GUI welche es ermöglicht Karten in der MYSQL-Datenbank anzulegen.
 */
public class CardCreatorGUI {
    private JFrame creatorFrame;
    private JPanel panel;
    private JTextField name;
    private JLabel nameL, angriffL, lebenL, erweiterungL, typL, kampfschreiL, todesroechelnL, beschreibungL, klasseL;
    private JSpinner angriff, leben, manakosten;
    private JButton erstelleKarte;
    private ListX<JCheckBox> effects;
    private JComboBox<Karte.Seltenheit> seltenheit;
    private JComboBox<Karte.KartenTyp> typ;
    private JTextField erweiterung;
    private JTextField kampfschrei, todesroecheln, beschreibung;
    private CardCreator cardCreator;
    private JComboBox<Klasse> klasse;
    private JLabel bildL;
    private JTextField bildText;

    public CardCreatorGUI(CardCreator cardCreator) {
        this.cardCreator = cardCreator;
        effects = new ListX<>();
        CardCreatorListener listener = new CardCreatorListener();
        creatorFrame = new JFrame("CardCreator - Admintool");
        panel = new JPanel(new GridLayout(0, 2));
        creatorFrame.setContentPane(panel);
        typL = new JLabel("Kartentyp:");
        typ = new JComboBox<>(Karte.KartenTyp.values());
        this.panel.add(typL);
        this.panel.add(typ);
        typ.addActionListener(listener);

        name = new JTextField();
        nameL = new JLabel("Name");
        panel.add(nameL);
        panel.add(name);
        angriffL = new JLabel("Angriff");
        angriff = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        panel.add(angriffL);
        panel.add(angriff);
        lebenL = new JLabel("Leben");
        leben = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        panel.add(lebenL);
        panel.add(leben);
        for (Diener.DienerEffekt effekt : Diener.DienerEffekt.values()) {
            JCheckBox effect = new JCheckBox(effekt.name());
            this.effects.append(effect);
            this.panel.add(effect);
        }
        if (Diener.DienerEffekt.values().length % 2 != 0) {
            //Platzhalter für das Layout
            this.panel.add(new JLabel(""));
        }
        erweiterungL = new JLabel("Erweiterung:");
        erweiterung = new JTextField("Standard");
        this.panel.add(erweiterungL);
        this.panel.add(erweiterung);

        this.manakosten = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        this.panel.add(manakosten);

        this.seltenheit = new JComboBox<>(Karte.Seltenheit.values());
        this.panel.add(seltenheit);
        this.kampfschreiL = new JLabel("Kampfschrei (AID aus Tabelle)");
        this.todesroechelnL = new JLabel("Todesröcheln (AID aus Tabelle)");
        this.kampfschrei = new JTextField();
        this.todesroecheln = new JTextField();
        this.panel.add(kampfschreiL);
        this.panel.add(kampfschrei);
        this.panel.add(todesroechelnL);
        this.panel.add(todesroecheln);
        klasseL = new JLabel("Klasse: ");
        klasse = new JComboBox<>(Klasse.values());
        this.panel.add(klasseL);
        this.panel.add(klasse);
        beschreibungL = new JLabel("Beschreibung: ");
        beschreibung = new JTextField();
        this.panel.add(beschreibungL);
        this.panel.add(beschreibung);
        bildL = new JLabel("Bild?:");
        bildText = new JTextField();
        this.panel.add(bildL);
        this.panel.add(bildText);
        this.erstelleKarte = new JButton("Erstelle Karte");
        erstelleKarte.addActionListener(listener);
        this.panel.add(erstelleKarte);
        creatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        creatorFrame.setSize(550, 650);
        creatorFrame.setLocationRelativeTo(null);
        creatorFrame.setVisible(true);
    }


    public class CardCreatorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == erstelleKarte) {
                int manaValue = Integer.parseInt(manakosten.getValue().toString());
                String nameValue = name.getText();
                String erweiterungValue = erweiterung.getText();
                String typValue = typ.getSelectedItem().toString();
                String seltenValue = seltenheit.getSelectedItem().toString();
                Integer kampfschreiID = null;
                if (kampfschrei.getText() != null && !kampfschrei.getText().isEmpty())
                    kampfschreiID = Integer.parseInt(kampfschrei.getText());
                String klasseValue = klasse.getSelectedItem().toString();
                String beschreibungValue = beschreibung.getText();
                if (nameValue.isEmpty()) {
                    new TextFrame(new TextFrame.TextFrameOptions().enableAccept().changeMode(TextFrame.TextFrameOptions.WARNING_MODE), "Fehler:", "Es muss ein Name angegeben werden!");
                } else {
                    if (typ.getSelectedItem() == Karte.KartenTyp.ZAUBER) {

                        cardCreator.erstelleKarte(cardCreator.getNextKartID(), nameValue, typValue, manaValue, klasseValue, erweiterungValue, seltenValue, beschreibungValue, kampfschreiID, bildText.getText());
                    } else {
                        Integer todesroechelnID = null;
                        if (todesroecheln.getText() != null && !todesroecheln.getText().isEmpty())
                            todesroechelnID = Integer.parseInt(todesroecheln.getText());
                        int angriffValue = Integer.parseInt(angriff.getValue().toString());
                        int lebenValue = Integer.parseInt(leben.getValue().toString());
                        ListX<String> effekteList = new ListX<>();
                        effects.forEach((current) -> {
                            if (current.isSelected()) effekteList.append(current.getText());
                        });
                        cardCreator.erstelleDiener(cardCreator.getNextKartID(), nameValue, typValue, manaValue, klasseValue, erweiterungValue, seltenValue, beschreibungValue, kampfschreiID, angriffValue, lebenValue, todesroechelnID, effekteList, bildText.getText());
                    }
                    new TextFrame(new TextFrame.TextFrameOptions().enableAccept(), "Karte erstellt!", "Die Karte wurde in der Datenbank hinterlegt.");
                }
            } else if (e.getSource() == typ) {
                if (e.getSource() instanceof JComboBox) {
                    JComboBox tmp = (JComboBox) e.getSource();
                    if (tmp.getSelectedItem() == Karte.KartenTyp.ZAUBER) {
                        angriff.setEnabled(false);
                        leben.setEnabled(false);
                        effects.forEach(it -> it.setEnabled(false));
                        todesroecheln.setEnabled(false);
                    } else {
                        angriff.setEnabled(true);
                        leben.setEnabled(true);
                        effects.forEach(it -> it.setEnabled(true));
                        todesroecheln.setEnabled(true);
                    }
                }
            }
        }
    }
}
