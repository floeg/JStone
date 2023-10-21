package de.loegler.jstone.core;


import de.loegler.schule.datenstrukturenExtensions.ListX;

/**
 * Klasse repräsentiert das Schlachtfeld.
 * Zentraler Speicherort für alle Aktionen des Schlachtfeldes.
 */
public class Schlachtfeld {
    private Spieler[] spieler;
    private ListX<Diener>[] diener = new ListX[]{new ListX<Diener>(), new ListX<Diener>()};
    private int spielerAmZug;

    private int[] maxMana = new int[2];
    private int[] currentMana = new int[2];
    private Karte[][] hand = new Karte[2][10];
    public void addCardToHand(int side, Karte karte) {
        Karte[] hand = this.hand[side];
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] == null) {
                hand[i] = karte;
                return;
            }
        }
    }

    /**
     * @param side Spielnummer des Spielers
     * @param handNR Index der Hand, Start bei 0
     * @return Eine Karte in der Hand des Spielers, oder null, falls es sie nicht gibt
     */
    public Karte getHand(int side, int handNR) {
        return hand[side][handNR];
    }

    public void setMaxMana(int player, int newMax) {
        maxMana[player] = newMax;
    }

    public void setCurrentMana(int player, int newCurrent) {
        currentMana[player] = newCurrent;
    }

    public int getMaxMana(int player) {
        return maxMana[player];
    }

    public int getCurrentMana(int player) {
        return currentMana[player];
    }

    public int getDienerCount(int gameNumber) {

        return diener[gameNumber].calculateSize();
    }

    public void playDiener(int spielerAmZug, Diener d) {
        diener[spielerAmZug].append(d);
    }

    /**
     * Entfernt eine Karte aus der Hand eines Spielers und verschiebt die restlichen Karten nach vorne
     * @param spielerAmZug Spielnummer
     * @param handID Index der Hand, Start bei 0
     */
    public void removeCard(int spielerAmZug, int handID) {
        this.hand[spielerAmZug][handID] = null;
        for (int i = handID + 1; i != hand[spielerAmZug].length; i++) {
            hand[spielerAmZug][i - 1] = hand[spielerAmZug][i]; //Verschiebt die Karten im Array nach vorne
        }
    }

    public Karte[] getHand(int side) {
        return hand[side];
    }

    public void removeDiener(int side, Diener d) {
        diener[side].remove(d);
    }

    public ListX<Diener> getDiener(int side) {
        return diener[side];
    }
}
