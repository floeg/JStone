package de.loegler.jstone.server.aktionen;

import de.loegler.jstone.core.Diener;
import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Schnittstelle zur Ausführung von Aktionen
 */
public interface AktionenAPI {

    /**
     * @param playerNumber Die Spielnummer des Spielers
     */
    void drawCard(int playerNumber);


    int getDeckCardsLeft(int playerNumber);

    /**
     * @return Die Spielnummer des aktiven Spielers, 0 oder 1.
     */
    int getCurrentGameNumber();

    /**
     * Fügt einem Charakter Schaden zu.
     *
     * @param side   Die Seite, entsprechend der GameNumber
     * @param id     - Die ID des Charakters. 0 fuer den Helden, 1-7 fuer Diener.
     * @param damage Der Schaden
     */
    void dealDamage(int side, int id, int damage);

    /**
     * Rückgabe einer Liste aller Diener eines Spielers
     *
     * @param side Die Spielnummer des Spielers
     * @return Eine Liste aller Diener eines Spielers
     */
    ListX<Diener> getDiener(int side);

    void summonMinion(int kid, int anzahl, int side);

    void healHero(int side, int amount);

}
