package de.loegler.jstone.server.main;

import de.loegler.schule.datenstrukturen.BinarySearchTree;
import de.loegler.schule.datenstrukturen.ComparableContent;
import de.loegler.schule.datenstrukturenExtensions.ListX;

import java.util.HashMap;

public class FightQueue {
    private HashMap<Integer, RemoteUser> idUserMap;
    private int userInQueue = 0;
    private boolean matchmakingStarted = false;
    private JStoneServer server;
    private BinarySearchTree<ComparableUser> comparableUser;

    /**
     * Erstellt eine neue FightQueue
     * @param server Der Server zur Einteilung von Spielern
     */
    public FightQueue(JStoneServer server) {
        idUserMap = new HashMap<>();
        this.server = server;
        comparableUser = new BinarySearchTree<>();
    }

    /**
     * Fügt einen neuen Nutzer zur FightQueue hinzu.
     * Thread-sicher
     * @param user Der User, welcher die Queue betreten möchte.
     */
    public void addUser(RemoteUser user) {
        synchronized (this) {
            idUserMap.put(user.getUserID(), user);
            ComparableUser neuerUser = new ComparableUser(user.getUserID(), user.getSpielstaerke());
            user.updateSpielstarke();
            int userSpielstaerke = user.getSpielstaerke();
            this.comparableUser.insert(neuerUser);
            userInQueue++;
            if (userInQueue > 1 && !matchmakingStarted) {
                starteMatchmaking();
            }
        }
    }


    private void starteMatchmaking() {
        matchmakingStarted = true;
        MatchmakingThread matchmakingThread = new MatchmakingThread();
        matchmakingThread.start();
    }

    private void teileSpielerEin() {
        System.out.println("Teile Spieler ein.");
        ListX<ComparableUser> resultList = new ListX<>();
        resultList = getInOrderList(resultList, this.comparableUser);
        resultList.forEach(System.out::println);
        boolean running = true;
        for (resultList.toFirst(); running; resultList.next()) {
            ComparableUser first = resultList.getContent();
            ComparableUser second = resultList.getNext();
            this.comparableUser.remove(first);
            this.comparableUser.remove(second);
            if (first != null && second != null) {
                server.matchPlayers(idUserMap.get(first.uID), idUserMap.get(second.uID));
            } else {
                running = false;
                userInQueue = 0;
                this.comparableUser = new BinarySearchTree<>();
                if (first != null) {
                    userInQueue++;
                    comparableUser.insert(first);
                }
            }
        }
        matchmakingStarted = false;
        System.out.println("Spieler eingeteilt!");
    }

    public ListX<ComparableUser> getInOrderList(ListX<ComparableUser> resultList, BinarySearchTree<ComparableUser> tree) {
        if (tree.getLeftTree() != null) {
            getInOrderList(resultList, tree.getLeftTree());
        }
        if (tree.getContent() != null) {
            resultList.append(tree.getContent());
        }
        if (tree.getRightTree() != null) {
            getInOrderList(resultList, tree.getRightTree());
        }
        return resultList;
    }


    private static class ComparableUser implements ComparableContent<ComparableUser> {
        private int spielstaerke;
        private int uID;

        public ComparableUser(int uID, int spielstaerke) {
            this.uID = uID;
            this.spielstaerke = spielstaerke;
        }

        @Override
        public boolean isEqual(ComparableUser cc) {
            return spielstaerke == cc.spielstaerke && uID == cc.uID;
        }

        @Override
        public boolean isLess(ComparableUser cc) {
            if (spielstaerke < cc.spielstaerke) {
                return true;
            } else
                return uID < cc.uID;
        }

        @Override
        public boolean isGreater(ComparableUser cc) {
            if (spielstaerke > cc.spielstaerke) {
                return true;
            } else
                return uID > cc.uID;
        }
    }

    private class MatchmakingThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                System.out.println("Debug: Starte MatchmakingThread. Warte 30 Sekunden.");
                Thread.sleep(30 * 1000);
                System.out.println("Matchmaking wird durchgeführt.");
                synchronized (FightQueue.this) { //Es können keine neuen Spieler hinzukommen, bis die alten zugeordnet wurden
                    teileSpielerEin();
                    matchmakingStarted = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
