package de.loegler.jstone.server.main;

import de.loegler.jstone.core.*;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.QueryResult;

public class DeckManager {

    /**
     * Rückgabe einer kopie des Decks mit der ID deckID
     * Oder null, wenn das Deck nicht existiert.
     */
    public Deck getCopyOfDeck(int deckID) {
        QueryResult klasse = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM DeckHatKlasse WHERE DECKID = " + deckID);
        if (klasse.getRowCount() < 1) {//Deck gibt es nicht
            return null;
        }
        String klassenName = klasse.getData()[0][0];
        QueryResult karten = DatabaseManager.getInstance().fuehreSQLAus("SELECT KID FROM Deck WHERE DECKID = " + deckID);
        ListX<Karte> deckKarten = new ListX<>();

        for (String[] row : karten.getData()) {
            int rowKID = Integer.parseInt(row[0]);
            Karte karte = getCopyOfCard(rowKID);
            deckKarten.append(karte);
        }
        Klasse klasseK = null;
        for (Klasse value : Klasse.values()) {
            if (value.toString().equalsIgnoreCase(klassenName))
                klasseK = value;
        }
        if (klasseK == null)
            klasseK = Klasse.MAGIER;
        return new Deck(deckKarten, klasseK);
        //Deck aus klassenName & deckKarten zusammenbauen & zurückgeben
    }

    /**
     * Rückgabe, wie oft der User eine Karte hat.
     * 2, wenn die Karte eine Standard-Karte ist.
     *
     * @param kid
     * @param userID
     * @return
     */
    public int getCardCount(int kid, int userID) {
        QueryResult tmp = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM Karte WHERE KID =" + kid + " AND Erweiterung = 'Standard'");
        if (tmp != null && tmp.getRowCount() == 1)
            return 2;
        else {
            tmp = DatabaseManager.getInstance().fuehreSQLAus("SELECT Anzahl From UserHatKarte WHERE KID=" + kid + " AND UID=" + userID);
            if (tmp != null && tmp.getRowCount() == 1) {
                return Integer.parseInt(tmp.getData()[0][0]);
            }
        }
        return 0;
    }

    /**
     * Liefert eine Karte mit der ID oder null, falls die Karte nicht existiert.
     *
     * @param kid
     * @return
     */
    public Karte getCopyOfCard(int kid) {
        QueryResult tmp = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM Karte WHERE KID = " + kid);
        if (tmp.getRowCount() == 1) {
            int i = 1;
            String name = tmp.getData()[0][i++];
            String typ = tmp.getData()[0][i++];
            String mana = tmp.getData()[0][i++];
            String klasse = tmp.getData()[0][i++];
            String erweiterung = tmp.getData()[0][i++];
            String seltenheit = tmp.getData()[0][i++];
            String beschreibung = tmp.getData()[0][i++];
            String kampfschrei = tmp.getData()[0][i++];
            int kampfschreiID = -1;
            if (kampfschrei != null && !kampfschrei.isEmpty()) {
                kampfschreiID = Integer.parseInt(kampfschrei);
            }
            Karte toReturn;
            Karte.Seltenheit sHeit = null;
            for (Karte.Seltenheit value : Karte.Seltenheit.values()) {
                if (value.toString().equalsIgnoreCase(seltenheit))
                    sHeit = value;
            }
            if (typ.equalsIgnoreCase(Karte.KartenTyp.DIENER.toString())) {
                QueryResult diener = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM Diener WHERE KID = " + kid);
                i = 1;
                String angriff = diener.getData()[0][i++];
                String leben = diener.getData()[0][i++];
                String todesroecheln = diener.getData()[0][i++];
                int todesroechelnID = -1;
                if (todesroecheln != null && !todesroecheln.isEmpty()) {
                    todesroechelnID = Integer.parseInt(todesroecheln);
                }
                QueryResult dienerHatEffekt = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM DienerHatEffekt WHERE KID = " + kid);
                ListX<Diener.DienerEffekt> effektListe = new ListX<>(); //Kann leer sein!
                for (int z = 0; z < dienerHatEffekt.getRowCount(); z++) {
                    String effektID = (dienerHatEffekt.getData()[z][1]);
                    String toAdd = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM Effekt WHERE EID = " + effektID).getData()[0][1];
                    Diener.DienerEffekt dEffekt = null;
                    for (Diener.DienerEffekt it : Diener.DienerEffekt.values()) {
                        if (it.toString().equalsIgnoreCase(toAdd))
                            dEffekt = it;
                    }
                    effektListe.append(dEffekt);
                }
                Karte.KartenTyp kTyp = null;
                for (Karte.KartenTyp value : Karte.KartenTyp.values()) {
                    if (value.toString().equalsIgnoreCase(typ))
                        kTyp = value;
                }
                toReturn = new Diener(name, beschreibung, Integer.parseInt(mana), kTyp,
                        sHeit, kampfschreiID, todesroechelnID, Integer.parseInt(angriff), Integer.parseInt(leben), effektListe);
            } else {
                toReturn = new Zauber(name, beschreibung, Integer.parseInt(mana), Karte.KartenTyp.ZAUBER, sHeit, kampfschreiID);
            }
            QueryResult qs = DatabaseManager.getInstance().fuehreSQLAus("SELECT Bildname FROM KarteHatBild WHERE KID=" + kid);
            if (qs.getRowCount() != 0) {
                toReturn.setBild(qs.getData()[0][0]);
            }
            return toReturn;
        }//Sonst gibt es die Karte nicht
        return null;
    }


    /**
     * Rückgabe der Deckanzahl eines Benutzers +1 (mit Standarddeck)
     *
     * @return
     */
    public int getDeckCountOfUser(int sid) {
        QueryResult q = DatabaseManager.getInstance().fuehreSQLAus("SELECT Count(DeckID) FROM SpielerHatDeck WHERE SID=" + sid);
        int deckCount = 1;
        if (q.getRowCount() == 1)
            deckCount += Integer.parseInt(q.getData()[0][0]);
        return deckCount;
    }

    /**
     * @param userID
     * @param deckNR - te. Deck des Nutzers, wobei 1 das Standarddeck ist.
     * @return -1, wenn der Nutzer kein deckNR'tes Deck hat.
     */
    public int getDeckID(int userID, int deckNR) {
        if (deckNR <= 0) {
            return -1;
        }
        QueryResult q = DatabaseManager.getInstance().fuehreSQLAus("SELECT DeckID FROM SpielerHatDeck WHERE SID = " + userID + " ORDER BY DeckID");
        if (q.getRowCount() < deckNR) {
            return -1;
        }
        return Integer.parseInt(q.getData()[deckNR - 1][0]);
    }
}
