package de.loegler.jstone.client.main;

import de.loegler.core.gui.Startframe;
import de.loegler.core.gui.TextFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * GUI ermöglicht die Serverauswahl und das Login.
 * Wird geschlossen und durch ein {@link JStoneGUI} ersetzt, sobald der User sich eingeloggt hat
 */
public class PreJStoneGUI {


    // Nur zum Testen
    public static final boolean debugMode = false;
    private final JButton connect;
    private final JFrame frame;
    Startframe startframe;
    private JStoneClient client;
    private JTextField ip;
    private JSpinner port;
    private JPanel panel;
    private JPasswordField passwordField;
    private JTextField username;
    private JButton login, register;
    private JLabel userL, pwL;
    //TODO Datenschutz

    //Datenschutz: Liefert Informationen welche Daten gespeichert werden
    private JButton daSchutzAllgemein; //Ungenau(er), dafür offline möglich
    private JButton daSchutzKonkreterServer; //Konkret, nur online möglich


    /**
     * Zeigt einen Splash-Screen und zeigt anschließend ein Fenster an, in welchem ein Server ausgewählt werden kann.
     */
    public PreJStoneGUI() {
        if (!debugMode) {
            startframe = new Startframe("JSTONE");
            startframe.toDarkmode();
            startframe.setIcon(new ImageIcon(JStoneClient.class.getResource("/images/JSTONE_ICON.png")));
            startframe.fortschritt(0, "Erstelle GUI...");
        }
        frame = new JFrame("JSTONE");
        frame.setSize(250, 250);
        panel = new JPanel();
        frame.setLocationRelativeTo(null);
        try {
            ip = new JTextField(Inet4Address.getLocalHost().getHostAddress());
            port = new JSpinner(new SpinnerNumberModel(9998, 0, 99999, 1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        connect = new JButton("Wähle Server");
        connect.addActionListener(e -> this.onGivenIPInformations(ip.getText(), Integer.parseInt(port.getValue().toString())));
        frame.setContentPane(panel);
        panel.add(ip);
        panel.add(port);
        panel.add(connect);
        if (!debugMode){
            startframe.fortschritt(100, "");
            showDaschutz();
        }
        frame.setVisible(true);
        if (debugMode){
            connect.doClick();
        }
    }

    /**
     * Die Einstiegsmethode auf der Seite des Clients.
     *
     * @param args Argumente, aktuell nicht benutzt
     */
    public static void main(String[] args) {
        PreJStoneGUI preJStoneGUI = new PreJStoneGUI();
    }

    public void onGivenIPInformations(String ip, int port) {
        frame.setVisible(false);
        client = new JStoneClient(ip, port,this);
        client.tauscheRSASchluessel();
        this.panel = new JPanel(new GridLayout(0, 2));
        passwordField = new JPasswordField();
        username = new JTextField("Spieler" + new Random().nextInt(99999));
        register = new JButton("Registrieren");
        login = new JButton("Anmelden");
        userL = new JLabel("Username:");
        pwL = new JLabel("Passwort: ");
        panel.add(userL);
        panel.add(username);
        panel.add(pwL);
        panel.add(passwordField);
        panel.add(login);
        panel.add(register);
        frame.setContentPane(panel);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        LoginListener listener = new LoginListener();
        login.addActionListener(listener);
        register.addActionListener(listener);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        daSchutzAllgemein = new JButton("Datenschutzinformationen");
        daSchutzAllgemein.addActionListener(e -> showDaschutz());
        frame.add(daSchutzAllgemein);
        frame.setVisible(true);
        if (debugMode) {
            try {
                Thread.sleep(550);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.username.setText("Spieler123");
            passwordField.setText("unsecurePassword");
        }
    }

    private void showDaschutz(){
        new TextFrame("Datenschutz Allgemein",
                "Durch das Verbinden mit einem Server wird deine IP Adresse sowie dein Port",
                "an den Server übertragen. Dies ist zur aufrechterhaltung der Verbindung notwendig.",
                "Für den Login muss der Benutzername mit einem Passwort gespeichert werden",
                "Weiterhin wird einer NutzerID vergeben, welche Rückschlüsse auf die",
                "Dauer der Mitgliedschaft zulässt.",
                "Weiterhin werden Spielstände gespeichert.",
                "Diese sind: Virtuelle Währungen: Staub und Gold",
                "Liste mit freigeschalteten Karten, eigene Decks sowie Freunde",
                "Übertragene Nachrichten werden geloggt, jedoch nach 60 Tagen gelöscht.").addTextFrameListener(res -> {
            if (res == TextFrame.RETURN_CANCEL) {
                System.exit(0);
            }
        });
    }



    /**
     * Wird aufgerufen, wenn der Client sich erfolgreich eingeloggt hat.
     */
    public void onLoginSuccessful(){
        this.frame.dispose();
    }

    /**
     * Wird aufgerufen, falls es beim Login zu einem Fehler kommen sollte.
     */
    public void onErrorLogin(String errorMessage){

    }


    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String user = username.getText();
            char[] pw = passwordField.getPassword();
            String pass = "";
            for (int i = 0; i != pw.length; i++) {
                pass += pw[i];
                pw[i] = '0';
            }
            client.sendeLoginEvent(user, pass, e.getSource() == register);
        }
    }

}
