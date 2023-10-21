package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;
import de.loegler.jstone.core.Klasse;

/**
 * Ein <code>GameStartEvent</code> wird versendet, wenn der Server zwei Spieler zu einem Spiel zuordnen konnte.
 */
public class GameStartEvent extends Event {
    public static final String eventTyp = "GameStartEvent";
    private final String enemyClass;
    private final String enemyName;
    private final String ownClass;
    /**
     * Die eigene Spielnummer. Entweder 0, oder 1. Der Gegner erhält die Spielnummer 1-eigeneSpielnummer
     * Der Spieler mit der Spielnummer 0 beginnt das Spiel.
     */
    private final String ownGameNumber;

    public GameStartEvent(String enemyClass, String enemyName, String ownClass, String ownGameNumber) {
        this.enemyClass = enemyClass;
        this.enemyName = enemyName;
        this.ownClass = ownClass;
        this.ownGameNumber = ownGameNumber;
    }

    public GameStartEvent(JSONObject argumente) {
        enemyClass = argumente.getString("enemyClass");
        enemyName = argumente.getString("enemyName");
        ownClass = argumente.getString("ownClass");
        ownGameNumber = argumente.getString("ownGameNumber");
    }

    /**
     * @return Die Klasse des Gegners
     */
    public Klasse getEnemyClass() {
        return getClass(enemyClass);
    }

    /**
     * @return Rückgabe der eigenen Klasse, welche für diese Runde ausgewählt wurde.
     */
    public Klasse getOwnClass() {
        return getClass(ownClass);
    }

    /**
     * @return Der (Anzeige-)Name des Gegners
     */
    public String getEnemyName() {
        return enemyName;
    }

    /**
     * Rückgabe der eigenen Spielnummer.
     * Es gilt {@link #getOwnGameNumber()} = 1-{@link #getEnemyGameNumber()}
     *
     * @return Die eigene Spielnummer
     */
    public int getOwnGameNumber() {
        return Integer.parseInt(ownGameNumber);
    }

    /**
     * Rückgabe der Spielnummer des Gegners.
     * Entspricht  1- {@link #getOwnGameNumber()}
     *
     * @return Die Spielnummer des Gegners
     */
    public int getEnemyGameNumber() {
        return 1 - getOwnGameNumber();
    }

    private Klasse getClass(String className) {
        for (Klasse value : Klasse.values()) {
            if (className.equalsIgnoreCase(value.toString()))
                return value;
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject event = new JSONObject();
        event.map("enemyClass", enemyClass);
        event.map("enemyName", enemyName);
        event.map("ownClass", ownClass);
        event.map("ownGameNumber", ownGameNumber + "");
        result.map(JSONAble.ARGUMENTE, event);
        return result;
    }
}
