package de.loegler.jstone.core;
/**
 * Objekte welche JSONAble sind besitzen einen Konstruktor, welcher ein {@link JSONObject} annimmt, wodurch sie instanziiert werden.
 * Weiterhin besitzen sie eine Methode toJSONObject, welche das Objekt in ein {@link JSONObject} umwandeln.
 */
public interface JSONAble<T> {
    String ARGUMENTE = "argumente";
    String EVENTTYPKEY = "eventTyp";

    JSONObject toJSONObject();
}
