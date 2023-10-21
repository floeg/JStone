package de.loegler.schule.datenstrukturen;

/**
 * Die Klasse PriorityQueue repräsentiert eine Queue in welcher Objekte Prioritaeten zugewiesen werden koennen wodurch diese weiter an den Anfang rücken.
 *
 * @param <ContentType>
 */
public class PriorityQueue<ContentType> {

    private List<PriorityContent<ContentType>> schlange;

    /**
     * Erzeugt eine neue PriorityQueue
     */
    public PriorityQueue() {
        schlange = new List<>();
    }

    /**
     * Entfernt ein Element aus der Schlange
     */
    public void deqeueu() {
        schlange.toFirst();
        schlange.remove();

    }


    /**
     * Sortiert ein Element nach der Prioritaet in die Schlange ein.
     * Bei gleicher Prioritaet wird das Objekt hinter das letzte Objekt mit dieser Prioritaet gesetzt.
     *
     * @param content
     * @param prio
     */
    public void enqueue(ContentType content, int prio) {

        PriorityContent<ContentType> neu = new PriorityContent<>(prio);
        neu.setContent(content);
        this.schlange.toFirst();
        while (schlange.getContent() != null && schlange.getContent().getPrio() >= neu.getPrio()) {
            schlange.next();
        }
        if (schlange.getContent() == null)
            schlange.append(neu);
        else
            schlange.insert(neu);
    }


    /**
     * Rueckgabe des ersten Elements der Schlange
     *
     * @return
     */
    public ContentType front() {
        schlange.toFirst();
        if (schlange.getContent() == null) return null;
        return schlange.getContent().getContent();
    }


    /**
     * Private Klasse zur Verknuepfung der Prioritaet mit den QueueNodes
     *
     * @param <ContentType>
     */
    private class PriorityContent<ContentType> {
        private int prio;
        private ContentType content;
        private PriorityContent nachfolger;


        protected PriorityContent(int prio) {
            this.prio = prio;

        }


        /**
         * Rueckgabe der Prioritaet des PriorityContents
         *
         * @return
         */
        protected int getPrio() {
            return prio;
        }


        /**
         * Rueckgabe des contents des PriorityContent-Objekts
         *
         * @return
         */
        protected ContentType getContent() {
            return this.content;
        }

        /**
         * Rueckgabe des Nachfolgers des PriorityContents - kann <code>null</code> sein (letztes Element der Liste)
         *
         * @return
         */
        protected PriorityContent<ContentType> getNachfolger() {
            return this.nachfolger;
        }

        /**
         * Setzt den Nachfolger
         *
         * @param priorityContent der (neue) Nachfolger
         * @return
         */
        protected PriorityContent<ContentType> setNachfolger(PriorityContent priorityContent) {
            this.nachfolger = priorityContent;
            return this;
        }

        /**
         * Setzen des Inhalts
         *
         * @param content
         * @return
         */
        protected PriorityContent<ContentType> setContent(ContentType content) {
            this.content = content;
            return this;
        }


    }


}
