package de.loegler.jstone.server.main;

import de.loegler.jstone.core.*;
import de.loegler.jstone.core.event.*;
import de.loegler.jstone.server.admin.sql.UserSQL;
import de.loegler.jstone.server.aktionen.AktionenAPI;
import de.loegler.jstone.server.aktionen.AktionsManager;
import de.loegler.schule.datenstrukturenExtensions.ListX;

public class ServerBattle implements AktionenAPI {

    private RemoteUser userOne, userTwo;
    private Spieler[] player = new Spieler[2];
    private JStoneServer server;
    private DeckManager deckManager = new DeckManager();
    private Deck userOneDeck, userTwoDeck;
    private Schlachtfeld schlachtfeld;
    private int spielerAmZug = 0;
    private AktionsManager aktionsManager = new AktionsManager(this);
    private int[] fatigue = new int[]{0,0};

    /**
     * Erstellt eine neue Serverversion des Schlachtfeldes, welcher sich um den kompletten Spielablauf kümmert.
     *
     * @param server  - Der Server
     * @param userOne - Der Benutzer mit der Spielnummer 0
     * @param userTwo - Der Benutzer mit der Spielnummer 1
     */
    public ServerBattle(JStoneServer server, RemoteUser userOne, RemoteUser userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        this.player[0] = new Spieler();
        this.player[1] = new Spieler();
        this.server = server;
        schlachtfeld = new Schlachtfeld();
        int deckUserID = userOne.getSelectedDeckID();
        int deckOtherID = userTwo.getSelectedDeckID();
        if (deckUserID == -1)
            deckUserID = 1;
        if (deckOtherID == -1)
            deckOtherID = 1; //StandardDeck
        this.userOneDeck = deckManager.getCopyOfDeck(deckUserID);
        this.userTwoDeck = deckManager.getCopyOfDeck(deckOtherID);
        if (userOneDeck == null)
            this.userOneDeck = deckManager.getCopyOfDeck(1);
        if (userTwoDeck == null)
            this.userTwoDeck = deckManager.getCopyOfDeck(1);
        if (userOneDeck == null) { //Falls DB nicht richtig eingerichtet wurde
            System.err.println("Kein Deck vorhanden. Wurde die Datenbank zurückgesetzt?");
            userOneDeck = new Deck(new ListX<>(), Klasse.MAGIER);
            userTwoDeck = new Deck(new ListX<>(), Klasse.MAGIER);
        }
        sendGameStartEvent();
    }


    /**
     * Startet den Zug eines Spielers
     *
     * @param neuAmZug Der Spieler, welcher nun am Zug ist
     */
    public void startTurn(int neuAmZug) {
        spielerAmZug = neuAmZug;
        TurnEvent turnStartEvent = new TurnEvent(spielerAmZug, true);
        int newMax = schlachtfeld.getMaxMana(spielerAmZug) + 1;
        schlachtfeld.setMaxMana(spielerAmZug, newMax);
        schlachtfeld.setCurrentMana(spielerAmZug, newMax);
        ManaChangeEvent manaChangeEvent = new ManaChangeEvent(newMax, newMax, spielerAmZug);
        sendBothPlayers(turnStartEvent);
        sendBothPlayers(manaChangeEvent);
        //Neue Runde -> Diener vom Spieler können (wieder) angreifen
        schlachtfeld.getDiener(spielerAmZug).forEach(it -> it.setCanAttack(true));
        drawCard(spielerAmZug);
    }

