package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird vom Client als Anfrage und vom Server als Antwort versendet, sobald das Hauptfenster ein neues Menu Anzeigen soll
 */
public class ChangeStateEvent extends Event {
    public static final String eventTyp = "CHANGESTATEEVENT";

    public static final String MAINMENU = "MAINMENU";
    public static final String FIGHTQUEUE = "FIGHTQUEUE";
    public static final String BATTLEFIELD = "BATTLEFIELD";
    public static final String COLLECTION = "COLLECTION";
    public static final String PACKOPENING = "PackOpening";

    private String changeToState;
    /**
     * @param changeToState Das Menu, zu welchem gewechselt werden soll
     */
    public ChangeStateEvent(String changeToState) {
        this.changeToState = changeToState;
    }


    /**
     * @deprecated Umsteigen auf {@link de.loegler.jstone.core.JSONTools#fromJSON(String)}
     */
    @Deprecated
    public static ChangeStateEvent fromJSONObject(JSONObject argumente) {
        String changeToState = argumente.getString("changeToState");
        return new ChangeStateEvent(changeToState);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toReturn = new JSONObject();
        toReturn.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject arguments = new JSONObject();
        arguments.map("changeToState", changeToState);

        toReturn.map(JSONAble.ARGUMENTE, arguments);
        return toReturn;
    }

    /**
     * @return Menu, zu welchem gewechselt werden soll
     */
    public String getChangeToState() {
        return changeToState;
    }
}
