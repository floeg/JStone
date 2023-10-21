package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * EmptyRequest bündelt mehrere Anfragen ohne Argumente, für welche ansonsten eigene
 * Klassen vonnöten wären.
 */
public class EmptyRequest extends Event {
    public static final String eventTyp = "EmptyRequest";
    /**
     * Ein RequestDeckCount wird von einem Client gesendet, sobald er erfahren möchte, wie viele Decks er selber besitzt.
     */
    public static final String REQUEST_DECKCOUNT = "RequestDeckCount";
    public static final String REQUEST_PACKNAMES = "RequestPackNames";
//Freundschaftssystem
    public static final String LOGIN_SUCCESSFUL ="LoginSuccessful";
    public static final String REQUEST_FRIEND_UPDATE="RequestFriendUpdate";


    private String request;

    public EmptyRequest(String request) {
        this.request = request;
    }

    public EmptyRequest(JSONObject args) {
        request = args.getString("request");
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("request", request);
        outer.map(JSONAble.ARGUMENTE, args);
        return outer;
    }

    /**
     * @return Subtyp des Events
     */
    public String getRequest() {
        return request;
    }
}
