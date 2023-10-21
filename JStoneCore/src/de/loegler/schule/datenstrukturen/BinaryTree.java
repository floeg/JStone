package de.loegler.schule.datenstrukturen;

public class BinaryTree<ContentType> {

    private BinaryTree<ContentType> leftTree, rightTree;
    private ContentType content;

    public static void erstelleBaum(BinaryTree<String> tree, int tiefe) {
        if (tiefe > 3) {
            tree.setLeftTree(new BinaryTree<>());
            tree.setRightTree(new BinaryTree<>());
        } else {
            tree.setLeftTree(new BinaryTree<>(tiefe + "Links"));
            tree.setRightTree(new BinaryTree<>(tiefe + "Rechts"));
            erstelleBaum(tree.getLeftTree(), tiefe + 1);
            erstelleBaum(tree.getRightTree(), tiefe + 1);
        }
    }

    public static void schreibeEbene(BinaryTree<String> tree) {
        if (tree == null || tree.getContent() == null) return;
        System.out.println(tree.getContent());
        schreibeEbene(tree.getLeftTree());
        schreibeEbene(tree.getRightTree());
    }

    public BinaryTree() {
    }

    public BinaryTree(ContentType pContent) {
        if (pContent == null) return;
        this.content = pContent;
        this.rightTree = new BinaryTree<>();
        this.leftTree = new BinaryTree<>();

    }

    public BinaryTree(ContentType pContent, BinaryTree<ContentType> left, BinaryTree<ContentType> right) {

        if (pContent == null) {
            return;
        } else {
            this.content = pContent;

            if (left != null) this.leftTree = left;
            else this.leftTree = new BinaryTree<>();
            if (right != null) this.rightTree = right;
            else this.rightTree = new BinaryTree<>();

        }
    }

    public boolean isEmpty() {
        return this.content == null;
    }

    public void setContent(ContentType pContent) {
        if (pContent == null) return;
        if (this.isEmpty()) {
            this.rightTree = new BinaryTree<>();
            this.leftTree = new BinaryTree<>();
        }
        this.content = pContent;

    }

    public ContentType getContent() {
        return this.content;
    }

    public void setLeftTree(BinaryTree<ContentType> tree) {
        if (!this.isEmpty() && tree != null) this.leftTree = tree;

    }

    public void setRightTree(BinaryTree<ContentType> tree) {
        if (!this.isEmpty() && tree != null) this.rightTree = tree;

    }

    public BinaryTree<ContentType> getRightTree() {
        return this.rightTree;
    }

    public BinaryTree<ContentType> getLeftTree() {
        return this.leftTree;
    }

}
