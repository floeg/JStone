package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

public abstract class Event implements JSONAble<Event> {
    /**
     * Erstellt ein neues Event Objekt
     */
    public Event() {
    }

    /**
     * Wandelt einen String in ein JSONObject um.
     *
     * @param json
     */
    public Event(JSONObject json) {
    }
}