    /**
     * Ein Spieler zieht eine Karte
     *
     * @param spieler Wählt den Spieler aus
     */
    public void drawCard(int spieler) {
        Karte topElement;
        if (spieler == 0) {
            topElement = userOneDeck.top();
            userOneDeck.pop();
        } else {
            topElement = userTwoDeck.top();
            userTwoDeck.pop();
        }
        //DEBUG - Prüft, ob Aktion funktioniert
        // topElement = deckManager.getCopyOfCard(56);
        //
        if (topElement != null) {
            if(schlachtfeld.getHand(spieler)[9]==null){
                schlachtfeld.addCardToHand(spieler, topElement);
                DrawCardEvent cardEvent = new DrawCardEvent(topElement, spieler);
                DrawCardEvent emptyCardEvent = new DrawCardEvent(Karte.getEmptyCardForEnemy(), spieler);
                if (spieler == 0) {
                    server.send(userOne.getVerbindungsinformationen(), cardEvent);
                    server.send(userTwo.getVerbindungsinformationen(), emptyCardEvent);
                } else {
                    server.send(userTwo.getVerbindungsinformationen(), cardEvent);
                    server.send(userOne.getVerbindungsinformationen(), emptyCardEvent);
                }
            }
        }else{
            int damage = ++fatigue[spieler];
            this.dealDamage(spieler,0, damage);
        }
    }

    @Override
    public int getDeckCardsLeft(int playerNumber) {
        Deck deck;
        if(playerNumber==0){
            deck=userOneDeck;
        }else{
            deck=userTwoDeck;
        }
        return deck.getKartenAnzahl();
    }

    /**
     * @return Die Spielnummer des aktiven Spielers, 0 oder 1.
     */
    @Override
    public int getCurrentGameNumber() {
        return spielerAmZug;
    }

    /**
     * Fügt einem Charakter Schaden zu.
     *
     * @param side   Die Seite, entsprechend der GameNumber
     * @param id     - Die ID des Charakters. 0 fuer den Helden, 1-7 fuer Diener.
     * @param damage Der Schaden
     */
    @Override
    public void dealDamage(int side, int id, int damage) {
        System.out.println("Füge " + side + " mit ID " + id + " Schaden zu!");
        if (id == 0) {
            this.player[side].setLeben(player[side].getLeben() - damage);
            DamageEvent damageEvent = new DamageEvent(damage, side, id);
            sendBothPlayers(damageEvent);
            if (player[side].getLeben() <= 0) {
                this.endGame(1 - side);
            }
        } else {
            //Unterstützung für Diener hier möglichst
        }
    }

    /**
     * Rückgabe einer Liste aller Diener eines Spielers
     *
     * @param side Die Spielnummer des Spielers
     * @return Eine Liste aller Diener eines Spielers
     */
    @Override
    public ListX<Diener> getDiener(int side) {
        return schlachtfeld.getDiener(side);
    }

    @Override
    public void summonMinion(int kid, int anzahl, int side) {
        for (int i = 0; i != anzahl; i++) {
            Karte k = deckManager.getCopyOfCard(kid);
            Diener d = (Diener) k;
            summonMinion(d, side, true);
        }
    }

    @Override
    public void healHero(int side, int amount) {
        int leben = this.player[side].getLeben() + amount;
        if (leben > 30)
            leben = 30;
        this.player[side].setLeben(leben);
        DamageEvent damageEvent = new DamageEvent(-amount, side, 0);
        sendBothPlayers(damageEvent);
    }

    private void summonMinion(Diener d, int side, boolean skipEffect) {
        if (schlachtfeld.getDiener(side).calculateSize() < 7) {
            schlachtfeld.playDiener(spielerAmZug, d);
            //Führe Kampfschrei effekt aus. Es passiert nichts, wenn der Diener keinen Effekt hat.
            if (!skipEffect) {
                this.aktionsManager.fuehreAktionAus(d, d.getKampfschreiID(), side);
            }
            DienerPlayedEvent dienerPlayedEvent = new DienerPlayedEvent(spielerAmZug, d);
            sendBothPlayers(dienerPlayedEvent);
        } else {
            System.out.println("Schlachtfeld war bereits voll!");
        }
    }

