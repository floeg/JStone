package de.loegler.schule.datenstrukturen;

public class Edge {
    private boolean marked;
    private Vertex[] vertices;
    private double weight;

    public Edge(Vertex pVertex, Vertex pAnotherVertex, double pWeight) {
        vertices = new Vertex[2];
        vertices[0] = pVertex;
        vertices[1] = pAnotherVertex;
        this.weight = pWeight;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMark(boolean mark) {
        this.marked = mark;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Vertex[] getVertices() {
        return vertices;
    }


}
