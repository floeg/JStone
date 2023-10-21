package de.loegler.schule.datenstrukturen;

public class Vertex {
    private final String pID;
    private boolean marked;

    public Vertex(String pID) {
        this.pID = pID;
    }

    public String getID() {
        return pID;
    }

    public boolean isMarked() {
        return marked;
    }
    public void setMark(boolean marked) {
        this.marked = marked;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (marked != vertex.marked) return false;
        return pID.equals(vertex.pID);
    }

    @Override
    public int hashCode() {
        int result = pID.hashCode();
        result = 31 * result + (marked ? 1 : 0);
        return result;
    }
}
