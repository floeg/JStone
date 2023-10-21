package de.loegler.jstone.server.aktionen;

import java.util.HashMap;

/**
 * Darstellung von Aktionen. Im Gegensatz zu den Events haben alle Aktionen den Typ Aktion.
 * Hierdurch sind sie enger an die Klasse {@link AktionsManager} gebunden.
 */
public class Aktion {
    private String aktionsTyp;
    private HashMap<String, String> parameter = new HashMap<>();

    public Aktion(String aktionsTyp) {
        this.aktionsTyp = aktionsTyp;
    }

    public Aktion putArgument(String key, String value) {
        parameter.put(key, value);
        return this;
    }

    public String getArgument(String key) {
        return parameter.get(key);
    }


    public String getAktionsTyp() {
        return aktionsTyp;
    }

    public enum Namenssammlung {
        DRAW_CARD, DRAW_CARD_AMOUNT, SUMMON_MINION, SUMMON_AMOUNT, HEAL_HERO,
        HEAL_AMOUNT, HERO_DAMAGE, TARGET_SIDE, DAMAGE_AMOUNT, SUMMON_RANDOM,
        SQL_STATEMENT,LEG_ISOTOPE,LEG_AB_JRR //Legendäre Karten haben teilweise einzigartige Effekte. Diese gehören konkret zur Karte.
    }
}
