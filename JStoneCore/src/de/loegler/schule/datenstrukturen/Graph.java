package de.loegler.schule.datenstrukturen;

public class Graph {
    private List<Edge> edges;
    private List<Vertex> vertices;


    public Graph() {
        edges = new List<>();
        vertices = new List<>();
    }

    /**
     * Der Auftrag fügt den Knoten pVertex vom Typ Vertexin den Graphen ein, sofern
     * es noch keinen Knoten mit demselben ID-Eintrag wie pVertex im Graphen gibt
     * und pVertex eine ID ungleich nullhat. Ansonsten passiert nichts
     *
     * @param pVertex
     */
    public void addVertex(Vertex pVertex) {
        if (pVertex == null || pVertex.getID() == null) return;
        if (vertexVorhandfen(pVertex)) {
            return;
        }

        vertices.append(pVertex);
    }

    /**
     * Der Auftrag fügt die Kante pEdgein den Graphen ein, sofern beide durch die
     * Kante verbundenen Knoten im Graphen enthalten sind, nicht identisch sind und
     * noch keine Kante zwischen den beiden Knoten existiert. Ansonsten passiert
     * nichts.
     *
     * @param pEdge
     */
    public void addEdge(Edge pEdge) {
        if (pEdge == null || pEdge.getVertices() == null || pEdge.getVertices()[0].equals(pEdge.getVertices()[1]))
            return;
        if (vertexVorhandfen(pEdge.getVertices()[0]) && vertexVorhandfen(pEdge.getVertices()[1])) {
            if (this.getEdge(pEdge.getVertices()[0], pEdge.getVertices()[1]) == null) {
                this.edges.append(pEdge);
            }
        }
    }

    /**
     * Die Anfrage liefert das Knotenobjekt mit pID als ID. Ist ein
     * solchesKnotenobjekt nicht im Graphen enthalten, wird null zurueckgeliefert.
     *
     * @param pID
     * @return
     */
    public Vertex getVertex(String pID) {
        for (this.vertices.toFirst(); vertices.hasAccess(); vertices.next()) {
            if (vertices.getContent().getID().equals(pID)) return vertices.getContent();
        }
        return null;
    }

    /**
     * Der Auftrag entfernt den Knoten pVertex aus dem Graphen und loescht alle
     * Kanten, die mit ihm inzident sind. Ist der Knoten pVertex nicht im Graphen
     * enthalten, passiert nichts.
     *
     * @param pVertex
     */
    public void removeVertex(Vertex pVertex) {
        if (pVertex != null && this.getVertex(pVertex.getID()) != null) {

            for (this.edges.toFirst(); this.edges.hasAccess(); this.edges.next()) {
                if (edges.getContent().getVertices()[0] == pVertex || edges.getContent().getVertices()[1] == pVertex) {
                    edges.remove();
                }
            }
        }
        // Entfernen aus Vertices
        for (this.vertices.toFirst(); this.vertices.hasAccess(); this.vertices.next()) {
            if (this.vertices.getContent() == pVertex) {
                this.vertices.remove();
                break;
            }
        }
    }


    /**
     * Der Auftrag entfernt die Kante pEdge aus dem Graphen. Ist die Kante pEdge
     * nicht im Graphen enthalten, passiert nichts.
     *
     * @param pEdge
     */
    public void removeEdge(Edge pEdge) {
        if (pEdge != null) {
            for (this.edges.toFirst(); this.edges.hasAccess(); this.edges.next()) {
                if (this.edges.getContent() == pEdge) {
                    this.edges.remove();
                    break;
                }
            }
        }
    }

    /**
     * Die Anfrage liefert eine neue Liste aller inzidenten Kanten zum Knoten
     * pVertex. Hat der Knoten pVertexkeine inzidenten Kanten in diesem Graphen oder
     * ist gar nicht in diesem Graphen enthalten, so wird eine leere Liste
     * zurückgeliefert.
     *
     * @param pVertex
     * @return
     */
    public List<Edge> getEdges(Vertex pVertex) {
        List<Edge> tmp = new List<>();
        if (pVertex == null || this.getVertex(pVertex.getID()) == null) return tmp;
        else {
            for (this.edges.toFirst(); this.edges.hasAccess(); this.edges.next()) {
                Vertex v1 = this.edges.getContent().getVertices()[0];
                Vertex v2 = this.edges.getContent().getVertices()[1];
                if (v1 == pVertex || v2 == pVertex) {
                    tmp.append(this.edges.getContent()); //
                }
            }
            return tmp;
        }
    }

    /**
     * Die Anfrage liefert die Kante, welche die Knoten pVertexund pAnotherVertex
     * verbindet, als Objekt vom Typ Edge. Ist der Knoten pVertex oder der Knoten
     * pAnotherVertexnicht im Graphen enthalten oder gibt es keine Kante, die beide
     * Knoten verbindet, so wird null zurueckgeliefert.
     *
     * @param pVertex
     * @param pAVertex
     * @return
     */
    public Edge getEdge(Vertex pVertex, Vertex pAVertex) {

        for (this.edges.toFirst(); edges.hasAccess(); edges.next()) {
            Vertex v1 = edges.getContent().getVertices()[0];
            Vertex v2 = edges.getContent().getVertices()[1];
            if ((v1 == pVertex && v2 == pAVertex) || v2 == pVertex && v1 == pAVertex) {
                return edges.getContent();
            }
        }
        return null;
    }

