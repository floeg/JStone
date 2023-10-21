package de.loegler.jstone.core;

import de.loegler.schule.datenstrukturen.Stack;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import java.util.Random;

/**
 * Repräsentiert ein Deck eines Spielers
 */
public class Deck extends Stack<Karte> {
    private ListX<Karte> karten;
    private int kartenAnzahl=0;
    private Klasse klasse;

    /**
     * Erzeugt ein neues, gemischtes Deck
     *
     * @param karten
     */
    public Deck(ListX<Karte> karten, Klasse klasse) {
       this(karten,klasse,true);
    }

    /**
     * Erzeugt ein neues Deck. Mischt es, falls mixCards gesetzt ist.
     * @param karten Liste mit Karten des Decks
     * @param klasse Klasse des Decks
     * @param mixCards true, falls das Deck gemischt werden soll
     */
    public Deck(ListX<Karte> karten, Klasse klasse, boolean mixCards){
        this.karten = karten;
        this.klasse = klasse;
        if(mixCards)
            mischen();
    }

    /**
     * Mischt den Kartenstapel
     * Wichtig: Aktuell muss das Deck aus 30 Karten bestehen
     */
    private void mischen() {
        Random rand = new Random();
        for (int i = 30; i != 0; i--) {
            karten.toFirst();
            for (int z = rand.nextInt(i); z != 0; z--) {
                karten.next();
            }
            this.push(karten.getContent());
            karten.remove();
        }
    }

    @Override
    public void push(Karte k){
        super.push(k);
        kartenAnzahl++;
    }

    @Override
    public void pop(){
        super.pop();
        kartenAnzahl--;
    }

    /**
     * @return Die Klasse des Decks.
     */
    public Klasse getKlasse() {
        return klasse;
    }

    public int getKartenAnzahl() {
        return kartenAnzahl<0 ? 0 : kartenAnzahl;
    }
}
