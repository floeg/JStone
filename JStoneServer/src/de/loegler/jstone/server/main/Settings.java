package de.loegler.jstone.server.main;

/**
 * Die Klasse Setting verwaltet einige Einstellungen
 */
public class Settings {
    private static Settings instance;
    /**
     * Die IP-Adresse der Datenbank
     */
    public final String DATABASE_IP = "localhost";
    /**
     * Der Port der Datenbank
     */
    public final int DATABASE_PORT = 3306;
    /**
     * Der Name der Datenbank
     */
    public final String DATABASE_NAME = "jstone";
    /**
     * Der Datenbanknutzer
     */

    // Warning: Example login data for a local database. It is recommended to create an own user with a password.
    public final String DATABASE_USER = "root";
    /**
     * Das Passwort des Datenbanknutzers
     */
    public final String DATABASE_PW = "";

    /**
     * Der Preis einer Kartenpackung in Gold
     */
    public final int PACK_PRICE = 10;

    /**
     * @return Die einzige Instanz
     */
    public static Settings getInstance() {
        return instance == null ? (instance = new Settings()) : instance;
    }

}