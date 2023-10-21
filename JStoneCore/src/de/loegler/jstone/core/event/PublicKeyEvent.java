package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

import java.math.BigInteger;

/**
 * Wird versendet um den öffentlichen Schlüssel von Client und Server auszutauschen (RSA)
 */
public class PublicKeyEvent extends Event {
    public static final String eventTyp = "PUBLICKEYEVENT";
    private final BigInteger senderE;
    private final BigInteger senderN;
    private boolean requestOtherKey = false;

    public PublicKeyEvent(BigInteger senderE, BigInteger thisN) {
        this.senderE = senderE;
        this.senderN = thisN;
    }

    public static PublicKeyEvent fromJSONObject(JSONObject argumente) {
        String eString = argumente.getString("e");
        String nString = argumente.getString("n");
        BigInteger e = new BigInteger(eString);
        BigInteger n = new BigInteger(nString);
        PublicKeyEvent tmp = new PublicKeyEvent(e, n);
        if ("'true'".equalsIgnoreCase(argumente.getString("requestOther")))
            tmp.requestOtherKey = true;

        return tmp;
    }

    public boolean isRequestOtherKey() {
        return requestOtherKey;
    }

    /**
     * Wenn auf true, fordert das gegenüber auf mit einem eigenen {@link PublicKeyEvent} zu antworten.
     * @param requestOtherKey Wird der andere Key benötigt?
     */
    public void setRequestOtherKey(boolean requestOtherKey) {
        this.requestOtherKey = requestOtherKey;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toReturn = new JSONObject();
        toReturn.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject arguments = new JSONObject();
        arguments.map("e", senderE.toString());
        arguments.map("n", senderN.toString());
        arguments.map("requestOther", "'" + requestOtherKey + "'");
        toReturn.map("argumente", arguments);
        return toReturn;
    }

    public BigInteger getSenderE() {
        return senderE;
    }

    public BigInteger getSenderN() {
        return senderN;
    }
}
