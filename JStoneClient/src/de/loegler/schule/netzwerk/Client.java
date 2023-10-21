package de.loegler.schule.netzwerk;

/**
 * Über die Klasse
 * Client werden Netzwerkverbindungen mit dem TCP/IP
 * Protokoll ermöglicht. Es können Strings gesendet und empfangen
 * werden, wobei der Empfang nebenläufig geschieht. Zur Vereinfachung
 * geschieht dies zeilenweise.
 * Die empfangene Nachricht wird durch eine Ereignisbehandlungs
 * methode verarbeitet.
 **/
public abstract class Client {
    public Connection connect;

    /**
     * Es wird eine Verbindung zum angegebenen Server aufgebaut
     */
    public Client(String pServerIP, int pServerPort) {
        connect = new Connection(pServerIP, pServerPort);

    }

    /**
     * Die Anfrage liefert den Wert true, wenn der Client mit dem Server
     * aktuell verbunden ist. Ansonsten liefert sie den Wert false.
     */
    public boolean isConnected() {
        return connect != null;

    }

    /**
     * Die Nachricht wird um einen Zeilentrenner ergänzt und zum Server gesendet.
     * @param pMessage Die Nachricht, die gesendet werden soll.
     */
    public void send(String pMessage) {
        connect.send(pMessage);
    }

    /**
     * Reaktion auf Nachrichten vom Server. In einer Unterklasse zu implementieren.
     **/
    public abstract void processMessage(String pMessage);

    /**
     * Die Verbindung zum Server wird getrennt und kann nicht
     * mehr verwendet werden.
     */
    public void close() {
        connect.close();

    }


}
