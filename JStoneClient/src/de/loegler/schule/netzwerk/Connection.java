package de.loegler.schule.netzwerk;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class Connection {
    BufferedReader buff;
    PrintWriter out;
    Socket socket;

    /**
     * Ein Objekt vom Typ Connection wird erstellt. Stellt eine Verbindung zum Server her.
     * Kann die Verbindung nicht hergestellt
     * werden, kann die Instanz von Connection nicht mehr verwendet
     * werden.
     **/
    public Connection(String pServerIP, int pServerPort) {
        try {
            socket = new Socket(pServerIP, pServerPort);
            buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Fehler beim Verbindungsaufbau");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Die Nachricht wird um einen Zeilentrenner ergänzt und zum Server gesendet.
     * @param pMessage Die Nachricht, die gesendet werden soll.
     **/
    public void send(String pMessage) {
        try {
            out.println(pMessage);
            out.flush();
        } catch (Exception se) {
            System.out.println(se.getMessage());
        }
    }

    /**
     * Es wird auf eine Nachricht des Servers gewartet und diese, ohne Zeilentrenner, zurückgegeben.
     * Der Aufruf blockiert hierbei den aufrufenden Thread bis eine Nachricht empfangen wurde.
     * @return Die empfangene Nachricht.
     **/
    public String receive() {
        String ein = null;
        try {
            ein = buff.readLine();
        } catch (IOException se) {
            System.out.println(se.getMessage());
        }
        return ein;
    }

    /**
     * Die Verbindung zum Server wird getrennt und kann nicht mehr
     * verwendet werden. War die Verbindung bereits getrennt, geschieht
     * nichts.
     */
    public void close() {
        try {
            socket.close();
            buff.close();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}