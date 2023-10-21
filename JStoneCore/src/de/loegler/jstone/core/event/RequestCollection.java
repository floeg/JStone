package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird versendet um eine bestimmte Seite der Collection anzufragen
 */
public class RequestCollection extends Event {

    public static final String eventTyp = "RequestCollection";
    public static final int SELECT_ALL = 0;
    private int page;
    private int selectionMode;

    public RequestCollection(int page, int selectionMode) {
        this.page = page;
        this.selectionMode = selectionMode;
    }

    public RequestCollection(JSONObject args) {
        page = args.getInt("page");
        selectionMode = args.getInt("selectionMode");
    }

    public int getPage() {
        return page;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        JSONObject inner = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY, eventTyp);
        inner.map("page", page);
        inner.map("selectionMode", selectionMode);
        outer.map(JSONAble.ARGUMENTE, inner);
        return outer;
    }
}
