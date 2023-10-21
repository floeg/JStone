package de.loegler.jstone.server.main;

import de.loegler.jstone.core.*;
import de.loegler.jstone.core.event.*;
import de.loegler.jstone.server.PackOpeningManager;
import de.loegler.jstone.server.admin.sql.UserSQL;
import de.loegler.schule.algorithmen.Verschluesslung;
import de.loegler.schule.datenstrukturenExtensions.ListX;
import de.loegler.schule.netzwerk.QueryResult;
import de.loegler.schule.netzwerk.Server;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class JStoneServer extends Server {
    private ConcurrentHashMap<RemoteUser.Verbindungsinformationen, RemoteUser> remoteUserMap = new ConcurrentHashMap<>();
    /**
     * Die RSA-Keys der Benutzer.
     */
    private ConcurrentHashMap<RemoteUser.Verbindungsinformationen, RSAWrapper> rsaKeys = new ConcurrentHashMap<>();
    private FightQueue fightQueue = new FightQueue(this);
    DeckManager deckManager = new DeckManager();


    private Random rand = new Random();
    private HashMap<RemoteUser, ServerBattle> battleMap = new HashMap<>();
    private HashMap<RemoteUser, CollectionManager> collectionMap = new HashMap<>();
    private PackOpeningManager packOpening = new PackOpeningManager();

    // No longer used. Used for hashPWOld
    private BigInteger hashE = new BigInteger("43");
    private BigInteger hashN = new BigInteger("-1");
    //

    /**
     * Ein  Objekt  vom  Typ Server  wird  erstellt,  das  über  die  angegebene Portnummer  einen  Dienst  anbietet  an.
     * Clients  können  sich  mit  dem Server  verbinden,  so  dass  Daten  (Zeichenketten)  zu diesen  gesendet und von diesen empfangen werden können.
     * Kann der Server unter der angegebenen   Portnummer   keinen   Dienst   anbieten
     * (z.B.   weil   die Portnummer  bereits  belegt  ist),  ist  keine  Verbindungsaufnahme  zum Server und kein Datenaustausch möglich.
     *
     * @param port
     */
    public JStoneServer(int port) {
        super(port);
    }

    private RemoteUser.Verbindungsinformationen getVerbindungsInformationen(String ip, int port) {
        return new RemoteUser.Verbindungsinformationen(ip, port);
    }

    /**
     * Diese  Ereignisbehandlungsmethode  wird  aufgerufen,  wenn  sich  ein Client  mit  IP-Adresse pClientIP  und  Portnummer pClientPort
     * mit dem Server verbunden hat. Die Methode ist abstrakt und muss in einer Unterklasse  der  Klasse Server  überschrieben  werden,
     * so  dass  auf den  Neuaufbau  der  Verbindung  reagiert  wird.  Der  Aufruf  der  Methode erfolgt nicht synchronisiert.
     *
     * @param pClientIP   - Die IP-Adresse des Clients
     * @param pClientPort - Der Port des Clients
     */
    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        RemoteUser.Verbindungsinformationen tmp = getVerbindungsInformationen(pClientIP, pClientPort);
        remoteUserMap.put(tmp, new RemoteUser(tmp));
    }

    /**
     * Diese Ereignisbehandlungsmethode wird aufgerufen, wenn der Server  die Nachricht pMessage von dem durch pClientIP und pClientPortspezifizierten
     * Client  empfangen  hat.  Der  vom  Client  hinzugefügte Zeilentrenner wurde zuvor entfernt. Die Methode ist abstrakt und muss in  einer  Unterklasse
     * der  Klasse Server  überschrieben  werden,
     * so dass  auf  den  Empfang  der  Nachricht  reagiert  wird.  Der  Aufruf  der Methode erfolgt nicht synchronisiert.
     *
     * @param pClientIP   - Die IP-Adresse des Clients
     * @param pClientPort - Der Port des Clients
     * @param pMessage    - Die letzte Nachricht des Clients
     */
    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        System.out.println("Server: " + pMessage);
        RemoteUser.Verbindungsinformationen tmp = getVerbindungsInformationen(pClientIP, pClientPort);
        RemoteUser user = remoteUserMap.get(tmp);
        try {
            JSONObject jsonObject = JSONTools.fromJSON(pMessage);
            String eventTyp = jsonObject.getString(JSONAble.EVENTTYPKEY);
            if (eventTyp != null) {
                switch (eventTyp) {
                    case PublicKeyEvent.eventTyp: {
                        RSAWrapper wrapper = this.rsaKeys.get(user.getVerbindungsinformationen());
                        if (wrapper == null) {
                            wrapper = new RSAWrapper();
                            this.rsaKeys.put(user.getVerbindungsinformationen(), wrapper);
                        }
                        PublicKeyEvent publicKeyEvent = PublicKeyEvent.fromJSONObject(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        wrapper.setOtherE(publicKeyEvent.getSenderE());
                        wrapper.setOtherN(publicKeyEvent.getSenderN());
                        if (publicKeyEvent.isRequestOtherKey()) {
                            PublicKeyEvent toSend = new PublicKeyEvent(wrapper.thisRSA.getE(), wrapper.thisRSA.getN());
                            toSend.setRequestOtherKey(false);
                            this.send(pClientIP, pClientPort, JSONTools.getJSON(toSend.toJSONObject()));
                        }
                    }
                    break;
                    case LoginDataEvent.eventTyp: {
                        LoginDataEvent loginDataEvent = LoginDataEvent.fromJSONObject(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        String username = loginDataEvent.getUsername();
                        BigInteger cryptoPW = loginDataEvent.getCryptoPW();
                        String password = this.rsaKeys.get(user.getVerbindungsinformationen()).entschluessle(cryptoPW.toString());
                        if (username.contains(" ") || username.contains(";") || username.contains("'")) {
                            return; //Fehlermeldung o.ä.
                        }
                        QueryResult result = DatabaseManager.getInstance().fuehreSQLAus("SELECT PASSWORT FROM USERLOGIN WHERE USERNAME = '" + username + "'");
                        if (loginDataEvent.isRegister()) {
                            if (result.getRowCount() > 0) {
                                TextMessageEvent textMessageEvent =
                                        new TextMessageEvent(TextMessageEvent.LOGIN_FAILED,"Dieser Name ist bereits vergeben!");
                                send(user.getVerbindungsinformationen(),textMessageEvent);
                                return;
                            } else {
                                String hashedPW = hashPW(password);
                                int uID = UserSQL.createUser(username, hashedPW);
                                user.setUserID(uID);
                            }
                        } else {
                            //Anmelden
                            TextMessageEvent userOderPWFalsch = new TextMessageEvent(TextMessageEvent.LOGIN_FAILED,"Nutzer oder Passwort falsch!");
                            if (result.getRowCount() < 1)
                            { send(user.getVerbindungsinformationen(),userOderPWFalsch);
                                return; //Fehler, Benutzer nicht gefunden
                            }
                            else {
                                String hashedPW = result.getData()[0][0];
                                if (hashedPW.equals(hashPW(password))) {
                                    String userID = DatabaseManager.getInstance().fuehreSQLAus("SELECT UID FROM USERLOGIN WHERE USERNAME = '" + username + "'").getData()[0][0];
                                    user.setUserID(Integer.parseInt(userID));
                                    changePlayersState(pClientIP, pClientPort, user, ChangeStateEvent.MAINMENU);
                                    EmptyRequest emptyRequest = new EmptyRequest(EmptyRequest.LOGIN_SUCCESSFUL);
                                    send(user.getVerbindungsinformationen(),emptyRequest);
                                } else {
                                    send(user.getVerbindungsinformationen(),userOderPWFalsch);
                                    return;
                                }
                            }
                        }
                    } break;
                    case ChangeStateEvent.eventTyp: {
                        ChangeStateEvent event = ChangeStateEvent.fromJSONObject(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        String changeToState = event.getChangeToState();
                        if (user.getCurrentState().equals(event.getChangeToState())) {
                            System.out.println("Server, Debug: Client war bereits im STATE " + event.getChangeToState());
                            return; //User hat z.B. 2x auf einen Knopf gedrückt, bevor der Server die erste Nachricht bekommen hat
                        }
                        if (changeToState.equals(ChangeStateEvent.FIGHTQUEUE)) {
                            enterFightQueue(user);
                        } else if (changeToState.equals(ChangeStateEvent.COLLECTION)) {
                            changeToCollectionState(user);
                        } else if (changeToState.equals(ChangeStateEvent.MAINMENU)) {
                            user.setCurrentState(ChangeStateEvent.MAINMENU);
                            ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.MAINMENU);
                            send(user.getVerbindungsinformationen(), changeStateEvent);
                        } else if (changeToState.equals(ChangeStateEvent.PACKOPENING)) {
                            user.setCurrentState(ChangeStateEvent.PACKOPENING);
                            ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.PACKOPENING);
                            send(user.getVerbindungsinformationen(), changeStateEvent);
                        }
                    } break;
                    case TurnEvent.eventTyp: {
                        TurnEvent turnEvent = new TurnEvent(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        battleMap.get(user).onTurnEvent(user, turnEvent);
                    } break;

                    case PlayCardEvent.eventTyp: {
                        PlayCardEvent cardEvent = new PlayCardEvent(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        battleMap.get(user).onPlayCardEvent(user, cardEvent);
                    } break;
                    case AttackEvent.eventTyp: {
                        AttackEvent attackEvent = new AttackEvent(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        battleMap.get(user).onAttackRequest(user, attackEvent);
                    } break;
                    case RequestCollection.eventTyp: {
                        RequestCollection requestCollection = new RequestCollection(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        CollectionManager collectionManager = collectionMap.get(user);
                        if (collectionManager != null) {
                            Event toSend = collectionManager.onCollectionRequest(requestCollection);
                            if(toSend!=null)
                                send(user.getVerbindungsinformationen(),toSend);
                        }
                    } break;
                    case AddCardToDeckEvent.eventTyp: {
                        System.out.println("Eine Karte soll zu dem Deck hinzugefügt werden");
                        CollectionManager collectionManager = collectionMap.get(user);
                        if (collectionManager != null) {
                            Event toSend = collectionManager.onAddToDeckRequest(new AddCardToDeckEvent(jsonObject.getJSONObject(JSONAble.ARGUMENTE)));
                            if(toSend!=null)
                                send(user.getVerbindungsinformationen(),toSend);
                        }
                    }
                    break;
                    case EmptyRequest.eventTyp: {
                        EmptyRequest emptyRequest = new EmptyRequest(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        if (emptyRequest.getRequest().equals(EmptyRequest.REQUEST_DECKCOUNT)) {
                            System.out.println("Sende Anzahl der Decks");
                            int count = deckManager.getDeckCountOfUser(user.getUserID());
                            TextMessageEvent textRespond = new TextMessageEvent(TextMessageEvent.DECK_COUNT_RESPOND, count + "");
                            send(user.getVerbindungsinformationen(), textRespond);
                        } else if (emptyRequest.getRequest().equals(EmptyRequest.REQUEST_PACKNAMES)) {
                            ListX<String> packTypes = packOpening.getPackTypes();
                            StringBuilder packs = new StringBuilder();
                            for (packTypes.toFirst(); packTypes.hasAccess(); packTypes.next()) {
                                if (!packTypes.getContent().equals("Standard"))
                                    packs.append(packTypes.getContent()).append("/");
                            }
                            String packsString = "";
                            if (packs.lastIndexOf("/") != -1)
                                packsString = packs.substring(0, packs.lastIndexOf("/"));
                            TextMessageEvent toSend = new TextMessageEvent(TextMessageEvent.PACK_NAMES_RESPOND, packsString);
                            send(user.getVerbindungsinformationen(), toSend);
                        }else if(emptyRequest.getRequest().equalsIgnoreCase(EmptyRequest.REQUEST_FRIEND_UPDATE)){
                            ListX<String> friendList = UserSQL.getFriendList(user);
                            ListX<String> requestList = UserSQL.getFriendRequests(user);
                            ListEvent listEvent = new ListEvent(ListEvent.FRIEND_LIST,friendList);
                            ListEvent requestEvent = new ListEvent(ListEvent.FRIEND_REQUESTS,requestList);
                            send(user.getVerbindungsinformationen(),listEvent);
                            send(user.getVerbindungsinformationen(),requestEvent);
                        }
                    }
                    break;
                    case TextMessageEvent.eventTyp: {
                        TextMessageEvent textRespond = new TextMessageEvent(jsonObject.getJSONObject(JSONAble.ARGUMENTE));
                        if (textRespond.getRespondName().equals(TextMessageEvent.CHOSE_DECK_REQUEST)) {
                            String chosenDeckString = textRespond.getRespondValue();
                            int deckNR = Integer.parseInt(chosenDeckString);
                            user.setSelectedDeckID(deckManager.getDeckID(user.getUserID(), deckNR));
                            System.out.println("SelectedDeck von User: " + user.getSelectedDeckID());
                        } else if (textRespond.getRespondName().equals(TextMessageEvent.PACK_OPEN_REQUEST)) {
                            ListX<Karte> result = packOpening.openPack(user.getUserID(), textRespond.getRespondValue());
                            if (result != null) {
                                OpenPackCards openPackCards = new OpenPackCards(result);
                                send(user.getVerbindungsinformationen(), openPackCards);
                            } else {
                                System.out.println("Packung konnte nicht geöffnet werden!");
                                TextMessageEvent textMessageEvent = new TextMessageEvent(TextMessageEvent.WARNING_MESSAGE,"Fehler: Packung konnte nicht geöffnet werden.");
                                send(pClientIP, pClientPort, textMessageEvent);
                            }
                        }else if(textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.REQUEST_FRIEND)){
                            UserSQL.requestFriendship(user,textRespond.getRespondValue());
                            requestClientFriendRefresh(user);
                        }else if(textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.DENY_FRIEND)){
                            UserSQL.denyRequest(user,textRespond.getRespondValue());
                            requestClientFriendRefresh(user);
                        }else if(textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.ACCEPT_FRIEND)){
                            UserSQL.acceptRequest(user,textRespond.getRespondValue());
                            requestClientFriendRefresh(user);
                        }else if(textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.ECKEN_BEFREUNDET)){
                            double ret = UserSQL.eckenBefreundet(user,textRespond.getRespondValue());
                            String message = "Du bist mit " + textRespond.getRespondValue() + " um " +Math.round(ret) + " - Ecken befreundet";
                            if(ret==-1){
                                message="Fehler: Keine Verbindung zu " + textRespond.getRespondValue();
                            }else if(ret==-2){
                                message= "Fehler: Der Freundschaftsgraph wurde zu groß";
                            }
                            TextMessageEvent textMessageEvent = new TextMessageEvent(
                                    message.contains("Fehler") ? TextMessageEvent.WARNING_MESSAGE : TextMessageEvent.INFO_MESSAGE,message);
                            send(user.getVerbindungsinformationen(),textMessageEvent);
                        }
                    }
                    break;
                }
            } else {
            }
        } catch (JSONTools.JSONMalformedException e) {
            e.printStackTrace(); //CHOICE Client version modifiziert/veraltet -> Rauswerfen?
        }
    }

    private void requestClientFriendRefresh(RemoteUser client){
        EmptyRequest emptyRequest = new EmptyRequest(EmptyRequest.REQUEST_FRIEND_UPDATE);
        send(client.getVerbindungsinformationen(),emptyRequest);
    }

    private void changeToCollectionState(RemoteUser user) {
        ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.COLLECTION);
        user.setCurrentState(ChangeStateEvent.COLLECTION);
        send(user.getVerbindungsinformationen(), changeStateEvent);
        collectionMap.put(user, new CollectionManager(user));
    }


    private void enterFightQueue(RemoteUser user) {
        if (user.getCurrentState().equals(ChangeStateEvent.MAINMENU)) {
            System.out.println("Ein Benutzer betritt die FightQueue.");
            ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.FIGHTQUEUE);
            user.setCurrentState(ChangeStateEvent.FIGHTQUEUE);
            send(user.getVerbindungsinformationen(), changeStateEvent);
            fightQueue.addUser(user);
        } else {
            System.out.println("Server: Client versucht Queue zu betreten ohne im Hauptmenü zu sein!");
        }

    }

    /**
     * Lässt zwei Spieler gegeneinander kämpfen.
     * Kümmert sich darum, dass beiden Spieler in den Kampfmodus wechseln.
     */
    public void matchPlayers(RemoteUser user, RemoteUser other) {
        System.out.println("2 User wurden gematcht!");
        ChangeStateEvent event = new ChangeStateEvent(ChangeStateEvent.BATTLEFIELD);
        send(user.getVerbindungsinformationen(), event);
        send(other.getVerbindungsinformationen(), event);
        boolean userIsFirst = rand.nextBoolean();
        if (!userIsFirst) {
            RemoteUser swap = user;
            user = other;
            other = swap;
        }
        ServerBattle battle = new ServerBattle(this, user, other);
        battleMap.put(user, battle);
        battleMap.put(other, battle);
    }


    private void changePlayersState(String pClientIP, int pClientPort, RemoteUser user, String changeStateTo) {
        user.setCurrentState(changeStateTo);
        ChangeStateEvent event = new ChangeStateEvent(changeStateTo);
        send(pClientIP, pClientPort, event);
    }



    private String hashPWOld(String pw) {
        BigInteger tmp = new Verschluesslung().nachrichtVerschluesseln(pw, hashE, hashN);
        String result = tmp.toString(Character.MAX_RADIX);
        System.out.println(result);
        return result;
    }

    /*
    Entnommen aus
    https://www.geeksforgeeks.org/sha-512-hash-in-java/

     */
    private String hashPW(String pw) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] tmp = pw.getBytes(StandardCharsets.UTF_8);
        byte[] digest = sha.digest(tmp);
        BigInteger numeric = new BigInteger(1, digest);
        String result = numeric.toString(Character.MAX_RADIX); //So kurz wie möglich
        System.out.println(result);
        return result;
    }


    /**
     * Sendet ein {@link JSONAble} an einen Client.
     *
     * @param clientIP Die IP-Adresse des Clients
     * @param port     Der Port des Clients
     * @param object   Ein JSONAble, welches versendet werden soll
     */
    public void send(String clientIP, int port, JSONAble<? extends Object> object) {
        super.send(clientIP, port, JSONTools.getJSON(object.toJSONObject()));
    }


    /**
     * Sendet ein {@link JSONAble} an einen Client.
     *
     * @param verbindungsinformationen - Die Verbindungsinformationen des Clients
     * @param jsonAble                 - Ein JSONAble, welches versendet werden soll
     * @see #send(String, int, JSONAble)
     */
    public void send(RemoteUser.Verbindungsinformationen verbindungsinformationen, JSONAble<? extends Object> jsonAble) {
        send(verbindungsinformationen.getIpAddress(), verbindungsinformationen.getPort(), jsonAble);
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        RemoteUser.Verbindungsinformationen tmp = getVerbindungsInformationen(pClientIP, pClientPort);
        RemoteUser user = remoteUserMap.get(tmp);
        remoteUserMap.remove(tmp);
    }
}
