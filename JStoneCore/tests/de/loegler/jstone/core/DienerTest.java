package de.loegler.jstone.core;

import org.junit.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DienerTest {
    @Test
    void kampfTest() {
        Schlachtfeld schlachtfeld = new Schlachtfeld();
        Diener d1 = new Diener(2, 5);
        Diener d2 = new Diener(4, 5);
        d2.setCanAttack(true);
        d2.angreifen(d1, schlachtfeld, 0);
        assertEquals(1, d1.getLeben());
        assertEquals(3, d2.getLeben());
        //d1 kann in diesem Zug nicht angreifen --> Von den Leben sollte sich nichts ändern
        d1.angreifen(d2, schlachtfeld, 0);
        assertEquals(1, d1.getLeben());
        assertEquals(3, d2.getLeben());

        //d1 hat Gottesschild und wird angegriffen -->
        d1.addEffect(Diener.DienerEffekt.GOTTESSCHILD);
        d2.setCanAttack(true);
        d2.angreifen(d1, schlachtfeld, 0);
        assertEquals(1, d1.getLeben());
        assertFalse(d1.hasEffect(Diener.DienerEffekt.GOTTESSCHILD));
        //d2 wird geheilt
        d2.setLeben(5);
        //d1 ist giftig --> Leben von d2 statt 5-2 auf 0 gesetzt da giftig
        d1.addEffect(Diener.DienerEffekt.GIFTIG);
        d1.setCanAttack(true);
        d1.angreifen(d2, schlachtfeld, 0);
        assertEquals(0, d2.getLeben());
    }
}