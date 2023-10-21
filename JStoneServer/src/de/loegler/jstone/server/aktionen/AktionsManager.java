package de.loegler.jstone.server.aktionen;

import de.loegler.jstone.core.Karte;
import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.schule.netzwerk.QueryResult;

import java.util.Random;

/**
 * Die Klasse AktionsManager wird genutzt um, basierend auf einer AktionsID eines Dieners, eine bestimmte Aktion auf dem Schlachtfeld auszuführen.
 * Zur Ausführung wird eine {@link AktionenAPI} benötigt.
 */
public class AktionsManager {
    private AktionenAPI aktionenAPI;
    private Random rand = new Random();

    /**
     * Erstellt eine neuen Aktionsmanager zur ausführung von Aktionen
     * @param aktionenAPI Implementierung von AktionenAPI
     */
    public AktionsManager(AktionenAPI aktionenAPI) {
        this.aktionenAPI = aktionenAPI;
    }

    private Aktion getAktion(int aktionsID) {
        if (aktionsID == -1) {
            return null;
        }
        QueryResult typ = DatabaseManager.getInstance().fuehreSQLAus("SELECT AktionsTyp FROM AKTION WHERE AID = " + aktionsID);
        if (typ.getRowCount() != 1)
            return null;
        String aTyp = typ.getData()[0][0];
        Aktion aktion = new Aktion(aTyp);
        QueryResult parameter = DatabaseManager.getInstance().fuehreSQLAus("SELECT Parameter FROM AktionParameter WHERE AID = " + aktionsID +
                " ORDER BY ParameterNR");
        if (parameter.getRowCount() != 0) {
            if (aTyp.equalsIgnoreCase(Aktion.Namenssammlung.DRAW_CARD.toString())) {
                aktion.putArgument(Aktion.Namenssammlung.DRAW_CARD_AMOUNT.toString(), parameter.getData()[0][0]);
            } else if (aTyp.equalsIgnoreCase(Aktion.Namenssammlung.SUMMON_MINION.toString())) {
                aktion.putArgument(Aktion.Namenssammlung.SUMMON_MINION.toString(), parameter.getData()[0][0]);
                aktion.putArgument(Aktion.Namenssammlung.SUMMON_AMOUNT.toString(), parameter.getData()[1][0]);
            } else if (aTyp.equalsIgnoreCase(Aktion.Namenssammlung.HEAL_HERO.toString())) {
                aktion.putArgument(Aktion.Namenssammlung.HEAL_AMOUNT.toString(), parameter.getData()[0][0]);
            } else if (aTyp.equalsIgnoreCase(Aktion.Namenssammlung.HERO_DAMAGE.toString())) {
                aktion.putArgument(Aktion.Namenssammlung.TARGET_SIDE.toString(), parameter.getData()[0][0]);
                aktion.putArgument(Aktion.Namenssammlung.DAMAGE_AMOUNT.toString(), parameter.getData()[1][0]);
            } else if (aTyp.equalsIgnoreCase(Aktion.Namenssammlung.SUMMON_RANDOM.toString())) {
                aktion.putArgument(Aktion.Namenssammlung.SUMMON_AMOUNT.toString(), parameter.getData()[0][0]);
                aktion.putArgument(Aktion.Namenssammlung.SQL_STATEMENT.toString(), parameter.getData()[1][0]);
            }else if(aTyp.equalsIgnoreCase(Aktion.Namenssammlung.LEG_ISOTOPE.toString())){
                //Keine Argumente vorhanden
            }
            else if(aTyp.equalsIgnoreCase(Aktion.Namenssammlung.LEG_AB_JRR.toString())){
                //Keine Argumente
            }

             /*
             In MYSQL zu definieren als: (aus Zeitgründen erfolgt dies manuell und nicht über eine GUI)
        SUMMON_RANDOM SUMMON_AMOUNT SQL_STATEMENT
        HERO_DAMAGE TARGET_SIDE DAMAGE_AMOUNT
        SUMMON_MINION SUMMON_MINION SUMMON_AMOUNT
        DRAW_CARD DRAW_CARD_AMOUNT
         */

        }
        return aktion;
    }

