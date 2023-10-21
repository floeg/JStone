package de.loegler.jstone.server.main;


import de.loegler.jstone.core.Diener;
import de.loegler.schule.netzwerk.DatabaseConnector;
import de.loegler.schule.netzwerk.QueryResult;

/**
 * Die Klasse DatabaseManager verwaltet alle SQL aufrufe. Sie wurde als Singleton implementiert.
 * Hierdurch kann eine einfache synchronisation stattfinden.
 */
public class DatabaseManager {


    private static DatabaseManager instance = new DatabaseManager();
    /**
     * Die Create Table Anweisungen der einzelnen Tabellen
     */
    private final String[] createTableCommands = {
            "CREATE TABLE USERLOGIN (UID Integer PRIMARY KEY AUTO_INCREMENT NOT NULL, USERNAME VarChar(30) NOT NULL ,PASSWORT TEXT NOT NULL)",
            "CREATE TABLE USER (UID INTEGER PRIMARY KEY NOT NULL, Gold Integer, Staub Integer, Punkte Integer)",
            "CREATE TABLE USERHATKARTE(UID INTEGER NOT NULL, KID Integer NOT NULL, Anzahl INTEGER NOT NULL, PRIMARY KEY (UID,KID))",
            "CREATE TABLE Karte(KID INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,Name TEXT, Typ VARCHAR(6),Manakosten INTEGER, Klasse TEXT, Erweiterung TEXT, Seltenheit TEXT, Beschreibung TEXT, Kampfschrei Integer)",
            "CREATE TABLE Diener(KID INTEGER PRIMARY KEY NOT NULL, Angriff INTEGER, Leben INTEGER, Todesroecheln Integer)",
            "CREATE TABLE Aktion (AID INTEGER PRIMARY KEY NOT NULL, AktionsTyp TEXT)",
            "CREATE TABLE AktionParameter(AID INTEGER NOT NULL, ParameterNR INTEGER, Parameter Text, PRIMARY KEY(AID,ParameterNR))",
            "CREATE TABLE Effekt (EID INTEGER PRIMARY KEY NOT NULL, Effektname TEXT)",
            "CREATE TABLE DienerHatEffekt (KID INTEGER NOT NULL,EID INTEGER NOT NULL, Primary KEY(KID,EID))",
            "CREATE TABLE DeckHatKlasse(DECKID INTEGER PRIMARY KEY NOT NULL, Klasse Text)",
            "CREATE TABLE Deck (DECKID INTEGER NOT NULL, SLOTNR INTEGER NOT NULL, KID INTEGER NOT NULL, Primary KEY(DECKID,SLOTNR))",
            "CREATE TABLE SpielerHatDeck(SID Integer NOT NULL, DECKID INTEGER NOT NULL, PRIMARY KEY(SID,DECKID))",
            "CREATE TABLE KarteHatBild(KID Integer NOT NULL PRIMARY KEY, Bildname TEXT)",
            "CREATE TABLE Freundschaft(EUID Integer NOT NULL, ZUID Integer NOT NULL, PRIMARY KEY(EUID,ZUID))",
            "CREATE TABLE FreundschaftAnfrage (EUID Integer NOT NULL, ZUID Integer NOT NULL, PRIMARY KEY(EUID,ZUID))",
    };
    DatabaseConnector connector;


    private DatabaseManager() {
        openConnection();
    }


    public static DatabaseManager getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        DatabaseManager.getInstance();
    }

    /**
     * Baut eine Verbindung zur Datenbank auf und erstellt alle Tabellen welche benötigt werden.
     */
    public void openConnection() {
        Settings inst = Settings.getInstance();
        connector = new DatabaseConnector(inst.DATABASE_IP, inst.DATABASE_PORT, inst.DATABASE_NAME, inst.DATABASE_USER, inst.DATABASE_PW);
        if (connector.getErrorMessage() != null) {
            if (connector.getErrorMessage().contains("Unknown database")) {

                System.err.println("Es gab einen Fehler beim Versuch auf die Datenbank zuzugreifen. Versuche sie anzulegen");
                DatabaseConnector tmp = new DatabaseConnector(inst.DATABASE_IP, inst.DATABASE_PORT, "", inst.DATABASE_USER, inst.DATABASE_PW);
                tmp.executeStatement("CREATE DATABASE " + inst.DATABASE_NAME);
                connector = new DatabaseConnector(inst.DATABASE_IP, inst.DATABASE_PORT, inst.DATABASE_NAME, inst.DATABASE_USER, inst.DATABASE_PW);
                if (connector.getErrorMessage() == null)
                    System.out.println("Datenbank " + inst.DATABASE_NAME + " wurde erfolgreich angelegt.");
            } else
                System.err.println("Fehler beim Versuch sich mit der Datenbank zu verbinden. Ist sie Online?");
        }
        for (String createTable : createTableCommands) {
            //Wenn die Tabellen schon existieren passiert nichts.
            connector.executeStatement(createTable);
        }
        int effekteAnzahl = Integer.parseInt(fuehreSQLAus("SELECT COUNT(EID) FROM Effekt").getData()[0][0]);
        Diener.DienerEffekt[] alleEffekte = Diener.DienerEffekt.values();
        if (alleEffekte.length > effekteAnzahl) {
            for (int i = effekteAnzahl; i != alleEffekte.length; i++) {
                System.out.println("Führe SQL AUS!");
                String sql = "INSERT INTO Effekt VALUES(" + (i + 1) + ", '" + alleEffekte[i].toString() + "')";
                fuehreSQLAus(sql);
            }
        }
        System.out.println("Datenbankverbindung erfolgreich eingerichtet.");
    }

    /**
     * Threadsichere Ausführung von SQL-Befehlen.
     *
     * @param sql
     * @return Ein mögliches QueryResult - auch null möglich
     */
    public QueryResult fuehreSQLAus(String sql) {
        synchronized (this) {
            connector.executeStatement(sql);
            return connector.getCurrentQueryResult();
        }
    }

}
