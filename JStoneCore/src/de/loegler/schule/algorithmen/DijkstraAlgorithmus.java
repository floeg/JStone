package de.loegler.schule.algorithmen;

import de.loegler.schule.datenstrukturen.*;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import java.util.HashMap;

public class DijkstraAlgorithmus {
    private Graph g;
    private Vertex start, goal;
    /**
     * Liste mit unbesuchten Vertex
     */
    /*
    Spart schreibarbeit & Laufzeit - Ansonsten pro Durchlauf alle Vertex überprüfen, ob bereits besucht und damit weiterarbeiten
     */
    private ListX<Vertex> unvisitedVertices;


    /**
     * Speichert den (aktuell) kürzesten Weg zu einem Knoten mit seinem vorgänger und der Kostensumme
     */
    /*
    Verwendet für einen schnelleren Zugriff auf den Weg von einem Vertex v als bei einer (verketteten) Liste, welche eine Laufzeit von
    O(n) hat.
     */
    private HashMap<String, WegWrapper> wege = new HashMap<>();


    public DijkstraAlgorithmus(Graph g) {
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
        unvisitedVertices = new ListX<>();
        List<Vertex> tmp = g.getVertices();
        for (tmp.toFirst(); tmp.hasAccess(); tmp.next()) {
            unvisitedVertices.append(tmp.getContent());
            WegWrapper wrapper = new WegWrapper(tmp.getContent());
            wege.put(tmp.getContent().getID(), wrapper);
        }
        /*
        Setze alle Kosten auf unendlich, Vorgänger auf null
        Setze Kosten des Weges vom Startknoten auf 0
        Setze alle Kanten auf nicht besucht
         */
        g.setAllEdgeMarks(false);

        WegWrapper startWrapper = wege.get(start.getID());
        startWrapper.vorgaenger = null;
        startWrapper.summeKosten = 0;
        boolean gefunden = false;

        while (!this.unvisitedVertices.isEmpty() && !gefunden) {
            this.unvisitedVertices.toFirst();
            //Suche nach dem Knoten mit den niedrigsten Kosten
            Vertex min = unvisitedVertices.getContent();
            for (unvisitedVertices.next(); unvisitedVertices.hasAccess(); unvisitedVertices.next()) {
                if (wege.get(unvisitedVertices.getContent().getID()).summeKosten < wege.get(min.getID()).summeKosten) {
                    min = unvisitedVertices.getContent();
                }
            }
            //Kürzester Weg zu diesem Knoten wurde bereits gefunden
            min.setMark(true);
            unvisitedVertices.remove(min);
            WegWrapper minWeg = wege.get(min.getID());
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
                double newCosts = wege.get(min.getID()).summeKosten + edge.getWeight();
                double oldCosts = wege.get(nachbar.getID()).summeKosten;
                //Wenn der Weg über min günstiger ist als der alte, aktualisiere ihn für den Nachbarn mit den Kosten
                if (oldCosts > newCosts) {
                    wege.get(nachbar.getID()).summeKosten = newCosts;
                    wege.get(nachbar.getID()).vorgaenger = min;
                }
            }
        }

        ListX<Vertex> weg = new ListX<>();
        Stack<Vertex> tmpStack = new Stack<>(); //Zum "umdrehen"
        for (Vertex current = goal; current != null; current = wege.get(current.getID()).vorgaenger) {
            tmpStack.push(current);
        }
        for (; !tmpStack.isEmpty(); tmpStack.pop()) {
            weg.append(tmpStack.top());
        }
        double cost = wege.get(goal.getID()).summeKosten;
        return new DijkstraReturnWrapper(weg, cost);
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

    public static class DijkstraReturnWrapper {
        private ListX<Vertex> wege;
        private double costs;

        public DijkstraReturnWrapper(ListX<Vertex> wege, double costs) {
            this.wege = wege;
            this.costs = costs;
        }

        /**
         * @return Eine Liste vom Startknoten bis zum Endknoten, sofern ein Weg bis zum Endknoten gefunden wurde
         */
        public ListX<Vertex> getWege() {
            return wege;
        }

        public double getCosts() {
            return costs;
        }
    }


}
