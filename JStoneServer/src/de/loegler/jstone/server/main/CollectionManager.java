package de.loegler.jstone.server.main;

import de.loegler.jstone.core.CollectionKarte;
import de.loegler.jstone.core.Karte;
import de.loegler.jstone.core.Klasse;
import de.loegler.jstone.core.event.AddCardToDeckEvent;
import de.loegler.jstone.core.event.Event;
import de.loegler.jstone.core.event.RequestCollection;
import de.loegler.jstone.core.event.RespondCollection;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.QueryResult;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Der CollectionManager kümmert sich um das gesamte Collection-Menu.
 */
public class CollectionManager {
    private RemoteUser user;
    private DeckManager deckManager = new DeckManager();
    private ListX<Karte> deckBuffer = new ListX<>();
    private Klasse deckKlasse = Klasse.MAGIER;
    /**
     * Speichert, wie oft eine Karte mit einer KartenID bereits im Deck vorhanden ist.
     */
    private HashMap<Integer, Integer> deckBufferKIDKCount = new HashMap<>();
    private int deckBufferSize = 0;

    /**
     * Erstellt einen neuen CollectionManager für einen Spieler
     *
     * @param user   Der Spieler
     */
    public CollectionManager(RemoteUser user) {
        this.user = user;
    }

    /**
     * Wird aufgerufen, wenn der Spieler eine Karte zu seinem Deck hinzufügen möchte
     *
     * @param event Das Event
     */
    public Event onAddToDeckRequest(AddCardToDeckEvent event) {
        int kid = getKID(event.getPage(), event.getIndex(), event.getFilter());
        Karte k = deckManager.getCopyOfCard(kid);
        if (k != null) {
            int maxAnzahlDeck = 2;
            if(k.getSeltenheit()== Karte.Seltenheit.LEGENDAER){
                System.out.println("Legendäre karte!");
                maxAnzahlDeck=1;
            }
            if (deckBufferKIDKCount.getOrDefault(kid, 0) < maxAnzahlDeck && deckBufferKIDKCount.getOrDefault(kid, 0) < this.deckManager.getCardCount(kid, user.getUserID())) {
                deckBuffer.append(k);
                deckBufferKIDKCount.put(kid, deckBufferKIDKCount.getOrDefault(kid, 0) + 1);
                deckBufferSize++;
                if (deckBufferSize == 30) {
                    createBufferedDeck();
                }
                return new AddCardToDeckEvent(event.getPage(), event.getIndex(), event.getFilter());
            }
        }
        return null;
    }

    /**
     * Erstellt ein Deck in der MySQL Datenbank basierend auf den Karten im Buffer
     */
    private void createBufferedDeck() {
        System.out.println("Erstelle Deck in MySQL");
        AtomicInteger index = new AtomicInteger(0);
        int deckIndex = 1;
        QueryResult q = DatabaseManager.getInstance().fuehreSQLAus("SELECT Count(DeckID) FROM DeckHatKlasse");
        if (q.getRowCount() == 1)
            deckIndex = Integer.parseInt(q.getData()[0][0]) + 1;
        DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO DeckHatKlasse VALUES (" + deckIndex + ", '" + this.deckKlasse.toString() + "')");
        int finalDeckIndex = deckIndex;
        this.deckBufferKIDKCount.forEach((key, value) -> {
            for (int i = 0; i != value; i++) {
                DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO Deck VALUES(" + finalDeckIndex + ", " + index.getAndIncrement() + ", " + key + ")");
            }
        });
        DatabaseManager.getInstance().fuehreSQLAus("INSERT INTO SpielerHatDeck VALUES(" + this.user.getUserID() + ", " + deckIndex + ")");
    }

    /**
     * Liefert die KartenID basierend auf der Seite, dem Index und dem gewählten Filter (Filter muss noch ergänzt werden)
     *
     * @return
     */
    public int getKID(int page, int index, int filter) {
        return 15 * page + index + 1; //Fängt bei 1 an zu Zählen
    }

    /**
     * Wird aufgerufen, sobald der Spieler eine Seite im Collection-Menu anzeigen möchte.
     *
     * @param requestCollection Informationen über die Seite
     */
    public Event onCollectionRequest(RequestCollection requestCollection) {
        int page = requestCollection.getPage();
        int selectionMode = requestCollection.getSelectionMode();
        int firstElement = 15 * page + 1; //MySQL fängt bei 1 an, daher +1
        int lastElement = firstElement + 14;
        ListX<CollectionKarte> kartenListe = new ListX<>();
        for (int i = 0; i != 15; i++) {
            Karte k = deckManager.getCopyOfCard(firstElement + i);
            if (k != null) {
                int anzahl = 0;
                QueryResult q = DatabaseManager.getInstance().
                        fuehreSQLAus("SELECT * FROM Karte WHERE KID=" + (firstElement + i) + " AND Erweiterung = 'Standard'");
                boolean isStandard = q != null && q.getRowCount() == 1;
                if (isStandard) {
                    anzahl = 2;
                } else {
                    QueryResult queryResult = DatabaseManager.getInstance().fuehreSQLAus("SELECT Anzahl FROM UserHatKarte WHERE KID = " + (firstElement + i) + " AND UID=" + user.getUserID());
                    if (queryResult.getRowCount() == 1) {
                        anzahl = Integer.parseInt(queryResult.getData()[0][0]);
                    }
                }
                kartenListe.append(new CollectionKarte(k, anzahl));
            } else {
                kartenListe.append(new CollectionKarte(Karte.getEmptyCardForCollection(), -1));
            }
        }
        return new RespondCollection(kartenListe, page);
    }
}
