package de.loegler.jstone.core;

public class Zauber extends Karte {
    private int aktionsID;

    /**
     * Instanziierung auf basis eines {@link JSONObject}.
     * Aufruf über Argumente, ohne eventTyp
     *
     * @param jsonObject
     */
    public Zauber(JSONObject jsonObject) {
        super(jsonObject);
        this.aktionsID = jsonObject.getInt("aktionsID");
    }

    public Zauber(String name, String beschreibung, int manakosten, KartenTyp kartenTyp, Seltenheit seltenheit, int zAktionsID) {
        super(name, beschreibung, manakosten, kartenTyp, seltenheit);
        this.aktionsID = zAktionsID;
    }

    /**
     * @return Die ID der Aktion in der Datenbank
     */
    public int getAktionsID() {
        return aktionsID;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject karte = super.toJSONObject();
        karte.map("kartenTyp", KartenTyp.ZAUBER.toString());
        karte.map("aktionsID", aktionsID);
        return karte;
    }
}
