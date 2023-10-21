package de.loegler.schule.algorithmen;

import de.loegler.schule.datenstrukturen.*;


public class DijkstraKopie {
    private Graph g;

    /**
     * Liste mit unbesuchten Vertex
     */
    /*
    Spart schreibarbeit & Laufzeit - Ansonsten pro Durchlauf alle Vertex überprüfen, ob bereits besucht und damit weiterarbeiten
     */
    private List<Vertex> unvisitedVertices;


    /**
     * Speichert den (aktuell) kürzesten Weg zu einem Knoten mit seinem vorgänger und der Kostensumme
     */
    private List<VertexWegPair> pairList = new List<>();

    private void putList(String key, WegWrapper value){
        boolean gefunden = false;
        for(pairList.toFirst();pairList.hasAccess();pairList.next()){
            if(pairList.getContent().vertexID.equals(key)){
                gefunden = true;
                pairList.getContent().wegWrapper = value;
            }
        }
        if(!gefunden){
            pairList.append(new VertexWegPair(key,value));
        }
    }
    private WegWrapper getValue(String key){
        for(pairList.toFirst();pairList.hasAccess();pairList.next()){
            if(pairList.getContent().vertexID.equals(key))
                return pairList.getContent().wegWrapper;
        }
        return null;
    }


    public DijkstraKopie(Graph g) {
        this.g = g;
    }

    /**
     * Sucht nach dem kürzesten Weg
     *
     * @param start
     * @param goal
     * @return Ein {@link DijkstraReturnWrapper} oder null, falls der Graph für start und goal nicht zusammenhängend ist
     */
    public DijkstraReturnWrapper dijkstra(Vertex start, Vertex goal) {
        unvisitedVertices = new List<>();
        List<Vertex> tmp = g.getVertices();
        for (tmp.toFirst(); tmp.hasAccess(); tmp.next()) {
            unvisitedVertices.append(tmp.getContent());
            WegWrapper wrapper = new WegWrapper(tmp.getContent());
            putList(tmp.getContent().getID(), wrapper);
        }
        /*
        Setze alle Kosten auf unendlich, Vorgänger auf null
        Setze Kosten des Weges vom Startknoten auf 0
        Setze alle Kanten auf nicht besucht
         */
        g.setAllEdgeMarks(false);

        WegWrapper startWrapper = getValue(start.getID());
        startWrapper.vorgaenger = null;
        startWrapper.summeKosten = 0;
        boolean gefunden = false;

        while (!this.unvisitedVertices.isEmpty() && !gefunden) {
            this.unvisitedVertices.toFirst();
            //Suche nach dem Knoten mit den niedrigsten Kosten
            Vertex min = unvisitedVertices.getContent();
            for (unvisitedVertices.next(); unvisitedVertices.hasAccess(); unvisitedVertices.next()) {
                if (getValue(unvisitedVertices.getContent().getID()).summeKosten < getValue(min.getID()).summeKosten) {
                    min = unvisitedVertices.getContent();
                }
            }
            //Kürzester Weg zu diesem Knoten wurde bereits gefunden
            min.setMark(true);
            for (unvisitedVertices.toFirst(); unvisitedVertices.hasAccess(); unvisitedVertices.next()) {
                if (unvisitedVertices.getContent().equals(min)) {
                    unvisitedVertices.remove();
                    break;
                }
            }
            WegWrapper minWeg = getValue(min.getID());
            if (minWeg.summeKosten == Double.POSITIVE_INFINITY) {
                return null;
            } else if (min.equals(goal)) {
                gefunden = true;
            }
            //Bestimme alle Nachbarsknoten
            List<Edge> minKanten = g.getEdges(min);
            for (minKanten.toFirst(); minKanten.hasAccess(); minKanten.next()) {
                Edge edge = minKanten.getContent();
                //Bestimme die Kosten des Weges zu einem Nachbarn über diesen Knoten
                Vertex nachbar = (edge.getVertices()[0] == min) ? edge.getVertices()[1] : edge.getVertices()[0];
                double newCosts = getValue(min.getID()).summeKosten + edge.getWeight();
                double oldCosts = getValue(nachbar.getID()).summeKosten;
                //Wenn der Weg über min günstiger ist als der alte, aktualisiere ihn für den Nachbarn mit den Kosten
                if (oldCosts > newCosts) {
                    getValue(nachbar.getID()).summeKosten = newCosts;
                    getValue(nachbar.getID()).vorgaenger = min;
                }
            }
        }

        List<Vertex> weg = new List<>();
        Stack<Vertex> tmpStack = new Stack<>(); //Zum "umdrehen"
        for (Vertex current = goal; current != null; current = getValue(current.getID()).vorgaenger) {
            tmpStack.push(current);
        }
        for (; !tmpStack.isEmpty(); tmpStack.pop()) {
            weg.append(tmpStack.top());
        }
        double cost = getValue(goal.getID()).summeKosten;
        return new DijkstraReturnWrapper(weg, cost);
    }


    private static class VertexWegPair{
        private String vertexID;
        private WegWrapper wegWrapper;
        public VertexWegPair(String vertex, WegWrapper wegWrapper) {
            this.vertexID = vertex;
            this.wegWrapper = wegWrapper;
        }
    }

    private static class WegWrapper {
        private Vertex element;
        private Vertex vorgaenger;
        private double summeKosten;

        public WegWrapper(Vertex element) {
            this.element = element;
            vorgaenger = null;
            summeKosten = Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Rückgabe des Weges (mit kosten)
     */
    public static class DijkstraReturnWrapper {
        private List<Vertex> wege;
        private double costs;

        public DijkstraReturnWrapper(List<Vertex> wege, double costs) {
            this.wege = wege;
            this.costs = costs;
        }

        /**
         * @return Eine Liste vom Startknoten bis zum Endknoten, sofern ein Weg bis zum Endknoten gefunden wurde
         */
        public List<Vertex> getWege() {
            return wege;
        }

        public double getCosts() {
            return costs;
        }
    }


}


