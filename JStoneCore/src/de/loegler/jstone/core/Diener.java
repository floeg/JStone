package de.loegler.jstone.core;


import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Die Klasse Diener repräsentiert einen Diener im Spiel.
 * Jeder Status der Klasse muss mit der Diener Instanz des Servers/ des Clients über {@link de.loegler.jstone.core.event.Event}s synchronisiert werden.
 */
public class Diener extends Karte {
    private int angriff;
    private int leben;
    private boolean canAttack;
    private int kampfschreiID;
    private int todesroechelnID;
    private ListX<DienerEffekt> dienerEffekte;


    /**
     * Karte welche dem Gegner übertragen wird, um die Karten des Spielers zu verbergen.
     *
     * @return Ein leerer Diener mit dem Namen ?
     */
    public static Diener getMysteriousDiener() {
        return new Diener("?", "Unbekannt, bis dein Gegner sie ausspielt.", -1,
                KartenTyp.DIENER, Seltenheit.NICHT_SAMMELBAR, -1, -1, 1, 1, new ListX<>());
    }

    /**
     * Veraltet aus dem alten Projekt, um die TestGui nutzen zu können.
     *
     * @param angriff
     * @param leben
     * @deprecated Nutze: {@link #Diener(String, String, int, KartenTyp, Seltenheit, int, int, int, int, ListX)}
     */
    @Deprecated
    public Diener(int angriff, int leben) {
        super("TODO", "ENTFERNEN", 9, KartenTyp.DIENER, Seltenheit.SELTEN);
        this.angriff = angriff;
        this.leben = leben;
        this.dienerEffekte = new ListX<>();
        canAttack = false;
    }


    /**
     * Erstellt einen neuen Diener.
     *
     * @param name            Der Name des Dieners
     * @param beschreibung    Die Beschreibung des Dieners
     * @param manakosten      Die Manakosten des Dieners
     * @param kartenTyp       Der {@link de.loegler.jstone.core.Karte.KartenTyp} des Dieners
     * @param seltenheit      Die {@link #seltenheit} des Dieners
     * @param kampfschreiID   Der Kampfschreieffekt des Dieners
     * @param todesroechelnID Der Todesroechelneffekt des Dieners
     * @param angriff         Der Angriff des Dieners
     * @param leben           Die Leben des Dieners
     * @param dienerEffekte   Die {@link DienerEffekt} des Dieners
     */
    public Diener(String name, String beschreibung, int manakosten, KartenTyp kartenTyp, Seltenheit seltenheit, int kampfschreiID, int todesroechelnID, int angriff, int leben, ListX<DienerEffekt> dienerEffekte) {
        super(name, beschreibung, manakosten, kartenTyp, seltenheit);
        this.kampfschreiID = kampfschreiID;
        this.todesroechelnID = todesroechelnID;
        this.angriff = angriff;
        this.leben = leben;
        if (dienerEffekte != null)
            this.dienerEffekte = dienerEffekte;
    }


    /**
     * Initialisiert einen Diener auf Grundlage eines JSONObjects.
     *
     * @param diener Der Diener als JSONObject
     */
    public Diener(JSONObject diener) {
        super(diener);
        String kampfschreiID = diener.getString("kampfschreiID");
        String todesroechelnID = diener.getString("todesroechelnID");

        if (kampfschreiID != null && !kampfschreiID.isEmpty()) {
            this.kampfschreiID = Integer.parseInt(kampfschreiID);
        }
        if (todesroechelnID != null && !todesroechelnID.isEmpty()) {
            this.todesroechelnID = Integer.parseInt(todesroechelnID);
        }
        angriff = diener.getInt("angriff");
        leben = diener.getInt("leben");
        JSONObject effekte = diener.getJSONObject("effekte");
        dienerEffekte = new ListX<>();
        if (effekte != null) {
            for (DienerEffekt value : DienerEffekt.values()) {
                if (effekte.getString(value.toString()) != null && effekte.getString(value.toString()).contains("true")) {
                    dienerEffekte.append(value);
                }
            }
        }
    }


    /**
     * @param playerMana        Die aktuellen Manakristalle
     * @param playerDienerCount Die aktuelle Anzahl an Dienern des Spielers
     * @return true, wenn die Karte ausgespielt werden kann
     */
    @Override
    public boolean canPlay(int playerMana, int playerDienerCount) {
        if (super.canPlay(playerMana, playerDienerCount)) {
            return playerDienerCount < 7;
        }
        return false;
    }

    /**
     * @param effekt Der Effekt, welche überprüft werden soll
     * @return true, wenn der Diener den Effekt besitzt.
     */
    public boolean hasEffect(DienerEffekt effekt) {
        return dienerEffekte.contains(effekt);
    }

    //Gehört eher zu Clientbattle/ServerBattle - Aufgrund der Wiederverwendbarkeit jedoch hier
    public boolean canAttack(Schlachtfeld schlachtfeld, Diener attackable, int thisSide) {
        if (this == (attackable)) {
            return false;
        }
        if (this.canAttack) {
            if (attackable instanceof Diener) {
                if (((Diener) attackable).hasEffect(DienerEffekt.SPOTT))
                    return true;
            }
            boolean oneHasSpott = false;
            ListX<Diener> tmp = schlachtfeld.getDiener(1-thisSide);
            for(tmp.toFirst();tmp.hasAccess();tmp.next()){
                Diener it = tmp.getContent();
                if(it.hasEffect(DienerEffekt.SPOTT))
                    oneHasSpott=true;
            }

            return !oneHasSpott;
        }
        return false;
    }

