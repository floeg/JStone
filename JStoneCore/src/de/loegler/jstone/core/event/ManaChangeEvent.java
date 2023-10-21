package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird vom Server versendet, sobald sich bei einem Spieler der Manastand ändert.
 */
public class ManaChangeEvent extends Event {

    public static final String eventTyp = "ManaChangeEvent";
    private int maxMana, currentMana;
    private int side;

    public ManaChangeEvent(int maxMana, int currentMana, int side) {
        this.maxMana = maxMana;
        this.currentMana = currentMana;
        this.side = side;
    }

    public ManaChangeEvent(JSONObject args) {
        maxMana = args.getInt("maxMana");
        currentMana = args.getInt("currentMana");
        side = args.getInt("side");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toRet = new JSONObject();
        JSONObject args = new JSONObject();
        toRet.map(JSONAble.EVENTTYPKEY, eventTyp);
        args.map("maxMana", maxMana);
        args.map("currentMana", currentMana);
        args.map("side", side);
        toRet.map(JSONAble.ARGUMENTE, args);
        return toRet;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getSide() {
        return side;
    }
}
