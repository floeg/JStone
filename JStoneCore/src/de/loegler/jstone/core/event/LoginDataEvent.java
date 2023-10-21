package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

import java.math.BigInteger;

/**
 * Ein LoginDataEvent wird versendet, sobald sich ein Client anmelden möchte.
 */
public class LoginDataEvent extends Event {
    public static final String eventTyp = "LOGINDATAEVENT";

    private String username;
    private BigInteger cryptoPW;
    //Wird gesetzt, wenn der Client einen neuen Account anlegen wollte
    private boolean register = false;


    public LoginDataEvent(String username, BigInteger cryptoPW) {
        this.username = username;
        this.cryptoPW = cryptoPW;
    }

    /**
     * Ersetzt bald die Statische Methode
     * @param args
     */
    public LoginDataEvent(JSONObject args) {
       super(args);
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public static String getEventTyp() {
        return eventTyp;
    }

    public String getUsername() {
        return username;
    }

    public BigInteger getCryptoPW() {
        return cryptoPW;
    }

    public boolean isRegister() {
        return register;
    }


    @Deprecated
    /**
     * @deprecated Nutze Konstruktor
     */
    public static LoginDataEvent fromJSONObject(JSONObject object) {
        String username = object.getString("username");
        BigInteger cryptoPW = new BigInteger(object.getString("cryptoPW"));
        LoginDataEvent toReturn = new LoginDataEvent(username, cryptoPW);
        if ("'true'".equalsIgnoreCase(object.getString("register")))
            toReturn.setRegister(true);

        return toReturn;
    }

    /**
     * Wandelt das aktuelle Objekt in ein JSONObject um
     * @return Ein JSONObject welches dieses Objekt darstellt.
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject toReturn = new JSONObject();
        toReturn.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject inner = new JSONObject();
        inner.map("username", username);
        inner.map("cryptoPW", cryptoPW.toString());
        inner.map("register", "'" + register + "'");
        toReturn.map(JSONAble.ARGUMENTE, inner);
        return toReturn;
    }
}