    /**
     * Wird aufgerufen, sobald ein Spieler seinen Zug beenden möchte.
     *
     * @param user      Der Nutzer, welcher seinen Zug beenden möchte
     * @param turnEvent Das TurnEvent
     */
    public void onTurnEvent(RemoteUser user, TurnEvent turnEvent) {
        if (spielerAmZug == 0) {
            if (user.equals(userTwo))
                return;
        } else {
            if (user.equals(userOne)) //Spieler 2 ist am Zug! Manipulation
                return;
        }
        TurnEvent endTurnEvent = new TurnEvent(spielerAmZug, false);
        sendBothPlayers(endTurnEvent);
        startTurn(1 - spielerAmZug);
    }


    /**
     * Hilfsmethode, sendet ein Event an beide Spieler
     *
     * @param event Das Event, welches an beide Spieler gesendet werden soll.
     */
    private void sendBothPlayers(Event event) {
        server.send(userOne.getVerbindungsinformationen(), event);
        server.send(userTwo.getVerbindungsinformationen(), event);
    }

    /**
     * Startet das Spiel, lässt beide Spieler Karten ziehen.
     */
    private void sendGameStartEvent() {
        GameStartEvent oneEvent = new GameStartEvent(userTwoDeck.getKlasse().toString(),
                userTwo.getDisplayname(), userOneDeck.getKlasse().toString(), 0 + "");
        GameStartEvent twoEvent = new GameStartEvent(userOneDeck.getKlasse().toString(),
                userOne.getDisplayname(), userTwoDeck.getKlasse().toString(), 1 + "");
        server.send(userOne.getVerbindungsinformationen(), oneEvent);
        server.send(userTwo.getVerbindungsinformationen(), twoEvent);
        //Startkarten
        for (int i = 0; i != 3; i++) {
            drawCard(0);
            drawCard(1);
        }
        startTurn(0);
    }

    /**
     * Wird aufgerufen, sobald ein Spieler eine Karte ausspielen möchte
     *
     * @param user      Der Spieler, welcher eine Karte ausspielen möchte
     * @param cardEvent Das PlayCardEvent
     */
    public void onPlayCardEvent(RemoteUser user, PlayCardEvent cardEvent) {
        //Stellt sicher, dass die Anfrage wirklich vom aktiven Spieler kommt
        if (spielerAmZug == 0)
            if (user.equals(userTwo))
                return;
        if (spielerAmZug == 1)
            if (user.equals(userOne))
                return;
        if (cardEvent.getSide() != spielerAmZug)
            return; // Karte des Gegners kann nicht ausgespielt werden - manipulation!
        //Prüfe, ob Karte gespielt werden kann - wenn nicht: Kein Fehler, da Client manipuliert!
        int currentMana = schlachtfeld.getCurrentMana(spielerAmZug);
        int dienerCount = schlachtfeld.getDienerCount(spielerAmZug);
        Karte k = schlachtfeld.getHand(spielerAmZug, cardEvent.getHandID());
        if (k == null)
            return;
        if (k.getManakosten() <= currentMana) {
            if (k instanceof Diener) {
                Diener d = (Diener) k;
                if (dienerCount < 7) {
                    _playCard(cardEvent, currentMana, k);
                    summonMinion((Diener) k, spielerAmZug, false);
                }
            } else {
                Zauber z = (Zauber) k;
                _playCard(cardEvent, currentMana, k);
                this.aktionsManager.fuehreAktionAus(z, z.getAktionsID(), spielerAmZug);
            }
        }
    }

    private void _playCard(PlayCardEvent cardEvent, int currentMana, Karte k) {
        currentMana -= k.getManakosten();
        schlachtfeld.setCurrentMana(spielerAmZug, currentMana);
        ManaChangeEvent manaChangeEvent = new ManaChangeEvent(schlachtfeld.getMaxMana(spielerAmZug), currentMana, spielerAmZug);
        sendBothPlayers(manaChangeEvent);
        schlachtfeld.removeCard(spielerAmZug, cardEvent.getHandID());
        PlayCardEvent playCardEvent = new PlayCardEvent(cardEvent.getHandID(), spielerAmZug);
        sendBothPlayers(playCardEvent);
    }

