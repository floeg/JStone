package de.loegler.jstone.core;

import de.loegler.schule.algorithmen.Verschluesslung;

import java.math.BigInteger;

/**
 * Wird verwendet um alle notwendigen Daten für die RSA-Verschlüsslung zu speichern.
 */
public class RSAWrapper {
    public Verschluesslung thisRSA = new Verschluesslung();
    private BigInteger otherN;
    private BigInteger otherE;

    /**
     * Erstellt einen neuen RSA-Wrapper. Erstellt eine neue Verschlüsslung.
     * Der öffentliche Schlüssel der Gegenseite muss noch gesetzt werden.
     */
    public RSAWrapper() {
    }

    /**
     * Verschlüsselt einen Text mit dem öffentlichen Schlüssel des Partners
     *
     * @param text Der Text, welcher verschlüsselt werden soll
     * @return Der verschlüsselte Text ({@link BigInteger#toString()})
     */
    public String verschluessle(String text) {
        return thisRSA.nachrichtVerschluesseln(text, otherE, otherN).toString();
    }


    public String entschluessle(String nachricht) {
        BigInteger bigNachricht = new BigInteger(nachricht);
        return thisRSA.nachrichtEntschluesseln(bigNachricht);
    }

    public BigInteger getOtherN() {
        return otherN;
    }

    public void setOtherN(BigInteger otherN) {
        this.otherN = otherN;
    }

    public BigInteger getOtherE() {
        return otherE;
    }

    public void setOtherE(BigInteger otherE) {
        this.otherE = otherE;
    }
}
