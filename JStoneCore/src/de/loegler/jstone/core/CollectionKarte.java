package de.loegler.jstone.core;

/**
 * Eine CollectionKarte enthält neben einer Karte noch die Information wie oft ein Spieler diese Karte besitzt.
 * Die Klasse wird für das Collectionsmenu verwendet.
 */
public class CollectionKarte implements JSONAble<CollectionKarte> {
    private Karte karte;
    private int karteAnzahl;

    public CollectionKarte(Karte karte, int karteAnzahl) {
        this.karte = karte;
        this.karteAnzahl = karteAnzahl;
    }

    public CollectionKarte(JSONObject cKarte) {
        karteAnzahl = cKarte.getInt("karteAnzahl");
        JSONObject card = cKarte.getJSONObject("karte");
        if (card.getString("kartenTyp").equals(Karte.KartenTyp.DIENER.toString())) {
            this.karte = new Diener(card);
        } else {
            this.karte = new Zauber(card);
        }
    }

    public Karte getKarte() {
        return karte;
    }

    public int getKarteAnzahl() {
        return karteAnzahl;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.map("karte", karte.toJSONObject());
        jsonObject.map("karteAnzahl", karteAnzahl);
        return jsonObject;
    }
}
