package de.loegler.schule.algorithmen;

import de.loegler.schule.datenstrukturen.ComparableContent;
import de.loegler.schule.datenstrukturen.List;



public class QuickSort<T extends ComparableContent<T>> {
    private List<T> liste;


    public int getListSize() {
        int i = 0;
        for (liste.toFirst(); liste.hasAccess(); liste.next()) {
            i++;
        }
        return i;
    }

    public void quick(int start, int end) {
        if ((end - start) <= 1) {
            return;
        }
        int pivotIndex = getPivot(start, end);
        this.goTo(pivotIndex);
        T pivotContent = liste.getContent();
        int i = start; // Zeigt auf den Index (wird immer mit next() erhöht)
        for (goTo(start); i < end; ) {
            if (i == pivotIndex) {
                i++;
                liste.next();
            } else if (liste.getContent().isLess(pivotContent)) {
                if (i > pivotIndex) {
                    T tmpContent = liste.getContent();
                    liste.remove();
                    goTo(pivotIndex);
                    liste.insert(tmpContent);
                    pivotIndex++;
                    i++; // i war rechts vor Pivot, daher ist i++ auch hinter dem Pivotelement
                    goTo(i);

                } else { // liste i bereits richtig
                    i++;
                    liste.next();
                }
            } else {
                if (i < pivotIndex) {
                    pivotIndex--;
                    T tmpContent = liste.getContent();
                    liste.remove();
                    liste.append(tmpContent); // i ist jetzt Element hinter altem i; indexPosition jedoch =

                } else { // liste i bereits richtig
                    i++;
                    liste.next();
                }
            }
        }
        quick(start, pivotIndex);
        quick(pivotIndex + 1, end);
    }

    /**
     * Mittlerer Wert von Start, Ende-1 und Mitte
     *
     * @param start
     * @param end
     * @return
     */
    private int getPivot(int start, int end) {
        // Nimmt 3 Elemente der Liste, sortiert diese mit Bubblesort (einfachste
        // Variante) und gibt die mitte wieder
        goTo(start);
        T first = liste.getContent();
        goTo(end - 1);
        T last = liste.getContent();

        int midIndex = start + (end - 1 - start) / 2; // (start+end)/2 --> Könnte zu Überlauf führen
        goTo(midIndex);
        T mid = liste.getContent();

        Object[] tmp = new Object[]{first, mid, last};
        int[] tmpIndex = new int[]{start, midIndex, end};

        for (int z = 0; z != tmp.length; z++) {
            for (int i = 0; i != tmp.length - 1; i++) {
                if (((T) tmp[i]).isGreater((T) tmp[i + 1])) {
                    T tmpV = (T) tmp[i];
                    tmp[i] = tmp[i + 1];
                    tmp[i + 1] = tmpV;
                    int tmpI = tmpIndex[i];
                    tmpIndex[i] = tmpIndex[i + 1];
                    tmpIndex[i + 1] = tmpI;
                }
            }
        }
        return tmpIndex[1];
    }


    static void printList(List<Integer> list) {
        for (list.toFirst(); list.hasAccess(); list.next()) {
            System.out.println(list.getContent());
        }
    }

    public QuickSort(List<T> list) {
        this.liste = list;
    }

    public void goTo(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Der Index ist kleiner als 0 (index=" + index + ")");

        liste.toFirst();

        for (; index != 0; liste.next()) {
            index--; // Optimierung der Schleife -> volatile
        }
    }
}
