package de.loegler.jstone.core.event;

import de.loegler.jstone.core.CollectionKarte;
import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;
import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Event wird vom Server gesendet, sobald der Client eine Collection Seite angefordert hat.
 * Event löst beim Client das anzeigen der Seite aus, sofern sich der Client im PackOpeningPanel befindet.
 */
public class RespondCollection extends Event {
    public static final String eventTyp = "RespondCollection";
    private ListX<CollectionKarte> karten;
    private int page;

    public RespondCollection(ListX<CollectionKarte> karten, int page) {
        this.karten = karten;
        this.page = page;
    }

    public RespondCollection(JSONObject args) {
        page = args.getInt("page");
        karten = new ListX<>();
        for (int i = 0; i != 15; i++) {
            JSONObject karte = args.getJSONObject("karte" + i);
            if (karte != null) {
                CollectionKarte karte1 = new CollectionKarte(karte);
                karten.append(karte1);
            }
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.map(JSONAble.EVENTTYPKEY, eventTyp);
        JSONObject args = new JSONObject();
        args.map("page", page);
        karten.forEachIndexed((it, i) -> args.map("karte" + i, it.toJSONObject()));
        jsonObject.map(JSONAble.ARGUMENTE, args);
        return jsonObject;
    }

    public ListX<CollectionKarte> getKarten() {
        return karten;
    }

    public int getPage() {
        return page;
    }
}
