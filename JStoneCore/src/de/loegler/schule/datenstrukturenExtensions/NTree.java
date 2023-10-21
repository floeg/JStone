package de.loegler.schule.datenstrukturenExtensions;

/**
 * Darstellung eines Baumes n-ten Grades
 */
public class NTree<T> {

    private NTree<T> parent;
    private ListX<NTree<T>> children = new ListX<>();
    private T content;


    public NTree(T content) {
        this(null, content);
    }

    public NTree(NTree<T> parent, T content) {
        this.content = content;
        this.parent = parent;
    }

    /**
     * @return Den neuen Teilbaum.
     */
    public NTree<T> addChild(T content) {
        NTree<T> tmp = new NTree<>(this, content);
        children.append(tmp);
        return tmp;
    }


    public NTree<T> getParent() {
        return parent;
    }

    public ListX<NTree<T>> getChildren() {
        return children;
    }


    public T getContent() {
        return content;
    }
}
