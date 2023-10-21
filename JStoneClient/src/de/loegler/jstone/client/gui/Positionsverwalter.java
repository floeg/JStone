package de.loegler.jstone.client.gui;

import de.loegler.schule.datenstrukturen.List;

import java.awt.*;

/**
 * Klasse zur Verwaltung der Positionen
 *
 */
public class Positionsverwalter {
    protected List<Rectangle> s1Hand1, s1Hand2;
    protected List<Rectangle> s1HandFull = new List<>(), s2HandFull = new List<>();
    protected List<Rectangle> s2Hand1, s2Hand2;
    protected List<Rectangle> s1Spielfeld, s2Spielfeld;
    protected Rectangle s1Held, s2Held, s1HeldenF, s2HeldenF;
    protected Rectangle zugBeenden;
    protected Rectangle[] manaleisten;
    //Jeder Spieler besitzt eine Hand welche aus 2. teilen besteht
    protected Positionsverwalter() {
        manaleisten = new Rectangle[2];
        s1Hand1 = new List<>();
        s1Hand2 = new List<>();
        s2Hand1 = new List<>();
        s2Hand2 = new List<>();
        s1Spielfeld = new List<>();
        s2Spielfeld = new List<>();
        s1Held = new Rectangle(750, 10, 200, 200);
        s2Held = new Rectangle(750, 800, 200, 200);
        s1HeldenF = new Rectangle(960, 120, 70, 120);
        s2HeldenF = new Rectangle(960, 780, 70, 120);
        manaleisten[0] = new Rectangle(1550, 250, 300, 50);
        manaleisten[1] = new Rectangle(1550, 700, 300, 50);
        this.zugBeenden = new Rectangle(1550, 450, 300, 50);
        int w = 150;
        int h = 200;
        int hh = 250;
        for (int i = 0; i != 7; i++) {
            // 350
            int x = 200 + (w * i + i * 50);
            s1Spielfeld.append(new Rectangle(x, hh, 100, 200));
            s2Spielfeld.append(new Rectangle(x, hh + 250, 100, 200));
        }
        for (int i = 0; i != 5; i++) {
            int x = 10 + (w * i + i * 5);
            this.s2Hand1.append(new Rectangle(x, 800, 100, 200));
            int x2 = 1100 + (w * i + i * 5);
            this.s2Hand2.append(new Rectangle(x2, 800, 100, 200));
            this.s1Hand1.append(new Rectangle(x, 10, 100, 200));
            this.s1Hand2.append(new Rectangle(x2, 10, 100, 200));
        }
        for (s1Hand1.toFirst(); s1Hand1.hasAccess(); s1Hand1.next()) {
            s1HandFull.append(s1Hand1.getContent());
        }
        for (s1Hand2.toFirst(); s1Hand2.hasAccess(); s1Hand2.next()) {
            s1HandFull.append(s1Hand2.getContent());
        }
        for (s2Hand1.toFirst(); s2Hand1.hasAccess(); s2Hand1.next()) {
            s2HandFull.append(s2Hand1.getContent());
        }
        for (s2Hand2.toFirst(); s2Hand2.hasAccess(); s2Hand2.next()) {
            s2HandFull.append(s2Hand2.getContent());
        }
    }
}
    
