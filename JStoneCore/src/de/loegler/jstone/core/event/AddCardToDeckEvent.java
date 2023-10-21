package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird versendet, sobald ein Client eine Karte zu seinem Deck hinzufügen möchte (Anfrage)
 * sowie nachdem der Server die Karte zu dem Kartenbuffer hinzugefügt hat (Bestätigung).
 * @see ChangeStateEvent#COLLECTION
 */
public class AddCardToDeckEvent extends Event {
    public static final String eventTyp = "AddCardToDeckEvent";
    private final int page;
    private final int index;
    private final int filter;

    /**
     * Erstellt ein neues Event-Objekt zur Übertragung
     * @param page Die aktuelle Seitennummer, Start bei 0
     * @param index Der Index er Karte auf der Seite, Start bei 0
     * @param filter Ein möglicher Filter, aktuell ungenutzt
     */
    public AddCardToDeckEvent(int page, int index, int filter) {
        this.page = page;
        this.index = index;
        this.filter = filter;
    }

    /**
     * @return Die aktuelle Seitennummer
     */
    public int getPage() {
        return page;
    }

    /**
     * @return Der Index der Karte, Start bei 0
     */
    public int getIndex() {
        return index;
    }

    public int getFilter() {
        return filter;
    }

    public AddCardToDeckEvent(JSONObject args) {
        page = args.getInt("page");
        index = args.getInt("index");
        filter = args.getInt("filter");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("page", page);
        args.map("index", index);
        args.map("filter", filter);
        outer.map(JSONAble.ARGUMENTE, args);
        return outer;

    }
}
