package de.loegler.jstone.core.event;

import de.loegler.jstone.core.*;
import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Event liefert eine Liste mit Karten einer Kartenpackung
 */
public class OpenPackCards extends Event {
    public static final String eventTyp = "OpenPackCards";
    private ListX<Karte> cards;
    private int cardAmount;

    public OpenPackCards(ListX<Karte> cards) {
        this.cards = cards;
        this.cardAmount = cards.calculateSize();
    }

    public OpenPackCards(JSONObject args) {
        this.cardAmount = args.getInt("cardAmount");
        this.cards = new ListX<>();
        for (int i = 0; i != cardAmount; i++) {
            JSONObject jsonCard = args.getJSONObject("cards" + i);
            Karte karte;
            if (jsonCard.getString("kartenTyp").equals(Karte.KartenTyp.DIENER.toString())) {
                karte = new Diener(jsonCard);
            } else {
                karte = new Zauber(jsonCard);
            }
            this.cards.append(karte);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject toRet = new JSONObject();
        toRet.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject inner = new JSONObject();
        inner.map("cardAmount", cardAmount);
        for (int i = 0; i != cardAmount; i++) {
            inner.map("cards" + i, cards.get(i).toJSONObject());
        }
        toRet.map(JSONAble.ARGUMENTE, inner);
        return toRet;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public ListX<Karte> getCards() {
        return cards;
    }
}
