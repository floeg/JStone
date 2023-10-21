package de.loegler.jstone.core.event;

import de.loegler.jstone.core.*;

/**
 * Wird vom Server versendet, sobald ein Spieler eine Karte zieht.
 * Wird nicht versendet, wenn der Spieler keine Karten mehr im Deck hat. In diesem Fall erleidet der Spieler
 * Schaden, sodass ein {@link DamageEvent} versendet wird.
 */
public class DrawCardEvent extends Event {
    private Karte karte;
    private final int side;
    public static final String eventTyp = "DRAWCARDEVENT";

    public DrawCardEvent(Karte karte, int side) {
        this.karte = karte;
        this.side = side;
    }

    public DrawCardEvent(JSONObject args) {
        JSONObject karte = args.getJSONObject("karte");
        String kartenTyp = karte.getString("kartenTyp");
        if (Karte.KartenTyp.DIENER.toString().equalsIgnoreCase(kartenTyp)) {
            this.karte = new Diener(karte);
        } else {
            this.karte = new Zauber(karte);
        }
        side = args.getInt("side");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject event = new JSONObject();
        event.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject arguments = new JSONObject();
        JSONObject karteJSONObject = karte.toJSONObject();
        arguments.map("karte", karteJSONObject);
        arguments.map("side", side);
        event.map(JSONAble.ARGUMENTE, arguments);
        return event;
    }

    public Karte getKarte() {
        return karte;
    }

    /**
     * @return Spielnummer des betroffenen Spielers
     */
    public int getSide() {
        return side;
    }
}
