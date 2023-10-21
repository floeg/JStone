package de.loegler.jstone.core;

/**
 * Repräsentiert eine Karte
 */
public abstract class Karte implements JSONAble {

    protected int manakosten;
    protected KartenTyp kartenTyp;
    protected Seltenheit seltenheit;

    private String name, beschreibung;
    private String bildName;

    /**
     * Instanziierung auf basis eines {@link JSONObject}.
     * Aufruf über Argumente, ohne eventTyp
     *
     * @param jsonObject
     */
    public Karte(JSONObject jsonObject) {
        manakosten = jsonObject.getInt("manakosten");
        name = jsonObject.getString("name");
        beschreibung = jsonObject.getString("beschreibung");
        bildName = jsonObject.getString("bildName");


        for (Seltenheit value : Seltenheit.values()) {
            if (value.toString().equalsIgnoreCase(jsonObject.getString("seltenheit")))
                seltenheit = value;
        }

        for (KartenTyp value : KartenTyp.values()) {
            if (value.toString().equalsIgnoreCase(jsonObject.getString("kartenTyp")))
                kartenTyp = value;
        }

    }

    public Karte(String name, String beschreibung, int manakosten, KartenTyp kartenTyp, Seltenheit seltenheit) {
        this.manakosten = manakosten;
        this.kartenTyp = kartenTyp;
        this.seltenheit = seltenheit;
        this.name = name;
        this.beschreibung = beschreibung;
    }

    /**
     * Liefert eine leere Karte.
     */
    public static Karte getEmptyCardForEnemy() { //Der Spieler soll nicht wissen, welche Karten sein Gegner auf der Hand hat
        Karte k = new Zauber("?", "Unbekannt, bis dein Gegner sie ausspielt.", -1000, KartenTyp.ZAUBER, Seltenheit.NICHT_SAMMELBAR, -1);
        k.setBild("mysteriousCard.png");
        return k;
    }

    public static Karte getEmptyCardForCollection() {
        Karte k = getEmptyCardForEnemy();
        k.setBeschreibung("Der Weg zur nächsten Erweiterung ist nicht mehr weit...");
        return k;
    }

    public static Karte getEmptyCardForOpening() {
        Karte k = getEmptyCardForEnemy();
        k.setBeschreibung("Klicke hier, um zu erfahren was sich hinter dieser Karte verbirgt!");
        return k;
    }

    protected void setBeschreibung(String s) {
        this.beschreibung = s;
    }




    /**
     * Rückgabe, ob eine Karte ausgespielt werden kann, sollte der Spieler am Zug sein.
     *
     * @return
     */
    public boolean canPlay(int playerMana, int playerDienerCount) {
        return playerMana >= manakosten;
    }

    /**
     * Rückgabe des Bildes, oder den leer String, sollte das Bild nicht gesetzt worden sein.
     *
     * @return
     */
    public String getBild() {
        return bildName == null ? "" : bildName;
    }

    public void setBild(String bildName) {
        this.bildName = bildName;
    }

    public int getManakosten() {
        return manakosten;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject core = new JSONObject();
        core.map("name", getName());
        core.map("beschreibung", getBeschreibung());
        core.map("manakosten", manakosten);
        core.map("bildName", bildName);
        core.map("seltenheit", seltenheit.toString());
        return core;
    }

    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public enum KartenTyp {
        DIENER, ZAUBER
    }

    public enum Seltenheit {
        LEGENDAER, EPISCH, SELTEN, GEWOEHNLICH, NICHT_SAMMELBAR

    }

    public Seltenheit getSeltenheit() {
        return seltenheit;
    }
}
