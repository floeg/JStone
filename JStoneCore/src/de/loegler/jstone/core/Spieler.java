package de.loegler.jstone.core;

public class Spieler {
    private int userID;
    private int kartenstabelLeerSchaden = 0;
    private int leben = 30;

    public int getLeben() {
        return leben;
    }
    public void setLeben(int leben) {
        this.leben = leben;
    }
}
