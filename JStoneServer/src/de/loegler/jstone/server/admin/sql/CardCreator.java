package de.loegler.jstone.server.admin.sql;

import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.DatabaseConnector;
import de.loegler.schule.netzwerk.QueryResult;

/**
 * Klasse wird zum erstellen neuer Karten benötigt. Diese werden in der Datenbank gespeichert.
 */
public class CardCreator {

    public int getNextKartID() {
        String[][] data = DatabaseManager.getInstance().fuehreSQLAus("SELECT MAX(KID) FROM Karte").getData();
        if (data.length == 0 || data[0][0] == null || data[0][0].equalsIgnoreCase("null"))
            return 1;
        else
            return Integer.parseInt(data[0][0]) + 1;
    }

    /**
     * Erstellt eine neue Karte. Reicht aus um einen neuen Zauber zu erstellen.
     */
    public void erstelleKarte(int KID, String name, String typ, int manakosten, String klasse, String erweiterung, String seltenheit, String beschreibung, Integer kampfschrei, String bildName) {
        DatabaseManager.getInstance();
        DatabaseConnector.DEBUG = true;
        String sql = "INSERT INTO Karte VALUES(" + KID + ", '" + name + "', '" + typ + "', " + manakosten + ", '" + klasse + "', '" + erweiterung + "', '" + seltenheit +
                "', '" + beschreibung + "', " + kampfschrei + ")";
        DatabaseManager.getInstance().fuehreSQLAus(sql);
        if (bildName != null && !bildName.isEmpty()) {
            String sqlImage = "INSERT INTO KarteHatBild VALUES(" + KID + ", '" + bildName + "')";
            DatabaseManager.getInstance().fuehreSQLAus(sqlImage);
            System.out.println(sqlImage);
        }
        System.out.println(sql);
    }

    public void erstelleDiener(int KID, String name, String typ, int manakosten, String klasse, String erweiterung, String seltenheit, String beschreibung, Integer kampfschrei, int angriff, int leben, Integer todesroecheln, ListX<String> dienerHatEffekte, String bildName) {
        erstelleKarte(KID, name, typ, manakosten, klasse, erweiterung, seltenheit, beschreibung, kampfschrei, bildName);
        String sql = "INSERT INTO Diener VALUES(" + KID + ", " + angriff + ", " + leben + ", " + todesroecheln + ")";
        System.out.println(sql);
        DatabaseManager.getInstance().fuehreSQLAus(sql);
        dienerHatEffekte.forEach(it -> {
            QueryResult eIDResult = DatabaseManager.getInstance().fuehreSQLAus("SELECT EID From Effekt WHERE EffektName = '" + it + "' ");
            if (eIDResult.getRowCount() != 0) {
                int effektID = Integer.parseInt(eIDResult.getData()[0][0]);
                String tmpSQL = "INSERT INTO DienerHatEffekt VALUES(" + KID + ", " + effektID + ")";
                DatabaseManager.getInstance().fuehreSQLAus(tmpSQL);
            }
        });
    }
}



