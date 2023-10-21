package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

import java.util.Locale;

/**
 * Ein <code>TurnEvent</code> wird versendet, sobald der Zug eines Spielers beginnt oder endet.
 * In der Regel folgt dieses Event daher nach Spielstart oder nach ende des Zuges des Gegners.
 * Es kann auch von einem Client gesendet werden um den eigenen Zug zu beenden.
 */
public class TurnEvent extends Event {
    public static final String eventTyp = "TurnEvent";
    /**
     * Zeigt, dass der Zug eines Spielers beendet wurde
     */
    public static final String turnEnd = "turnEnd";
    /**
     * Zeigt, dass der Zug eines Spielers begonnen hat
     */
    public static final String turnStart = "turnStart";

    private int playerGameNumber;
    private boolean isStart;


    public TurnEvent(JSONObject argumente) {
        isStart = argumente.getString("isStart").toLowerCase(Locale.ROOT).contains("true");
        playerGameNumber = Integer.parseInt(argumente.getString("playerGameNumber"));
    }

    public TurnEvent(int playerGameNumber, boolean isStart) {
        this.playerGameNumber = playerGameNumber;
        this.isStart = isStart;
    }

    public int getPlayerGameNumber() {
        return playerGameNumber;
    }
    public boolean isStart() {
        return isStart;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject argumente = new JSONObject();
        argumente.map("isStart", "'" + isStart + "'");
        argumente.map("playerGameNumber", playerGameNumber + "");
        result.map(JSONAble.ARGUMENTE, argumente);
        return result;
    }
}
