package de.loegler.jstone.core.event;

import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;
import de.loegler.jstone.core.JSONTools;
import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Die Klasse <code>ListEvent</code> wird verwendet um eine {@link de.loegler.schule.datenstrukturen.List} an Daten zu übertragen.
 * Zwischen einzelnen <code>ListEvent</code>s ist durch {@link #getTyp()} eine genauere Unterscheidung möglich.
 */
public class ListEvent extends Event{
    /**
     * Wird verwendet um ein {@link JSONObject} als ListEvent zu erkennen
     */
    public static final String eventTyp = "ListEvent";
    public static final String FRIEND_REQUESTS = "FriendRequests",FRIEND_LIST = "FRIEND_LIST";

    private ListX<String> data;
    private int amount;
    private String typ;

    /**
     * Erstellt ein neues ListEvent
     * @param typ Der Subtyp des Events
     * @param data Die Liste
     */
    public ListEvent(String typ, ListX<String> data){
        this.typ=typ;
        this.data=data;
        this.amount=data.calculateSize();
    }

    /**
     * Hilfsmethode zum Umwandeln einer Liste aus {@link JSONAble}s zu einer Liste aus Strings
     * @param objects Liste mit {@link JSONAble} Objekten, welche in einen String umgewandelt werden
     * @return Eine Liste aus Strings welche die Objekte repräsentieren
     */
    public static ListX<String> convertToStringList(ListX<JSONAble> objects){
        ListX<String> list = new ListX<>();
        objects.forEach(it ->{
            String json = JSONTools.getJSON(it.toJSONObject());
            list.append(json);
        });
        return list;
    }


    public ListEvent(JSONObject args){
        typ = args.getString("typ");
        amount = args.getInt("amount");
        this.data = new ListX<>();
        for(int i=0;i!=amount;i++){
            data.append(args.getString("data"+i));
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        JSONObject inner = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY,eventTyp);
        inner.map("amount",amount);
        inner.map("typ",typ);
        data.forEachIndexed((it,i) ->{
            inner.map("data"+i, it);
        });
        outer.map(JSONAble.ARGUMENTE,inner);
        return outer;
    }

    /**
     * @return Der Subevent-Typ
     */
    public String getTyp() {
        return typ;
    }

    /**
     * @return Anzahl der Elemente in der Liste
     */
    public int getAmount() {
        return amount;
    }
    /**
     * @return Übertragene Liste
     */
    public ListX<String> getData() {
        return data;
    }
}