    /**
     * Rückgabe, ob der Diener sterben würde, wenn er das Ziel
     * angreift.
     */
    public boolean wouldDie(Diener target) {
        if (hasEffect(DienerEffekt.GOTTESSCHILD))
            return false;
        if (target.hasEffect(DienerEffekt.GIFTIG))
            return true;
        return getLeben() < target.getAngriff();
    }


    /**
     * Rückgabe, ob der Diener den gegnerischen Helden angreifen kann.
     *
     * @param schlachtfeld Das aktuelle Schlachtfeld
     * @param thisSide     Die Seite des Dieners.
     * @return true, wenn der Diener angreifen kann und kein Gegner mit Spott im weg steht.
     */
    public boolean canAttackEnemyHero(Schlachtfeld schlachtfeld, int thisSide) {
        ListX<Diener> diener = schlachtfeld.getDiener(1 - thisSide);
        for (diener.toFirst(); diener.hasAccess(); diener.next()) {
            if (diener.getContent().hasEffect(DienerEffekt.SPOTT))
                return false;
        }
        return canAttack;
    }


    /**
     * Greift einen anderen Diener an
     *
     * @param ziel         Das Ziel, welches angegriffen werden soll
     * @param schlachtfeld Das Schlachtfeld
     * @param thisSide     k
     */
    public void angreifen(Diener ziel, Schlachtfeld schlachtfeld, int thisSide) {
        if (this.dienerEffekte.contains(DienerEffekt.GIFTIG)) {
            if (ziel.dienerEffekte.contains(DienerEffekt.GOTTESSCHILD)) {
                ziel.dienerEffekte.remove(DienerEffekt.GOTTESSCHILD);
            } else ziel.setLeben(0);
        } else {
            ziel.nehmeSchaden(this.angriff);
        }
        //Angreifer nimmt Schaden
        if (ziel.dienerEffekte.contains(DienerEffekt.GIFTIG)) {
            if (this.dienerEffekte.contains(DienerEffekt.GOTTESSCHILD))
                this.dienerEffekte.remove(DienerEffekt.GOTTESSCHILD);
            else this.setLeben(0);
        } else {
            this.nehmeSchaden(ziel.angriff);
        }
        this.canAttack = false;
    }

    /**
     * Reduziert die Leben des Dieners um Angriff, beachtet dabei Effekte wie {@link DienerEffekt#GOTTESSCHILD}
     *
     * @param angriff Der Angriff des anderen Dieners
     */
    public void nehmeSchaden(int angriff) {
        if (dienerEffekte.contains(DienerEffekt.GOTTESSCHILD))
            dienerEffekte.remove(DienerEffekt.GOTTESSCHILD);
        else
            this.setLeben(leben - angriff);
    }

    /**
     * @return Der Angriff des Dieners
     */
    public int getAngriff() {
        return angriff;
    }

    /**
     * Ändert den Angriff des Dieners
     *
     * @param angriff Der neue Angriff
     */
    public void setAngriff(int angriff) {
        this.angriff = angriff;
    }

    /**
     * Entfernt einen Effekt, falls er vorhanden war.
     *
     * @param effekt
     */
    public void removeEffect(DienerEffekt effekt) {
        dienerEffekte.remove(effekt);
    }

    /**
     * Fügt einen neuen Effekt hinzu, falls er noch nicht vorhanden war.
     *
     * @param effekt
     */
    public void addEffect(DienerEffekt effekt) {
        if (!dienerEffekte.contains(effekt))
            dienerEffekte.append(effekt);
    }

    /**
     * @return Rückgabe der aktuellen Leben
     */
    public int getLeben() {
        return leben;
    }
    /**
     * @param newLeben Die neuen Leben
     */
    public void setLeben(int newLeben) {
        this.leben = newLeben;
    }

    /**
     * @return true, wenn der Diener nicht neu ausgespielt wurde und noch nicht angegriffen hat
     */
    public boolean isCanAttack() {
        return canAttack;
    }
    /**
     * @param canAttack Ändert, ob ein Diener angreifen kann.
     */
    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public int getKampfschreiID() {
        return kampfschreiID;
    }

    public int getTodesroechelnID() {
        return todesroechelnID;
    }

    /**
     * Wandelt den Diener in ein JSONObject um.
     *
     * @return Der Diener als JSONObject
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject diener = super.toJSONObject();
        diener.map("kartenTyp", KartenTyp.DIENER.toString());
        JSONObject effekte = new JSONObject();
        diener.map("effekte", effekte);
        dienerEffekte.forEach(it -> effekte.map(it.toString(), "'true'"));
        //Ansonsten gibt es einen Fehler, da das JSONObject leer ist
        // Leeres JSON extra behandeln
        if (dienerEffekte.isEmpty())
            effekte.map(DienerEffekt.GOTTESSCHILD.toString(), "'false'");
        diener.map("leben", this.leben + "");
        diener.map("angriff", this.angriff + "");
        diener.map("canAttack", "'" + canAttack + "'");
        return diener;
    }

    /**
     * Effekte, welche ein Diener besitzen kann.
     */
    public enum DienerEffekt {
        GOTTESSCHILD, SPOTT, GIFTIG
    }
}
