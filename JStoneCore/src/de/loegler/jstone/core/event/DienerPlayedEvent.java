package de.loegler.jstone.core.event;

import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird versendet, sobald ein Diener auf das {@link de.loegler.jstone.core.Schlachtfeld} ausgespielt wurde
 */
public class DienerPlayedEvent extends Event {
    public static final String eventTyp = "DienerPlayedEvent";
    private final int side;
    private final Diener diener;

    public DienerPlayedEvent(JSONObject args) {
        side = args.getInt("side");
        diener = new Diener(args.getJSONObject("diener"));
    }

    public DienerPlayedEvent(int side, Diener diener) {
        this.side = side;
        this.diener = diener;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toRet = new JSONObject();
        toRet.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("side", side);
        args.map("diener", diener.toJSONObject());
        toRet.map(JSONAble.ARGUMENTE, args);
        return toRet;
    }

    public int getSide() {
        return side;
    }

    public Diener getDiener() {
        return diener;
    }
}
