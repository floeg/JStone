package de.loegler.schule.datenstrukturen;

public class BinarySearchTree<ContentType extends ComparableContent<ContentType>> {

    private ContentType inhalt;
    private BinarySearchTree<ContentType> rechts, links;

    /**
     * @param liste
     * @param nochLevel
     * @return Ist noch eine Ebene vorhanden?
     */
    private boolean levelorder(List<ContentType> liste, int nochLevel) {
        if (isEmpty()) return false;// Keine weitere Ebene vorhanden
        if (nochLevel == 0) {
            liste.append(this.inhalt);
            return true;
        } // Wenn links oder rechts noch mind. eine Ebene vorhanden ist muss weitergesucht
        // werden
        return links.levelorder(liste, nochLevel - 1) | rechts.levelorder(liste, nochLevel - 1); // !!! Ein | = beide
        // Seiten werden
        // ausgewertet/ausgefuehrt
    }

    private List<ContentType> getLevelOrderList() {
        List<ContentType> liste = new List<>();
        boolean tmp = true;
        for (int level = 0; tmp; level++) {
            tmp = levelorder(liste, level);

        }
        return liste;
    }

    private List<List<ContentType>> getEachLevelList() {
        List<List<ContentType>> liste = new List<>();
        boolean tmp = true;
        for (int level = 0; tmp; level++) {
            List<ContentType> innereListe = new List<>();
            tmp = levelorder(innereListe, level);
            if (!innereListe.isEmpty()) liste.append(innereListe);
        }

        return liste;
    }

    private List<ContentType> getSortedList() {
        List<ContentType> liste = new List<>();
        inorder(liste);
        return liste;

    }

    private void inorder(List<ContentType> liste) {

        // LTB, Wurzel, Rechter
        if (!links.isEmpty()) links.inorder(liste);

        liste.append(inhalt);
        if (!rechts.isEmpty()) rechts.inorder(liste);
    }

    // Preorder: Wurzel Links Rechts

    private void preorder(List<ContentType> liste) {
        liste.append(this.inhalt);
        if (!links.isEmpty()) links.preorder(liste);
        if (!rechts.isEmpty()) rechts.preorder(liste);

    }

    // Links rechts knoten
    private void postorder(List<ContentType> liste) {
        if (!links.isEmpty()) links.postorder(liste);
        if (!rechts.isEmpty()) rechts.postorder(liste);
        liste.append(this.inhalt);

    }

    /**
     * Falls ein Objekt im binaeren Suchbaum enthalten ist, das gleichgross ist wie
     * pContent, wird dieses entfernt. Falls der Parameter null ist, aendert sich
     * nichts.
     *
     * @param content
     */
    public void remove(ContentType content) {
        if (content == null || isEmpty()) return;
        else {
            if (content.isEqual(this.inhalt)) {

                // Baum ist gesuchte Element - muss entfernt werden
                if (rechts.isEmpty() || links.isEmpty()) {
                    if (rechts.isEmpty() && links.isEmpty()) {// Baum ist Blatt
                        this.inhalt = null;
                        rechts = null;
                        links = null;

                    } else if (rechts.isEmpty()) {
                        // Baum ist Halbblatt (Rechts ist leer)
                        this.inhalt = links.getContent();
                        this.rechts = links.getRightTree();
                        this.links = links.getLeftTree();
                    } else {
                        this.inhalt = rechts.getContent();
                        this.links = rechts.getRightTree();
                        this.rechts = rechts.getRightTree();
                    }

                } // Ende (Halb(Batt
                else {
                    // Baum ist kein Halbblatt - hat sowohl left als auch right
                    BinarySearchTree<ContentType> current = this.links;
                    // Es wird das groesste Element des linken Teilbaums gesucht
                    if (!this.links.getRightTree().isEmpty()) {
                        while (current.getRightTree().getRightTree().getContent() != null) {// Da Referenz sp�ter
                            // benoetigt wird
                            current = current.getRightTree();
                        }
                        this.inhalt = current.getRightTree().inhalt; // Das groesste Element des linken Baumes
                        current.rechts = current.getRightTree().getLeftTree();// Der Rechte Teilbaum von current ist der
                        // linke von dem getauschten Baum ->
                        // daher getR.getR() in der Schleife
                    } else {// Wenn der Linke Teilbaum keinen rechten Baum hat, so ist er selber der groesste
                        // Linker Rechterteilbaum ist leer
                        this.inhalt = links.getContent();
                        this.links = links.links;
                        // Rechts bleibt
                    }
                }
            } else {
                if (content.isGreater(this.inhalt)) rechts.remove(content);
                else links.remove(content);
            }

        }

    }

    public BinarySearchTree() {
    }

    public boolean isEmpty() {
        return inhalt == null;
    }

    public void insert(ContentType content) {
        if (content == null) return;

        if (search(content) != null) return;
        else {
            if (this.isEmpty()) {
                this.inhalt = content;
                rechts = new BinarySearchTree<>();
                links = new BinarySearchTree<>();
            } else {

                if (content.isGreater(inhalt)) rechts.insert(content);
                else links.insert(content);
            }
        }
    }

    public ContentType search(ContentType content) {
        if (content == null) return null;
        if (this.inhalt == null) return null;
        if (content.isEqual(inhalt)) return this.inhalt;
        if (content.isGreater(inhalt)) return rechts.search(content);
        else return this.links.search(content);
    }

    /**
     * Diese Anfrage liefert das Inhaltsobjekt des Suchbaumes. Wenn der Suchbaum
     * leer ist, wird null zur�ckgegeben
     *
     * @return
     */
    public ContentType getContent() {
        return this.inhalt;
    }

    public BinarySearchTree<ContentType> getLeftTree() {
        if (isEmpty()) return null;
        else return this.links;
    }

    /**
     * @return
     */
    public BinarySearchTree<ContentType> getRightTree() {
        if (isEmpty()) return null;
        else return this.rechts;
    }
}
