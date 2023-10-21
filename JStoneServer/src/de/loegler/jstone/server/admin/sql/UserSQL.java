package de.loegler.jstone.server.admin.sql;

import de.loegler.jstone.server.main.DatabaseManager;
import de.loegler.jstone.server.main.RemoteUser;
import de.loegler.schule.algorithmen.DijkstraAlgorithmus;
import de.loegler.schule.datenstrukturen.Edge;
import de.loegler.schule.datenstrukturen.Graph;
import de.loegler.schule.datenstrukturen.Queue;
import de.loegler.schule.datenstrukturen.Vertex;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.DatabaseConnector;
import de.loegler.schule.netzwerk.QueryResult;

import java.util.HashSet;

/**
 * Die Klasse UserSQL bietet einige Methoden an, welche bestimmte SQL-Befehle ausführen.
 */
public class UserSQL {
    /**
     * Abfrage der aktuellen Goldmünzen des Spielers
     * @param userID UID des Users
     * @return Aktuelle Goldanzahl des Users
     */
    public int getGold(int userID) {
        QueryResult result = DatabaseManager.getInstance().fuehreSQLAus("SELECT Gold FROM user WHERE UID=" + userID);
        if (result.getRowCount() == 0)
            return -1;
        else return Integer.parseInt(result.getData()[0][0]);
    }

    public static void setGold(int userid, int newGold) {
        DatabaseManager.getInstance().fuehreSQLAus("UPDATE USER SET Gold = " + newGold + " WHERE UID = " + userid);
    }

    /**
     * Setzt den Staub des Users
     * @param userid UID des Users
     * @param newStaub Neue Staubanzahl
     */
    public static void setStaub(int userid, int newStaub) {
        DatabaseManager.getInstance().fuehreSQLAus("UPDATE USER SET Staub = " + newStaub + " WHERE UID = " + userid);
    }

    /**
     * Prüft, ob ein Name verwendet werden kann, oder ob er ggf. SQL-Injections enthält.
     * Notwendig, da DatabaseConnector keine {@link java.sql.PreparedStatement} nutzt.
     * @param username Der zu prüfende Name
     * @return true, wenn der Name gültig ist
     */
    public static boolean checkUsernameValid(String username){
        return !username.contains(" ") && !username.contains(";") && !username.contains("'");
    }




    /**
     * Fügt dem Konto des Benutzers Punkte hinzu
     * @param userID UID des Users
     * @param punkteToAdd Punkte, welche hinzugefügt werden sollen
     */
    public static void addPunkte(int userID, int punkteToAdd) {
        DatabaseManager.getInstance().fuehreSQLAus("UPDATE user SET Punkte = Punkte + " + punkteToAdd + " WHERE UID=" + userID);
    }


    /**
     * Abfrage der aktuellen Punktzahl eines Users
     * @param userID UID des Users
     * @return Punktzahl des Users
     */
    public static int getPunkte(int userID){
        return Integer.parseInt(DatabaseManager.getInstance().fuehreSQLAus("SELECT Punkte FROM User WHERE UID = " + userID).getData()[0][0]);
    }

    /**
     * Fügt dem Konto des Benutzers Gold hinzu
     * @param userID Die UID des Nutzers
     * @param goldToAdd Anzahl des Goldes, welches hinzugefügt werden soll
     */
    public static void addGold(int userID, int goldToAdd) {
        DatabaseManager.getInstance().fuehreSQLAus("UPDATE user SET Gold = Gold + " + goldToAdd + " WHERE UID=" + userID);
    }


    /**
     * Erstellt einen neuen User in der Datenbank User und UserLogin
     *
     * @return Die UserID des neuen Users
     */
    public static int createUser(String user, String hashedPW) {
        DatabaseConnector.DEBUG = true;
        DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO USERLOGIN VALUES(" + 0 + ", '" + user + "', '" + hashedPW + "'" + ")");
        String uID = DatabaseManager.getInstance().fuehreSQLAus("SELECT UID FROM USERLOGIN WHERE USERNAME = '" + user + "'").getData()[0][0];

        DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO USER VALUES (" + uID + ", " + 0 + ", " + 0 + ", " + 0 + ")");
        return Integer.parseInt(uID);
    }

