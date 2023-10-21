package de.loegler.jstone.core;

import java.util.HashMap;

/**
 * Ein <code>JSONObject</code> speichert alle angegebenen Zustände eines Objektes in einer Datenstruktur.
 */
public class JSONObject {
    /**
     * String: Name im JSON; Object: Entweder als String oder als JSONObject gespeichert
     */
    protected HashMap<String, Object> data = new HashMap<>();


    public String toString() {
        return "JSONObject:  Y" + data.toString() + "  Y";
    }


    public void map(String key, String value) {
        data.put(key, value);
    }

    public void map(String key, JSONObject value) {
        data.put(key, value);
    }

    public void map(String key, int value) {
        data.put(key, value + "");
    }

    /**
     * @return value oder null, falls der Key nicht vorhanden ist oder Value kein String ist
     */
    public String getString(String key) {

        Object value = data.get(key);
        if (value instanceof String)
            return (String) value;
        else return null;
    }

    public JSONObject getJSONObject(String key) {
        Object value = data.get(key);
        if (value instanceof JSONObject)
            return (JSONObject) value;
        else return null;
    }

    /**
     *
     * @param key
     * @return Rückgabe einer Zahl, falls der key existiert und der String eine Zahl darstellt
     */
    public Integer getInt(String key) {
        Object value = data.get(key);
        if (value instanceof String) {
            return Integer.parseInt((String) value); //TODO riskant
        }
        return null;
    }

    /*
    Automatisch generiert - benötigt, damit JSONObjects geschachtelt gespeichert und schnell wiedergefunden werden können
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONObject that = (JSONObject) o;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
