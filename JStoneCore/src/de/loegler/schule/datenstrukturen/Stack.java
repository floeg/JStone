package de.loegler.schule.datenstrukturen;

/**
 * Objekte der generischen Klasse Stack verwalten beliebige Objekte vom Typ ContentType nach dem
 * Last-In-First-Out-Prinzip
 */
public class Stack<ContentType> {
    private ListenElement<ContentType> oben;

    /**
     * Ein leerer Stapel wird erzeugt.
     */
    public Stack() {

    }

    /**
     * Die Anfrage liefert den Wert true, wenn der Stapel keine Objekte enthaelt
     * @return true, wenn der Stapel keine Objekte enthaelt, sonst false.
     */
    public boolean isEmpty() {
        return oben == null;
    }

    /**
     * Das Objekt pContent wird oben auf den Stapel gelegt. Falls pContent gleich
     * null ist, bleibt der Stapel unveraendert.
     *
     * @param pContent
     */
    public void push(ContentType pContent) {
        if (pContent != null) {
            ListenElement<ContentType> neu = new ListenElement();
            neu.setInhalt(pContent);
            if (this.isEmpty()) {
                oben = neu;
            } else {
                neu.setNachfolger(oben);
                oben = neu;
            }
        }

    }

    /**
     * Die Anfrage liefert das oberste Stapelobjekt. Der Stapel bleibt unveraendert.
     * Falls der Stapel leer ist, wird null zurueckgegeben.
     *
     * @return
     */
    public ContentType top() {
        if (!this.isEmpty()) {
            return oben.getInhalt();
        }
        return null;

    }

    /**
     * Das zuletzt eingefuegte Objekt wird von dem Stapel entfernt. Falls der Stapel
     * leer ist, bleibt er unveraendert.
     */
    public void pop() {
        if (!this.isEmpty()) {
            oben = oben.getNachfolger();
        }
    }

    private class ListenElement<ContentType> {
        private ContentType inhalt;
        private ListenElement<ContentType> nachfolger;

        protected ContentType getInhalt() {
            return inhalt;
        }

        protected ListenElement getNachfolger() {
            return nachfolger;
        }

        protected void setInhalt(ContentType pContent) {
            inhalt = pContent;
        }

        protected void setNachfolger(ListenElement nf) {
            nachfolger = nf;
        }
    }
}
