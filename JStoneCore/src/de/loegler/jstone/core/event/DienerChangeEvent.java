package de.loegler.jstone.core.event;

import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Aktualisiert einen vorhandenen Diener auf dem Schlachtfeld
 * Sind seine Leben kleiner als 1, so wird er entfernt.
 */
public class DienerChangeEvent extends Event {

    public static final String eventTyp = "DienerChangeEvent";

    /**
     * Benennung:
     * 0: Spieler 0
     * 1: Spieler 1
     * Benennung nach SpielerNummer, nicht der Nummer in der GUI!
     */
    private final int targetSide;
    /**
     * Benennung:
     * 0 - Held
     * 1-7: Diener
     */
    private final int targetNumber;

    private final Diener diener;


    public DienerChangeEvent(JSONObject args) {
        this.targetNumber = args.getInt("targetNumber");
        this.targetSide = args.getInt("targetSide");
        this.diener = new Diener(args.getJSONObject("diener"));
    }


    public DienerChangeEvent(int targetSide, int targetNumber, Diener diener) {
        this.targetSide = targetSide;
        this.targetNumber = targetNumber;
        this.diener = diener;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("targetNumber", targetNumber);
        args.map("targetSide", targetSide);
        args.map("diener", diener.toJSONObject());
        obj.map(JSONAble.ARGUMENTE, args);
        return obj;
    }
}
