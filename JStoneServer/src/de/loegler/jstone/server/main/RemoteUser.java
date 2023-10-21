package de.loegler.jstone.server.main;


import de.loegler.jstone.server.admin.sql.UserSQL;
import de.loegler.schule.netzwerk.QueryResult;

/**
 * Repräsentiert einen Client
 */
public class RemoteUser {
    private final Verbindungsinformationen verbindungsinformationen;
    private int userID = -1;
    private String currentState = "NOTLOGGEDIN";
    private int spielstaerke = 0;
    private int selectedDeckID = -1;
    private String username = "UNKNOWN";

    /**
     * Rückgabe des Decks des Spielers. Anhand der ID lässt sich zudem die ausgewählt Klasse bestimmen.
     *
     * @return Die ID des Decks des Spielers oder -1 wenn kein Deck ausgewählt wurde
     */
    public int getSelectedDeckID() {
        return selectedDeckID;
    }

    public void setSelectedDeckID(int selectedDeckID) {
        this.selectedDeckID = selectedDeckID;
    }

    public RemoteUser(Verbindungsinformationen verbindungsinformationen) {
        this.verbindungsinformationen = verbindungsinformationen;
    }

    /**
     * Rückgabe der Spielstärke.
     * Muss über {@link #updateSpielstarke()} aktualisiert werden, um aufrufe zu minimieren.
     * @return Die letzte abgefragte Spielstärke des Spielers
     */
    public int getSpielstaerke() {
        return spielstaerke;
    }

    /**
     * Läd die aktuelle Spielstärke von der Datenbank.
     * Sollte so selten wie möglich aufgerufen werden um SQL Abfragen zu reduzieren.
     */
    public void updateSpielstarke() {
        spielstaerke = UserSQL.getPunkte(this.userID);
    }

    public Verbindungsinformationen getVerbindungsinformationen() {
        return verbindungsinformationen;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getCurrentState() {
        return currentState;
    }
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getDisplayname() {
        if (username.equalsIgnoreCase("UNKNOWN")) {
            QueryResult tmp = DatabaseManager.getInstance().fuehreSQLAus("SELECT username FROM userlogin WHERE UID = " + userID);
            if (tmp.getRowCount() != 0)
                username = tmp.getData()[0][0];
        }
        return username;
    }

    public static class Verbindungsinformationen {
        private final String ipAddress;
        private final int port;

        public Verbindungsinformationen(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getPort() {
            return port;
        }
        //Automatisch Generiert (Template Intellij-Default)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Verbindungsinformationen that = (Verbindungsinformationen) o;

            if (port != that.port) return false;
            return ipAddress.equals(that.ipAddress);
        }
        //Wichtig zum finden der User

        @Override
        public int hashCode() {
            int result = ipAddress.hashCode();
            result = 31 * result + port;
            return result;
        }
        //Bis hier automatisch generiert
    }
}
