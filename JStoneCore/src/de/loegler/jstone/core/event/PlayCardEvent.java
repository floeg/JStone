package de.loegler.jstone.core.event;


import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird versendet, sobald ein Spieler eine Karte senden möchte sowie sobald der Server das ausspielen akzeptiert.
 * Handelt es sich um einen Diener wird zudem ein {@link DienerPlayedEvent} versendet.
 */
public class PlayCardEvent extends Event {
    public static final String eventTyp = "PlayCardEvent";
    private final int handID;
    private final int side;

    public PlayCardEvent(int handID, int side) {
        this.handID = handID;
        this.side = side;
    }

    public PlayCardEvent(JSONObject args) {
        handID = args.getInt("handID");
        side = args.getInt("side");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toReturn = new JSONObject();
        JSONObject args = new JSONObject();
        toReturn.map(JSONAble.EVENTTYPKEY, eventTyp);
        args.map("handID", handID);
        args.map("side", side);
        toReturn.map(JSONAble.ARGUMENTE, args);
        return toReturn;
    }

    public int getHandID() {
        return handID;
    }

    public int getSide() {
        return side;
    }
}
