package de.loegler.jstone.server;

import de.loegler.jstone.core.Karte;
import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.jstone.server.main.DeckManager;
import de.loegler.jstone.server.main.Settings;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.QueryResult;

import java.util.Random;
/**
 * Klasse zum öffnen von Kartenpackungen.
 * Erledigt alles Serverseitig, informiert jedoch nicht den Client.
 */
public class PackOpeningManager {
    protected int normal = 740, rare = 200, epic = 50, leg = 10;
    private Random rand;
    private DeckManager deckManager = new DeckManager();

    public PackOpeningManager() {
        rand = new Random();
    }

    /**
     * Öffnet eine Kartenpackung und fügt die Karten zu UserHatKarte hinzu.
     * Rückgabe wird vom Server verwendet um den User über seine neuen Karten zu informieren.
     *
     * @param userID   Der User, welcher eine Packung öffnen möchte.
     * @param packName Name der Erweiterung, von der der User die Packung öffnen möchte.
     * @return Die Karten, welche in der Kartenpackung waren. Oder null, wenn die Packung nicht geöffnet werden konnte.
     */
    public ListX<Karte> openPack(int userID, String packName) {
        ListX<Karte> toReturn = null;
        int gold = Integer.parseInt(DatabaseManager.getInstance().fuehreSQLAus("SELECT Gold FROM User WHERE UID = " + userID).getData()[0][0]);
        if (gold >= Settings.getInstance().PACK_PRICE) {
            toReturn = new ListX<>();
            DatabaseManager.getInstance().fuehreSQLAus("UPDATE User SET Gold = " + (gold - Settings.getInstance().PACK_PRICE) + " WHERE UID = " + userID);
            int[] cardsIndies = this.getCards(5, packName);
            for (int index : cardsIndies) {
                Karte k = deckManager.getCopyOfCard(index);
                toReturn.append(k);
                QueryResult oldCardResult = DatabaseManager.getInstance().
                        fuehreSQLAus("SELECT Anzahl FROM UserHatKarte WHERE UID = " + userID + " AND KID = " + index);
                if (oldCardResult.getRowCount() == 1) {
                    int oldCardCount;
                    oldCardCount = Integer.parseInt(oldCardResult.getData()[0][0]);
                    DatabaseManager.getInstance().
                            fuehreSQLAus("UPDATE UserHatKarte SET Anzahl = " + (oldCardCount + 1) + " WHERE UID = " + userID + " AND KID = " + index);
                } else {
                    if (index != -1)
                        DatabaseManager.getInstance().
                                fuehreSQLAus("INSERT INTO UserHatKarte VALUES (" + userID + ", " + index + ", 1)");
                }
            }
        }
        return toReturn;
    }


    /**
     * Hinweis: Es muss vorher geprüft werden, dass es mindestens eine Karte von der Erweiterung gibt!
     * @param erweiterung
     */
    public int getCardFrom(String erweiterung) {
        int kid = -1;
        int r = rand.nextInt(1001);
        Karte.Seltenheit seltenheit = Karte.Seltenheit.GEWOEHNLICH;
        //Bei epic=5, leg=1
        if (r < leg) { //0..1
            seltenheit = Karte.Seltenheit.LEGENDAER;
        } else if (r < epic + leg) { //2..6
            seltenheit = Karte.Seltenheit.EPISCH;
        } else if (r < rare + epic + leg) { //7..27
            seltenheit = Karte.Seltenheit.SELTEN;
        }
        System.out.println(r);
        String sql = "SELECT KID FROM Karte WHERE Erweiterung = '" + erweiterung + "' AND Seltenheit = '" + seltenheit.toString() + "'";
        QueryResult queryResult = DatabaseManager.getInstance().fuehreSQLAus(sql);
        if (queryResult == null || queryResult.getRowCount() == 0) {
            System.out.println("Es gibt keine Karte mit dieser Seltenheit!");
            //Client erhält eine Karte weniger.
        } else {
            int arrayIndex = rand.nextInt(queryResult.getRowCount());
            kid = Integer.parseInt(queryResult.getData()[arrayIndex][0]);
        }
        return kid;
    }


    public int[] getCards(int amount, String erweiterung) {
        int[] cIndex = new int[amount];
        for (int i = 0; i != amount; i++) {
            cIndex[i] = this.getCardFrom(erweiterung);
        }
        return cIndex;
    }

    /**
     * @return Rückgabe einer Liste aller Kartenpackungen inklusive Standard.
     */
    public ListX<String> getPackTypes() {
        ListX<String> toRet = new ListX<>();
        QueryResult result = DatabaseManager.getInstance().fuehreSQLAus("SELECT Erweiterung FROM Karte GROUP BY Erweiterung");
        for (String[] row : result.getData()) {
            toRet.append(row[0]);
        }
        return toRet;
    }
}
