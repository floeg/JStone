package de.loegler.jstone.core.event;

import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;

/**
 * Wird versendet, sobald sich der Status eines Dieners ändert.
 * Fordert den Client dazu auf, den Diener mit der entsprechenden SlotID zu entfernen und durch den übergebenen Diener zu ersetzen.
 */
public class ChangeMinionStateEffect extends Event{
    public static final String eventTyp = "ChangeMinionStateEffect";
    private Diener newDiener;
    private int dienerID;
    private int sideGameNumber;

    /**
     * Erstellt ein neues Event Objekt
     */
    public ChangeMinionStateEffect(Diener newDiener, int dienerID, int sideGameNumber) {
        this.newDiener = newDiener;
        this.dienerID = dienerID;
        this.sideGameNumber = sideGameNumber;
    }

    public ChangeMinionStateEffect(JSONObject args){
        this.dienerID=args.getInt("dienerID");
        this.sideGameNumber=args.getInt("sideGameNumber");
        JSONObject newDienerJSON = args.getJSONObject("newDiener");
        this.newDiener= new Diener(newDienerJSON);
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject outer = new JSONObject();
        outer.map(JSONAble.EVENTTYPKEY,eventTyp);
        JSONObject args = new JSONObject();
        args.map("newDiener", newDiener.toJSONObject());
        args.map("dienerID",dienerID);
        args.map("sideGameNumber",sideGameNumber);
        outer.map(JSONAble.ARGUMENTE,args);
        return outer;
    }

    public Diener getNewDiener() {
        return newDiener;
    }

    public int getDienerID() {
        return dienerID;
    }

    public int getSideGameNumber() {
        return sideGameNumber;
    }
}
