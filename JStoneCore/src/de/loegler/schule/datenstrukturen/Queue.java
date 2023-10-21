package de.loegler.schule.datenstrukturen;

/**
 * Objekte der generischen Klasse Queue verwalten beliebige
 * Objekte vom Typ ContentType nach dem First-In-First-Out-Prinzip.
 *
 * @param <ContentType>
 */
public class Queue<ContentType> {
    private ListenElement<ContentType> first, last;

    /**
     * Eine leere Schlange wird erzeugt. Objekte, die in dieser Schlange verwaltet
     * werden, müssen vom Typ ContentType sein.
     */
    public Queue() {

    }

    /**
     * Die Anfrage liefert den Wert true, wenn die Schlange keine Objekte enthält,
     * sonst liefert sie den Wert false.
     */
    public boolean isEmpty() {
        return first == null;

    }

    /**
     * Das Objekt pContent wird an die Schlange angehängt. Falls pContent gleich
     * null ist, bleibt die Schlange unverändert.
     */
    public void enqueue(ContentType pContent) {
        if (pContent != null) {
            ListenElement<ContentType> neu = new ListenElement();
            neu.setInhalt(pContent);
            if (this.isEmpty()) {
                first = neu;
                last = neu;
            } else {
                last.setNachfolger(neu);
                last = neu;
            }

        }

    }

    /**
     * Das erste Objekt wird aus der Schlange entfernt. Falls die Schlange leer ist,
     * wird sie nicht verändert.
     */
    public void dequeue() {
        if (!this.isEmpty()) {
            first = first.getNachfolger();
        }
        if(first==null)
            last=null;
    }

    /**
     * Die Anfrage liefert das erste Objekt der Schlange. Die Schlange bleibt
     * unverändert. Falls die Schlange leer ist, wird null zurückgegeben.
     */
    public ContentType front() {
        if (this.isEmpty()) {
            return null;
        } else {
            return first.getInhalt();
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
