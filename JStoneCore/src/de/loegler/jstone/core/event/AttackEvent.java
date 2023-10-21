package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird vom Client versendet, sobald er einen Diener angreifen möchte oder vom Server,
 * wenn er den Angriff eines Clients bestätigt und ihn an den Gegner (mit) sendet.
 */
public class AttackEvent extends Event {
    public static final String eventTyp = "AttackEvent";
    private final int sourceSide;
    private final int sourceNumber;
    private final int targetSide;
    private final int targetNumber;

    /**
     * Erzeugt ein neues Event, welches versendet werden kann
     * @param sourceSide Die Seite des Angreifers
     * @param sourceNumber Die Nummer des Angreifers (Held 0, Diener 1..7)
     * @param targetSide Die Seite des Ziels
     * @param targetNumber Die Nummer des Ziels
     */
    public AttackEvent(int sourceSide, int sourceNumber, int targetSide, int targetNumber) {
        this.sourceSide = sourceSide;
        this.sourceNumber = sourceNumber;
        this.targetSide = targetSide;
        this.targetNumber = targetNumber;
    }

    public AttackEvent(JSONObject jsonObject) {
        sourceSide = Integer.parseInt(jsonObject.getString("sourceSide"));
        sourceNumber = Integer.parseInt(jsonObject.getString("sourceNumber"));
        targetSide = Integer.parseInt(jsonObject.getString("targetSide"));
        targetNumber = Integer.parseInt(jsonObject.getString("targetNumber"));
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("sourceSide", sourceSide);
        args.map("sourceNumber", sourceNumber);
        args.map("targetSide", targetSide);
        args.map("targetNumber", targetNumber);
        jsonObject.map(JSONAble.ARGUMENTE, args);
        return jsonObject;
    }

    public int getSourceSide() {
        return sourceSide;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getTargetSide() {
        return targetSide;
    }

    public int getTargetNumber() {
        return targetNumber;
    }
}