    /**
     * Führt eine bestimmte Aktion aus
     * @param source Die Karte, welche den Effekt ausgelöst hat
     * @param aktionsID ID der Aktion, welche ausgeführt werden soll
     * @param ownerID Der Spieler, dem die Karte gehört
     */
    public void fuehreAktionAus(Karte source, int aktionsID, int ownerID) {
        Aktion aktion = getAktion(aktionsID);
        if (aktion == null) {
            boolean debug = false;
            if (debug) {
                System.out.println("Debug: Jede Karte hat Summon-Effekt!");
                aktion = new Aktion(Aktion.Namenssammlung.SUMMON_MINION.toString());
                aktion.putArgument(Aktion.Namenssammlung.SUMMON_AMOUNT.toString(), 1 + "");
                aktion.putArgument(Aktion.Namenssammlung.SUMMON_MINION.toString(), 1 + "");
                aktion = new Aktion(Aktion.Namenssammlung.HEAL_HERO.toString());
                aktion.putArgument(Aktion.Namenssammlung.HEAL_AMOUNT.toString(), 3 + "");
                aktion = getAktion(9);
            } else {
                return;
            }
        }
        String aktionsTyp = aktion.getAktionsTyp();
        if (aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.DRAW_CARD.toString())) {

            String amount = aktion.getArgument(Aktion.Namenssammlung.DRAW_CARD_AMOUNT.toString());
            int a = 1;
            if (amount != null)
                a = Integer.parseInt(amount);

            for (int i = 0; i != a; i++)
                aktionenAPI.drawCard(ownerID);

        } else if (aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.SUMMON_MINION.toString())) {
            String amount = aktion.getArgument(Aktion.Namenssammlung.SUMMON_AMOUNT.toString());
            String kID = aktion.getArgument(Aktion.Namenssammlung.SUMMON_MINION.toString());
            int iAmount = Integer.parseInt(amount);
            int iKID = Integer.parseInt(kID);
            aktionenAPI.summonMinion(iKID, iAmount, ownerID);
        } else if (aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.HEAL_HERO.toString())) {
            int amount = Integer.parseInt(aktion.getArgument(Aktion.Namenssammlung.HEAL_AMOUNT.toString()));
            aktionenAPI.healHero(ownerID, amount);
        } else if (aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.HERO_DAMAGE.toString())) {
            int amount = Integer.parseInt(aktion.getArgument(Aktion.Namenssammlung.DAMAGE_AMOUNT.toString()));
            int dbSide = Integer.parseInt(aktion.getArgument(Aktion.Namenssammlung.TARGET_SIDE.toString()));
            int targetID = 0;
            int side;
            if (dbSide == 0)
                side = ownerID;
            else
                side = 1 - ownerID;

            aktionenAPI.dealDamage(side, targetID, amount);
        } else if (aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.SUMMON_RANDOM.toString())) {
            int amount = Integer.parseInt(aktion.getArgument(Aktion.Namenssammlung.SUMMON_AMOUNT.toString()));
            String whereCond = aktion.getArgument(Aktion.Namenssammlung.SQL_STATEMENT.toString());
            for (int i = 0; i != amount; i++) {
                String sql = "SELECT Karte.KID FROM Karte INNER JOIN Diener ON Karte.KID = Diener.KID";
                if (whereCond != null && !whereCond.isEmpty())
                    sql = sql + " WHERE " + whereCond;
                QueryResult result = DatabaseManager.getInstance().fuehreSQLAus(sql);
                if (result.getRowCount() != 0) {

                    int r = rand.nextInt(result.getRowCount());
                    int kid = Integer.parseInt(result.getData()[r][0]);
                    aktionenAPI.summonMinion(kid, 1, ownerID);
                } else {
                    System.out.println("Es wurde kein Diener gefunden!");
                }
            }
        }
        else if(aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.LEG_ISOTOPE.toString())){
            int cardsLeft = aktionenAPI.getDeckCardsLeft(ownerID);
            if(cardsLeft==0)
                return;
            //zu beginn 30
            System.out.println("Karten im Deck: "+cardsLeft);
            for(int i=0;i!=cardsLeft;i++){
                aktionenAPI.drawCard(ownerID);
            }
            System.out.println("Nun im Deck: " + aktionenAPI.getDeckCardsLeft(ownerID));
        }
        else if(aktionsTyp.equalsIgnoreCase(Aktion.Namenssammlung.LEG_AB_JRR.toString())){
            int cardsLeft = aktionenAPI.getDeckCardsLeft(ownerID);
            if(cardsLeft<1){

                aktionenAPI.summonMinion(57,6,ownerID); //TODO KID nicht im Quellcode
            }
        }
    }
}
