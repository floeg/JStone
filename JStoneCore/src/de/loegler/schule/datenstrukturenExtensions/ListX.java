package de.loegler.schule.datenstrukturenExtensions;

import de.loegler.schule.datenstrukturen.List;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Die Klasse ListX erweitert die List der Dokumentation um einige Methoden.
 * Hinweis: Die meisten Methoden verändern das aktuelle Element der Liste.
 *
 * @param <T> Inhaltstyp der Liste
 */
public class ListX<T> extends List<T> {

    /**
     * @param index Der Index
     * @return Rückgabe des index. Elements
     */
    public T get(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Index " + index + " ist kleiner als 0!");

        toFirst();
        for (int i = 0; i != index; i++)
            this.next();
        return getContent();
    }

    /**
     * Builder-Methode/Wrapper
     *
     * @return instance
     */
    public ListX<T> goNext() {
        super.next();
        return this;
    }

    /**
     * Rückgabe des Folgeelements und änderung von dem aktuellen Element.
     *
     * @return Das Folgeelement
     */
    public T getNext() {
        super.next();
        return super.getContent();
    }

    /**
     * Rückgabe des ersten Index, an welchem das gesuchte Element vorkommt.
     * Verglich über equals
     */
    public int getIndex(T element) {
        int i = 0;
        for (toFirst(); hasAccess(); next(), i++) {
            if (getContent().equals(element))
                return i;

        }
        return -1;
    }

    /**
     * Rückgabe, ob item in der Liste vorhanden ist.
     * current zeigt anschließend auf item oder auf null (sollte item nicht in der Liste vorhanden sein)
     */
    public boolean contains(T item) {
        for (toFirst(); hasAccess(); next())
            if (getContent().equals(item))
                return true;
        return false;

    }

    /**
     * Führt die gegebenen Aktion für alle Elemente der Liste aus.
     *
     * @param action Die Aktion welche für jedes Element ausgeführt werden soll
     * @see #forEachIndexed(BiConsumer)
     */
    public void forEach(Consumer<? super T> action) {
        for (this.toFirst(); hasAccess(); next())
            action.accept(getContent());
    }

    /**
     * Führt eine gegebene Aktion für jedes Element der Liste aus. Übergibt zudem einen Index.
     *
     * @param indexAction Die Aktion, welche für jedes Element ausgeführt werden soll.
     * @see #forEach(Consumer)
     */
    public void forEachIndexed(BiConsumer<? super T, Integer> indexAction) {
        int i = 0;
        for (this.toFirst(); hasAccess(); next())
            indexAction.accept(getContent(), i++);

    }

    /**
     * Entfernt ein Item, sollte es vorhanden sein. Entfernt das erste vorkommen.
     * Nebeneffekt: Verändert den Index der Liste.
     *
     * @param item Das Element, welches entfernt werden soll.
     */
    public void remove(T item) {
        for (this.toFirst(); this.hasAccess(); next()) {
            if (getContent().equals(item)) {
                this.remove();
                break;
            }
        }
    }

    /**
     * Berechnet die Größe der Liste!
     * Laufzeit beträgt n
     */
    public int calculateSize() {
        int count = 0;
        for (toFirst(); hasAccess(); next()) {
            count++;
        }
        return count;


    }
}
