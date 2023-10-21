package de.loegler.core.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Die Klasse Startframe erzeugt ein (undekoriertes) JFrame welches aus einem
 * Icon, einer aktuellen Meldung, sowie eines Fortschrittbalkens besteht.
 * Um das Startframe zu beenden muss {@link #fortschritt(int, String)} der Wert 100 uebergeben werden.
 *
 */
public class Startframe implements Darkmodeable {
    private final JFrame frame;
    private final JProgressBar fortschritt;
    private final JLabel text;
    private final JLabel welcheAufgabe;
    private final JLabel icon;


    /**
     * Nachdem das Icon geladen wurde kann dieses beim Laden angezeigt werden. Das
     * Bild wird auf die größe 128x128 skaliert.
     */
    public void setIcon(ImageIcon image) {
        image.setImage(image.getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT));
        icon.setIcon(image);
        icon.setText("");
    }

    public Startframe(String name) {
        System.out.println("Startframe wird nun geladen");
        frame = new JFrame(name + " - Start");
        frame.getContentPane().setLayout(null);
        frame.setLayout(null);
        fortschritt = new JProgressBar();
        frame.setUndecorated(true);
        frame.setSize(650, 350);
        frame.setLocationRelativeTo(null);
        text = new JLabel(name + " wird geladen...");
        icon = new JLabel("icon_unknown");

        icon.setBounds(255, 100, 128, 128);
        frame.getContentPane().add(icon);

        text.setBounds(275, 270, 150, 25);
        fortschritt.setBounds(175, 295, 350, 25);

        frame.getContentPane().add(text);
        frame.getContentPane().add(fortschritt);
        welcheAufgabe = new JLabel("Überprüfe Grundeinstellungen");
        welcheAufgabe.setBounds(175, 325, 250, 25);
        frame.getContentPane().add(welcheAufgabe);
        this.toLightmode();
        frame.setVisible(true);
    }

    /**
     * Informiert den Nutzer über den aktuellen Fortschritt. Wird eine Zahl über 99
     * übergeben, so wird das <code>Startframe</code> nach 2.5 Sekunden beendet
     *
     * @param percent
     * @param naechsteAufgabe
     */
    public void fortschritt(int percent, String naechsteAufgabe) {
        this.fortschritt.setValue(percent);
        this.welcheAufgabe.setText(naechsteAufgabe);

        if (percent >= 100) {
            this.text.setText("Programm wird gestartet");
            this.welcheAufgabe.setText("Programm wird jetzt gestartet");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Das Hauptprogramm wurde geladen. Startframe wird beendet.");
            frame.dispose();
        }

    }

    @Override
    public void toLightmode() {
        frame.getContentPane().setBackground(Darkmodeable.LightmodeColor.BACKGROUND.getColor());
        text.setForeground(Darkmodeable.LightmodeColor.BUTTON.getColor());
        this.fortschritt.setForeground(Darkmodeable.LightmodeColor.BUTTON.getColor());
        this.welcheAufgabe.setForeground(Darkmodeable.LightmodeColor.BUTTON_SECOND.getColor());

    }

    @Override
    public void toDarkmode() {
        frame.getContentPane().setBackground(Darkmodeable.DarkmodeColor.BACKGROUND.getColor());
        text.setForeground(Darkmodeable.DarkmodeColor.BUTTON.getColor());
        this.fortschritt.setForeground(Darkmodeable.DarkmodeColor.BUTTON.getColor());
        this.welcheAufgabe.setForeground(Darkmodeable.DarkmodeColor.BUTTON_SECOND.getColor());
    }

}