    /**
     * Wird aufgerufen, sobald ein Spieler ein Ziel angreifen möchte
     *
     * @param user        Der Spieler
     * @param attackEvent AttackEvent mit Zielinformationen
     */
    public void onAttackRequest(RemoteUser user, AttackEvent attackEvent) {
        if (spielerAmZug == 1)
            if (user == this.userOne)
                return;
        if (spielerAmZug == 0)
            if (user == userTwo)
                return;
        int sourceSide = attackEvent.getSourceSide();
        int targetSide = attackEvent.getTargetSide();
        if (sourceSide == spielerAmZug && targetSide == 1 - spielerAmZug) { //Eigener Diener muss einen Gegner angreifen
            if (attackEvent.getSourceNumber() > 0) {
                //Source ist ein Diener
                Diener source = schlachtfeld.getDiener(spielerAmZug).get(attackEvent.getSourceNumber() - 1);
                if (attackEvent.getTargetNumber() > 0 && source != null) {
                    //Target ist ein Diener
                    Diener target = schlachtfeld.getDiener(1 - spielerAmZug).get(attackEvent.getTargetNumber() - 1);
                    if (target != null && source.canAttack(schlachtfeld, target, spielerAmZug)) {
                        AttackEvent attackEvent1 = new AttackEvent(spielerAmZug, attackEvent.getSourceNumber(), attackEvent.getTargetSide(), attackEvent.getTargetNumber());
                        sendBothPlayers(attackEvent1); //GGF. Animation & ausführen des Angriffes (source.angreifen auf der Seite beider Clients)
                        source.angreifen(target, schlachtfeld, spielerAmZug);

                        if (target.getLeben() <= 0) {
                            onDienerDeath(1 - spielerAmZug, target);
                        }
                    }
                } else if (attackEvent.getTargetNumber() == 0 && source.canAttackEnemyHero(schlachtfeld,spielerAmZug)) {
                    AttackEvent heroAttackEvent = new AttackEvent(sourceSide, attackEvent.getSourceNumber(), targetSide, attackEvent.getTargetNumber());
                    sendBothPlayers(heroAttackEvent);

                    this.player[1 - spielerAmZug].setLeben(this.player[1 - spielerAmZug].getLeben() - source.getAngriff());
                    source.setCanAttack(false);

                    if (this.player[1 - spielerAmZug].getLeben() <= 0) {
                        endGame(spielerAmZug);
                    }
                }
                if (source.getLeben() <= 0) {
                    onDienerDeath(spielerAmZug, source);
                }
            }
        }
    }


    /**
     * Wird am Ende des Spiels aufgerufen.
     *
     * @param winnerGameNumber Der Gewinner des Spiels
     */
    public void endGame(int winnerGameNumber) {
        System.out.println(winnerGameNumber + " -Spieler hat gewonnen!");
        RemoteUser winner = null;
        RemoteUser other = null;
        if (winnerGameNumber == 0) {
            winner = userOne;
            other = userTwo;
        } else {
            winner = userTwo;
            other = userOne;
        }
        winner.setCurrentState(ChangeStateEvent.MAINMENU);
        other.setCurrentState(ChangeStateEvent.MAINMENU);

        UserSQL.addPunkte(winner.getUserID(), 5);
        UserSQL.addGold(winner.getUserID(), 5);
        ChangeStateEvent backToMenu = new ChangeStateEvent(ChangeStateEvent.MAINMENU);
        sendBothPlayers(backToMenu);
    }

    /**
     * Entfernt einen Diener und führt dessen Todesröcheln aus.
     * Informiert NICHT die Clients, dass der Diener gestorben ist.
     * Dies geschieht meist indirekt, da die Clients die attack-Methode selber aufrufen.
     * Aktionen werden jedoch übermittelt, da die Ausführung nur Serverseitig erfolgt.
     *
     * @param side Die Seite
     * @param d    Der Diener, welcher gestorben ist
     */
    private void onDienerDeath(int side, Diener d) {
        this.aktionsManager.fuehreAktionAus(d, d.getTodesroechelnID(), side);
        schlachtfeld.removeDiener(side, d);
    }
}