    /**
     *
     * @param thisUser Der aktuelle Nutzer
     * @param requestedUser Der Nutzer, welche die Anfrage versendet hat
     */
    public static void acceptRequest(RemoteUser thisUser, String requestedUser){

        int count = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM FreundschaftAnfrage WHERE EUID = " + getUserID(requestedUser)+
                " AND ZUID = " + thisUser.getUserID()).getRowCount();
        if(count!=0){
            //Es gibt eine Anfrage für ihn
            DatabaseManager.getInstance().fuehreSQLAus("DELETE FROM FreundschaftAnfrage WHERE EUID = " + getUserID(requestedUser) +
                    " AND ZUID = " + thisUser.getUserID());
            DatabaseManager.getInstance().
                    fuehreSQLAus("INSERT INTO Freundschaft VALUES(" + getUserID(requestedUser)+","+thisUser.getUserID()+")");
        }
    }

    /**
     * Lehnt die Freundschaftsanfrage eines Nutzers ab
     * @param thisUser Der Nutzer, welche seine Freundschaftsanfrage ablehnen möchte
     * @param requestedUser Name der anderen Person, welche die Freundschaftsanfrage gestellt hat
     */
    public static void denyRequest(RemoteUser thisUser, String requestedUser){
        int count = DatabaseManager.getInstance().fuehreSQLAus("SELECT * FROM FreundschaftAnfrage WHERE EUID = " + getUserID(requestedUser)+
                " AND ZUID = " + thisUser.getUserID()).getRowCount();
        if(count!=0) {
            //Es gibt eine Anfrage für ihn
            DatabaseManager.getInstance().fuehreSQLAus("DELETE FROM FreundschaftAnfrage WHERE EUID = " + getUserID(requestedUser) +
                    " AND ZUID = " + thisUser.getUserID());
        }
    }


    /**
     * Sendet eine Freundschaftsanfrage an einen User
     * @param requester Der User, welche die Freundschaftsanfrage versendet
     * @param userRequested Username, welcher requester hinzufügen möchte
     */
    public static void requestFriendship(RemoteUser requester, String userRequested) {
        String otherID = getUserID(userRequested);
        if(otherID!=null){
            //TODO Prüfen, ob bereits Anfrage vorhanden & Annehmen, wenn Anfrage von Gegenüber vorhanden
            DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO FreundschaftAnfrage VALUES(" + requester.getUserID() + "," + otherID+")");
        }
    }

    private static String getUserID(String username){
        String toRet = null;
        if(checkUsernameValid(username)){
           QueryResult q = DatabaseManager.getInstance().fuehreSQLAus("SELECT UID FROM UserLogin WHERE Username = '"+ username + "'");
           if(q.getRowCount()!=0){
               toRet = q.getData()[0][0];
           }
        }
        return toRet;
    }


    private static ListX<String> getFriendList(String userID,boolean useID){
        ListX<String> toReturn = new ListX<>();
        QueryResult q = DatabaseManager.getInstance().
                fuehreSQLAus("SELECT * FROM Freundschaft WHERE EUID = " + userID + " OR ZUID = " + userID);
        if(q.getRowCount()!=0){
            for(String[] row : q.getData()){
                String otherID;
                if(row[0].equalsIgnoreCase(userID)){
                    otherID=row[1];
                }else{
                    otherID=row[0];
                }
                String otherName= DatabaseManager.getInstance().fuehreSQLAus("SELECT Username FROM UserLogin WHERE UID = " + otherID).getData()[0][0];

                if(useID){
                    toReturn.append(otherID);
                }else{
                    toReturn.append(otherName);
                }

            }
        }
        return toReturn;
    }

    /**
     * Anfrage einer Liste mit allen Freunden eines Nutzers
     * @param user Der Nutzer, welche seine Freundesliste anfragt
     * @return Eine Liste mit allen Freunden des Nutzers
     */
    public static ListX<String> getFriendList(RemoteUser user){
        String userID=user.getUserID()+"";
        return getFriendList(user.getUserID()+"",false);
    }

    /**
     * Rückgabe der Nutzernamen, welche dem User eine (offene) Freundschaftsanfrage versendet haben
     * @param user Der User, welche seine Freundschaftsanfragen abfragen möchte
     * @return Eine Liste an Nutzernamen
     */
    public static ListX<String> getFriendRequests(RemoteUser user){
        ListX<String> toReturn = new ListX<>();
        QueryResult q = DatabaseManager.getInstance().fuehreSQLAus("SELECT EUID FROM FreundschaftAnfrage WHERE ZUID = " + user.getUserID());
        if(q.getRowCount()!=0){
            for(String[] row : q.getData()){
                String userName = DatabaseManager.getInstance().
                        fuehreSQLAus("SELECT Username FROM UserLogin WHERE UID = " + user.getUserID()).getData()[0][0];
                toReturn.append(userName);
            }
        }
        return toReturn;
    }


    /**
     * Rückgabe des Grades der Freundschaft zwischen zwei Usern
     * @param user Der User, welche die Anfrage versendet hat
     * @param username Name des Benutzers, dessen bekanntschaft mit user herausgefunden werden soll
     * @return -1, wenn es keine Verbindung zum User gab, -2, wenn der Graph zu groß geworden ist oder die Distanz
     */
    public static double eckenBefreundet(RemoteUser user, String username){
        String otherID = getUserID(username);
        if(otherID!=null) {
            //Schneller Zugriff - List ggf. zu langsam
            HashSet<String> userIDs = new HashSet<>();
            Graph graph = new Graph();
            Vertex start = new Vertex(user.getUserID() + "");
            Vertex end = new Vertex(otherID);
            int addLimit = 100;
            int addCount =0;

            graph.addVertex(start);
            Queue<Vertex> queue = new Queue<>();
            for (queue.enqueue(start); !queue.isEmpty(); queue.dequeue()) {
                String userID = queue.front().getID();
                if (!userIDs.contains(userID)) {
                    ListX<String> freunde = getFriendList(userID, true);
                    for (freunde.toFirst(); freunde.hasAccess(); freunde.next()) {
                        if (!userIDs.contains(freunde.getContent())) {
                            Vertex other = new Vertex(freunde.getContent());
                            queue.enqueue(other);
                            Edge edge = new Edge(queue.front(),other,1);
                            graph.addVertex(other);
                            addCount++;
                            graph.addEdge(edge);
                            if(addCount>=addLimit){
                                return -2; //Graph wurde zu groß
                            }
                        }
                    }
                    userIDs.add(userID);
                }
            }
            //Graph wurde erstellt
            DijkstraAlgorithmus algorithmus = new DijkstraAlgorithmus(graph);
            DijkstraAlgorithmus.DijkstraReturnWrapper result = algorithmus.dijkstra(start,end);
            if(result==null)
                return -1;
            else
                return result.getCosts();
        }
        return -1;
    }
}
