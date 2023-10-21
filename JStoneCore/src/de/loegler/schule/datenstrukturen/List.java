package de.loegler.schule.datenstrukturen;

/**
 *
 * Objekte der generischen Klasse List verwalten beliebig viele, linear angeordnete Objekte
 * vom Typ ContentType. Wenn eine Liste leer ist, vollständig durchlaufen wurde oder das
 * aktuelle Objekt am Ende der Liste gelöscht wurde, gibt es kein aktuelles Objekt.
 */
public class List<ContentType> {

    private ListenElement<ContentType> first, current, last;

    /**
     * Eine leere Liste wird erzeugt.
     */
    public List() {

    }

    /**
     * Die Anfrage liefert den Wert true, wenn die Liste keine Objekte
     * enthält, sonst liefert sie den Wert false.
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Falls die Liste nicht leer ist, es ein aktuelles Objekt gibt und dieses nicht das letzte Objekt der Liste ist,
     * wird das dem aktuellen Objekt in der Liste folgende Objekt zum aktuellen Objekt, andernfalls gibt es nach
     * Ausführung des Auftrags kein aktuelles Objekt.
     */
    public void next() {

        if (this.hasAccess()) {
            if (current != last) {
                current = current.getNachfolger();
            } else {
                current = null;
            }
        }
    }

    /**
     * Ein neues Objekt pContent wird am Ende der Liste eingefügt. Das aktuelle Objekt bleibt unverändert. Wenn die
     * Liste leer ist, wird das Objekt pContent in die Liste eingefügt und es gibt weiterhin kein aktuelles Objekt.
     */
    public void append(ContentType pContent) {
        if (pContent != null) {
            ListenElement<ContentType> neu = new ListenElement();
            neu.setInhalt(pContent);
            if (this.isEmpty()) {
                first = neu;
                last = neu;
                current = neu;
            } else {
                last.setNachfolger(neu);
                last = neu;
            }
        }

    }

    /**
     * Die Anfrage liefert den Wert true, wenn es ein aktuelles Objekt gibt,sonst liefert sie den Wert false.
     */
    public boolean hasAccess() {
        return current != null;
    }

    /**
     * Falls es ein aktuelles Objekt gibt, wird ein neues Objekt pContent vor dem aktuellen Objekt
     * in die Liste eingefügt. Das aktuelle Objekt bleibt unverändert.
     * Falls die Liste leer ist, wird pContent in die Liste eingefügt und es gibt weiterhin kein aktuelles Objekt.
     * Falls es kein aktuelles Objekt gibt und die Liste nicht leer ist oder pContent==null ist,
     * bleibt die Liste unverändert.
     */
    public void insert(ContentType pContent) {
        if (pContent != null) {
            ListenElement<ContentType> neu = new ListenElement();
            neu.setInhalt(pContent);
            if (this.isEmpty()) {
                this.append(pContent);
            } else {
                if (current == first) {
                    neu.setNachfolger(current);
                    first = neu;
                } else {
                    ListenElement<ContentType> merke = current;
                    this.toFirst();
                    while (current.getNachfolger() != merke) {
                        this.next();
                    }
                    current.setNachfolger(neu);
                    neu.setNachfolger(merke);
                    current = merke;
                }

            }
        }

    }

    /**
     * Falls es ein aktuelles Objekt gibt, wird das aktuelle Objekt zurückgegeben. Andernfalls
     * gibt die Anfrage den Wert null zurück.
     */
    public ContentType getContent() {
        if (this.hasAccess()) {
            return current.getInhalt();
        } else {
            return null;
        }

    }

    /**
     * Falls es ein aktuelles Objekt gibt und pContent ungleich null ist, wird das aktuelle Objekt
     * durch pContent ersetzt. Sonst bleibt die Liste unverändert.
     */
    public void setContent(ContentType pContent) {
        if (this.hasAccess()) {
            if (pContent != null) {
                current.setInhalt(pContent);
            }
        }
    }

    /**
     * Falls die Liste nicht leer ist, wird das erste Objekt der Liste aktuelles Objekt. Ist die Liste leer,
     * geschieht nichts.
     */
    public void toFirst() {
        if (!this.isEmpty()) {
            current = first;
        }
    }

    /**
     * Falls die Liste nicht leer ist, wird das letzte Objekt der Liste aktuelles Objekt. Ist die Liste leer, geschieht nichts.
     */
    public void toLast() {
        if (!this.isEmpty()) {
            current = last;
        }

    }

    /**
     * Falls es ein aktuelles Objekt gibt, wird das aktuelle Objekt gelöscht und das Objekt hinter dem
     * gelöschten Objekt wird zum aktuellen Objekt. Wenn die Liste leer ist oder es kein aktuelles Objekt gibt, bleibt die Liste unverändert.
     */
    public void remove() {
        if (this.hasAccess()) {
            ListenElement<ContentType> merke = current;
            this.toFirst();

            if (merke == current) {
                this.current = current.getNachfolger();
                this.first = this.current;
                return;
            }


            while (current.getNachfolger() != merke) {
                this.next();
            }
            if (last != merke) {
                current.setNachfolger(merke.getNachfolger());
                this.next();
            } else {
                current.setNachfolger(null);
                last = current;
                current = null;
            }
        }
    }

    /**
     * Die Liste pList wird an die Liste angehängt. Anschließend wird pList eine leere Liste. Das aktuelle Objekt
     * bleibt unverändert. Falls es sich bei der Liste und pList um dasselbe Objekt handelt, pList==null oder eine
     * leere Liste ist, bleibt die Liste unverändert.
     */
    public void concat(List<ContentType> pList) {
        if (pList != null && !pList.isEmpty() && pList != this) {
            if (this.isEmpty()) {
                first = pList.first;
                current = first;

            } else {
                last.setNachfolger(pList.first);
            }
            this.last = pList.last;
            pList = null;
        }
    }

    private class ListenElement<ContentType> {
        private ContentType inhalt;
        private ListenElement<ContentType> nachfolger;

        protected ContentType getInhalt() {
            return inhalt;
        }

        protected void setInhalt(ContentType pContent) {
            inhalt = pContent;
        }

        protected ListenElement<ContentType> getNachfolger() {
            return nachfolger;
        }

        protected void setNachfolger(ListenElement nf) {
            nachfolger = nf;
        }
    }
}
