package de.loegler.jstone.core.event;

import de.loegler.jstone.core.*;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RespondCollectionTest {

    @Test
    void getKarten() {
        ListX<CollectionKarte> karten = new ListX<>();
        for (int i = 0; i != 15; i++) {
            karten.append(new CollectionKarte(Karte.getEmptyCardForEnemy(), 0));
        }
        JSONObject args = null;
        RespondCollection respondCollection = new RespondCollection(karten, 0);
        String json = JSONTools.getJSON(respondCollection.toJSONObject());
        System.out.println(json);
        try {
            args = JSONTools.fromJSON(json).getJSONObject(JSONAble.ARGUMENTE);
        } catch (JSONTools.JSONMalformedException e) {
            e.printStackTrace();
        }
        RespondCollection newRes = new RespondCollection(args);
        assertNotNull(newRes.getKarten());
        newRes.getKarten().forEach(it -> {
            assertNotNull(it.getKarte());
            System.out.println(it.getKarte());
        });
    }
}