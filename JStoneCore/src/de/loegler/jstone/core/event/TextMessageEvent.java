package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Die Klasse TestMessageEvent bündelt mehrere Events welche aus nur einer einzelnen
 * Textantwort bestehen, um die Anzahl der Klassen zu minimieren.
 */
public class TextMessageEvent extends Event {

    public static final String eventTyp = "TextRespond";

    public static final String DECK_COUNT_RESPOND = "DeckCountRespond";
    public static final String CHOSE_DECK_REQUEST = "ChoseDeckRequest";
    /**
     * Client möchte eine Kartenpackung öffnen.
     */
    public static final String PACK_OPEN_REQUEST = "PackOpenRequest";
    public static final String PACK_NAMES_RESPOND = "PackNamesRespond";
    public static final String LOGIN_FAILED="LoginFailed";

    /**
     * Wird vom Client als Anfrage versendet, wenn er wissen möchte, um wie viele Ecken er mit x Befreundet ist
     * Wird als Antwort vom Server versendet, sobald ausgerechnet wurde, um wie viele Ecken er mit x Befreundet ist
     */
    public static final String ECKEN_BEFREUNDET = "EckenBefreundet";

    /**
     * Client möchte Freund hinzufügen
     */
    public static final String REQUEST_FRIEND="ClientRequestFriend";
    /**
     * Client möchte eine Freundschaftsanfrage annehmen
     */
    public static final String ACCEPT_FRIEND="ClientAcceptFriend";
    /**
     * Client lehnt eine Freundschaftsanfrage ab
     */
    public static final String DENY_FRIEND = "ClientDenyFriend";

    /**
     * Bringt den Client dazu ein neues TextFrame im {@link de.loegler.core.gui.TextFrame.TextFrameOptions#WARNING_MODE} mit der entsprechenden Nachricht anzuzeigen
     */
    public static final String WARNING_MESSAGE = "WarningMessage";

    /**
     * Bringt den Client dazu ein neues TextFrame mit der entsprechenden Nachricht anzuzeigen.
     */
    public static final String INFO_MESSAGE = "InfoMessage";

    private String respondName;
    private String respondValue;


    public TextMessageEvent(String respondName, String respondValue) {
        this.respondName = respondName;
        this.respondValue = respondValue;
    }

    public TextMessageEvent(JSONObject args) {
        this.respondName = args.getString("respondName");
        this.respondValue = args.getString("respondValue");
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("respondName", respondName);
        args.map("respondValue", respondValue);
        outer.map(JSONAble.ARGUMENTE, args);
        return outer;
    }

    /**
     * @return Der Subtyp des Events
     */
    public String getRespondName() {
        return respondName;
    }

    /**
     * @return Die eigentliche Nachricht
     */
    public String getRespondValue() {
        return respondValue;
    }
}