    /**
     * Die Anfrage liefert alle Nachbarn des Knotens pVertex als neue Liste vom Typ
     * List<Vertex>. Hat der Knoten pVertex keine Nachbarn in diesem Graphen oder ist
     * gar nicht in diesem Graphen enthalten, so wird eine leere Liste
     * zurueckgeliefert.
     */
    public List<Vertex> getNeighbours(Vertex pVertex) {
        List<Vertex> nachbarn = new List<>();
        if (pVertex == null || this.getVertex(pVertex.getID()) == null) return nachbarn;
        else {
            for (edges.toFirst(); edges.hasAccess(); edges.next()) {
                if ((edges.getContent().getVertices()[0] == pVertex)) {
                    nachbarn.append(edges.getContent().getVertices()[1]);
                } else {
                    if (edges.getContent().getVertices()[1] == pVertex) {
                        nachbarn.append(edges.getContent().getVertices()[0]);
                    }
                }
            }

        }

        return nachbarn;

    }

    public boolean allEdgesMarked() {
        for (this.edges.toFirst(); this.edges.hasAccess(); this.edges.next()) {
            if (!this.edges.getContent().isMarked()) return false;
        }

        return true;

    }

    /**
     * Der Auftrag setzt die Markierungen aller Kanten des Graphen auf den Wert
     * pMark.
     *
     * @param pMark
     */
    public void setAllEdgeMarks(boolean pMark) {
        for (this.edges.toFirst(); this.edges.hasAccess(); this.edges.next()) {
            this.edges.getContent().setMark(pMark);
        }
    }

    /**
     * @param pVertex
     * @return
     * @deprecated Nutzen: getVertex(pVertex.getid())!=null
     */
    private boolean vertexVorhandfen(Vertex pVertex) {
        for (vertices.toFirst(); vertices.hasAccess(); vertices.next()) {
            if (vertices.getContent().getID().equals(pVertex.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Die Anfrage liefert true, wenn die Markierungen aller Knoten des Graphen den
     * Wert true haben, ansonsten false.
     *
     * @return
     */
    public boolean allVerticesMarked() {
        for (this.vertices.toFirst(); this.vertices.hasAccess(); this.vertices.next()) {
            if (!this.vertices.getContent().isMarked()) return false;
        }

        return true;
    }

    /**
     * Der Auftrag setzt die Markierungen aller Knoten des Graphen auf den Wert
     * pMark.
     */
    public void setAllVertexMarks(boolean pMark) {
        for (this.vertices.toFirst(); this.vertices.hasAccess(); this.vertices.next()) {
            this.vertices.getContent().setMark(pMark);

        }

    }

    public boolean isEmpty() {
        return this.vertices.isEmpty();
    }

    /**
     * Die Anfrage liefert eine neue Liste aller Knotenobjekte vom Typ List<Vertex>.
     * Enthaelt der Graph keine Knotenobjekte, so wird eine leere Liste
     * zurueckgeliefert.
     *
     * @return
     */
    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }


    public boolean breitensuche(Vertex start, Vertex toFind) {
        Queue<Vertex> schlange = new Queue<>();
        this.setAllEdgeMarks(false);
        schlange.enqueue(start);

        for (; !schlange.isEmpty(); schlange.dequeue()) {
            if (schlange.front().getID().equals(toFind.getID())) {
                return true;
            } else {

                schlange.front().setMark(true);
                List<Vertex> tmp = this.getNeighbours(schlange.front());
                for (tmp.toFirst(); tmp.hasAccess(); tmp.next()) {
                    if (!tmp.getContent().isMarked()) {
                        schlange.enqueue(tmp.getContent());
                        tmp.getContent().setMark(true);

                    }

                }

            }

        }
        return false;
    }


    public boolean tiefenSuche(Vertex v, Vertex zuSuchen) {
        return this.pTiefensuche(v, zuSuchen);
    }

    private boolean isEqual(Vertex v1, Vertex v2) {
        return v1.getID().equals(v2.getID());
    }


    private boolean pTiefensuche(Vertex v, Vertex zuSuchen) {
        if (v.getID().equals(zuSuchen.getID())) {
            return true;
        }
        v.setMark(true);
        List<Vertex> knoten = this.getNeighbours(v);


        for (knoten.toFirst(); knoten.hasAccess(); knoten.next()) {
            if (!knoten.getContent().isMarked()) {
                boolean b = pTiefensuche(knoten.getContent(), zuSuchen);
                if (b) return true;
            }
        }
        return false;
    }


    public Graph kruskal() {
        this.setAllEdgeMarks(false);
        Graph neuerGraph = new Graph();
        List<Edge> kanten = this.getEdges();
        Queue<Edge> kantenQueue = new Queue<>();
        while (!kanten.isEmpty()) {
            kanten.toFirst();
            Edge min = kanten.getContent();
            for (; kanten.hasAccess(); kanten.next()) {
                if (min.getWeight() > kanten.getContent().getWeight()) {
                    min = kanten.getContent();
                }
            }
            kantenQueue.enqueue(min);
            for (kanten.toFirst(); kanten.hasAccess(); kanten.next()) {
                if (kanten.getContent().equals(min)) {
                    kanten.remove();
                    break;
                }
            }
        }
        for (; !kantenQueue.isEmpty(); kantenQueue.dequeue()) {
            Vertex v1 = kantenQueue.front().getVertices()[0];
            Vertex v2 = kantenQueue.front().getVertices()[1];
            if (!(v1.isMarked() && v2.isMarked())) {
                v1.setMark(true);
                v2.setMark(true);
                neuerGraph.addVertex(v1);
                neuerGraph.addVertex(v2);
                neuerGraph.addEdge(kantenQueue.front());
            }
        }
        return neuerGraph;
    }
}
