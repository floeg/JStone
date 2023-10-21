package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird vom Server versendet, sobald ein Angriff durchgeführt wurde/ ein Charakter Schaden erleidet.
 */
public class DamageEvent extends Event {
    public static final String eventTyp = "DamageEvent";
    private int damage;
    /**
     * Benennung:
     * 0: Spieler 0
     * 1: Spieler 1
     * Benennung nach SpielerNummer, nicht der Nummer in der GUI!
     */
    private int targetSide;
    /**
     * Benennung:
     * 0 - Held
     * 1-7: Diener
     */
    private int targetNumber;


    public DamageEvent(int damage, int targetSide, int targetNumber) {
        this.damage = damage;
        this.targetSide = targetSide;
        this.targetNumber = targetNumber;
    }

    /**
     * Wandelt einen String in ein JSONObject um.
     *
     * @param json
     */
    public DamageEvent(JSONObject json) {
        damage = Integer.parseInt(json.getString("damage"));
        targetSide = Integer.parseInt(json.getString("targetSide"));
        targetNumber = Integer.parseInt(json.getString("targetNumber"));

    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY, eventTyp);

        JSONObject args = new JSONObject();
        args.map("damage", damage + "");
        args.map("targetSide", targetSide + "");
        args.map("targetNumber", targetNumber + "");

        outer.map(JSONAble.ARGUMENTE, args);
        return outer;
    }


    public int getDamage() {
        return damage;
    }

    /**
     * Benennung:
     * 0 - Held
     * 1-7: Diener
     */
    public int getTargetNumber() {
        return targetNumber;
    }

    /**
     * Benennung:
     * 0: Spieler 0
     * 1: Spieler 1
     */
    public int getTargetSide() {
        return targetSide;
    }
}
