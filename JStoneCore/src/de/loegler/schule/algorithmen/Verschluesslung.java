package de.loegler.schule.algorithmen;

import de.loegler.schule.datenstrukturen.Stack;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Implementation aus dem Vortrag RSA-Verfahren.
 *
 */
public class Verschluesslung {
    BigInteger K0 = new BigInteger("0");
    BigInteger K1 = new BigInteger("1");
    String zahlenNachricht = "";
    BigInteger verNachricht = BigInteger.ZERO;
    BigInteger entNachricht = BigInteger.ZERO;
    String entNachrichtKlar = "";
    private int nachrichtenCap;

    /**
     * Kodierungstabelle
     */
    char[] tabelle;
    private BigInteger q, p, phiN, e, d;
    /**
     * Öffentlicher Schlüssel von B
     */
    private BigInteger n;

    public Verschluesslung() {
        char[] sonderzeichen = new char[]{' ', '!', '.', '_', '(', ')'};
        tabelle = new char[26 * 2 + sonderzeichen.length + 10];
        char current = 'a';
        char z = 'z';
        int i = 0;
        for (; current != z + 1; current++, i++) {
            tabelle[i] = current;
        }
        current = 'A';
        z = 'Z';
        for (; current != z + 1; current++, i++) {
            tabelle[i] = current;
        }
        for (char c : sonderzeichen) {
            tabelle[i++] = c;
        }
        char last = '9' + 1;
        for (current = '0'; current != last; current++, i++) {
            tabelle[i] = current;
        }
        this.schluesselErzeugen();
    }


    /**
     * Rückgabe einer Primzahl mit min. 500 Bits
     */
    private BigInteger erzeugePrimzahl() {
        BigInteger result;
        do {
            do {
                result = new BigInteger(512, new SecureRandom());
            }
            while (result.bitLength() < 500);
        }
        while (!result.isProbablePrime(3));
        return result;
    }

    /**
     * Erzeugt den öffentlichen Schlüssel n und berechnet Phi(n),
     * Bestimmt e,
     * Bestimmt d
     */
    private void schluesselErzeugen() {
        q = erzeugePrimzahl();
        p = erzeugePrimzahl();
        n = q.multiply(p);
        phiN = (q.subtract(new BigInteger("1"))).multiply(p.subtract(BigInteger.ONE));
        do {
            e = new BigInteger(7, new SecureRandom());
            e = e.add(new BigInteger("2"));
        }
        while (e.compareTo(phiN) >= 0 || !istTeilerfremd(phiN, e));
        d = EEA(phiN, e);
        /*
        Quelle: https://crypto.stackexchange.com/questions/10805/how-does-one-deal-with-a-negative-d-in-rsa
         */
        //Falls d negativ ist, ansonsten hat es keine Auswirkung auf d
        d = d.mod(phiN);
        nachrichtenCap = n.bitLength();
    }

    /**
     * Rückgabe, ob a und b teilerfremd sind
     */
    public boolean istTeilerfremd(BigInteger a, BigInteger b) {
        BigInteger rest;
        rest = a.mod(b);
        if (rest.abs().equals(K0.abs())) {
            return b.abs().equals(K1.abs());
        }
        return istTeilerfremd(b, rest);
    }

    /**
     * @param a phiN
     * @param b e
     */
    public BigInteger EEA(BigInteger a, BigInteger b) {
        BigInteger rest, x1, x2, y;
        Stack<BigInteger> k = new Stack<>();
        do {
            // x*b + c (Rest) --> x wird auf Stack
            k.push(a.divide(b));
            rest = a.mod(b);
            // Verschiebe nach Links
            a = b;
            b = rest;
        }
        while (!rest.abs().equals(K0.abs())); // Rest ist 0 --> GGT gefunden
        k.pop();
        // Erweiterter -> nach 'oben'
        // GGT =
        x2 = BigInteger.ZERO;
        x1 = BigInteger.ONE;
        y = x1;
        while (!k.isEmpty()) {
            y = x2.subtract((k.top().multiply(x1)));
            x2 = x1;
            x1 = y;
            k.pop();
        }
        return y;
    }


    /**
     * Verschlüsselt die übergebene Nachricht.
     * Die Nachricht darf nur aus kleinen Buchstaben und Leerzeichen bestehen
     */
    public BigInteger nachrichtVerschluesseln(String nachricht) {
        return nachrichtVerschluesseln(nachricht, this.e, this.n);
    }

    public BigInteger nachrichtVerschluesseln(String nachricht, BigInteger e, BigInteger n) {
        this.kodiereNachricht(nachricht);
        return (this.verNachricht = new BigInteger(this.zahlenNachricht).modPow(e, n));
    }


    /**
     * Nutzt {@link #d} und {@link #n}, um {@code verNachricht} zu entschlüsseln
     *
     * @param verNachricht Die Nachirchricht, welche entschlüsselt werden soll
     * @return
     * @see #nachrichtEntschluesseln(BigInteger, BigInteger, BigInteger)
     */
    public String nachrichtEntschluesseln(BigInteger verNachricht) {
        return nachrichtEntschluesseln(verNachricht, d, n);
    }

    /**
     * @param verNachricht Nachricht welche mit diesem {@link #n} verschluesselt wurde.
     * @param d            Eigenes {@link #d}
     * @param n            Eigenes {@link #n}
     * @return
     */
    public String nachrichtEntschluesseln(BigInteger verNachricht, BigInteger d, BigInteger n) {
        if (verNachricht != null && !verNachricht.equals(BigInteger.ZERO)) {
            entNachricht = (verNachricht.modPow(d, n));
            return dekodiereNachricht(entNachricht);
        }
        return null;
    }

    /**
     * Wandelt eine Nachricht in einen BigInteger mithilfe der Tabelle um.
     *
     * @param nachricht Die Nachricht, welche kodiert werden soll.
     */
    private void kodiereNachricht(String nachricht) {
        zahlenNachricht = "";
        for (int i = 0; i != nachricht.length(); i++) {
            char currentChar = nachricht.charAt(i);
            int index = -1;
            for (int z = 0; z != this.tabelle.length; z++) {
                if (currentChar == this.tabelle[z]) {
                    index = z;
                    break;
                }
            }
            if (index < 0)
                throw new IllegalArgumentException("Bitte halte dich an den Zeichensatz!");
            zahlenNachricht += (index + 10);
        }
        if (zahlenNachricht.length() >= this.nachrichtenCap) {
            throw new IllegalArgumentException("Deine Nachricht ist zu groß! (" + nachricht.length() + " bei maximal " + ((this.nachrichtenCap - 1) / 2) + " )");
        }
    }

    /**
     * Wandelt eine kodierte Nachricht in einen String um
     *
     * @param zahlenNachricht Die Nachricht
     */
    private String dekodiereNachricht(BigInteger zahlenNachricht) {
        this.entNachrichtKlar = "";
        char[] codeMessage = zahlenNachricht.toString().toCharArray();
        for (int i = 0; i != codeMessage.length; ) {
            int index = Integer.parseInt("" + codeMessage[i++] + codeMessage[i++]);
            entNachrichtKlar += tabelle[index - 10];
        }
        return entNachrichtKlar;
    }

    /*
     * Getter
     */

    /**
     * Rückgabe der entschlüsselten Nachricht
     */
    public String getEntNachrichtKlar() {
        return entNachrichtKlar;
    }

    /**
     * Rückgabe des öffentlichen Schlüssel e des Empfängers
     */
    public BigInteger getE() {
        return e;
    }

    /**
     * Rückgabe des öffentlichen Schlüssels n
     */
    public BigInteger getN() {
        return n;
    }

}
