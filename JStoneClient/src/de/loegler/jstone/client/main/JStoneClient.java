package de.loegler.jstone.client.main;

import de.loegler.core.gui.TextFrame;
import de.loegler.jstone.client.gui.FriendFrame;
import de.loegler.jstone.core.JSONAble;
import de.loegler.jstone.core.JSONObject;
import de.loegler.jstone.core.JSONTools;
import de.loegler.jstone.core.RSAWrapper;
import de.loegler.jstone.core.event.*;
import de.loegler.schule.netzwerk.Client;

import javax.swing.*;
import java.math.BigInteger;

public class JStoneClient extends Client {
    private RSAWrapper wrapper = new RSAWrapper();
    private MessageThread messageThread;
    private JStoneGUI gui;
    private ClientBattle battle;
    private String username = "Du";
    private FriendManager friendManager;
    private FriendFrame friendFrame;
    private PreJStoneGUI preGUI;


    /**
     * Es wird eine Verbindung zum durch IP Adresse und
     * Portnummer
     * angegebenen Server aufgebaut, so dass Zeichenketten gesendet
     * und empfangen werden können.
     *
     * @param pServerIP
     * @param pServerPort
     */
    public JStoneClient(String pServerIP, int pServerPort,PreJStoneGUI preGUI) {
        super(pServerIP, pServerPort);
        this.preGUI=preGUI;
        messageThread = new MessageThread();
        messageThread.start();
    }


    public void sendeLoginEvent(String user, String pw, boolean register) {
        BigInteger cryptPW = new BigInteger(wrapper.verschluessle(pw));
        username = user;
        LoginDataEvent event = new LoginDataEvent(user, cryptPW);
        event.setRegister(register);
        send(event);

    }


    public void send(JSONAble<? extends Object> object) {
        send(JSONTools.getJSON(object.toJSONObject()));
    }


    public void tauscheRSASchluessel() {
        PublicKeyEvent event = new PublicKeyEvent(this.wrapper.thisRSA.getE(), this.wrapper.thisRSA.getN());
        event.setRequestOtherKey(true);
        this.send(JSONTools.getJSON(event.toJSONObject()));
    }


    /**
     * Nachdem der Server die angegebene Nachricht
     * pMessage gesendet hat wurde der Zeilentrenner entfernt. Der
     * Client  kann auf die Nachricht pMessage	in dieser Methode
     * reagieren. Allerdings enthält diese	Methode keine Anweisungen und
     * muss in Unterklassen überschrieben werden, damit die Nachricht
     * verarbeitet werden kann.
     *
     * @param pMessage
     **/
    @Override
    public void processMessage(String pMessage) {
        try {
            JSONObject message = JSONTools.fromJSON(pMessage);
            String eventTyp = message.getString(JSONAble.EVENTTYPKEY);
            switch (eventTyp) {
                case PublicKeyEvent.eventTyp: {
                    JSONObject keyObject = message.getJSONObject(JSONAble.ARGUMENTE);
                    PublicKeyEvent event = PublicKeyEvent.fromJSONObject(keyObject);
                    this.wrapper.setOtherN(event.getSenderN());
                    this.wrapper.setOtherE(event.getSenderE());
                    if (event.isRequestOtherKey()) {
                        PublicKeyEvent tmp = new PublicKeyEvent(this.wrapper.thisRSA.getE(), this.wrapper.thisRSA.getN());
                        tmp.setRequestOtherKey(false);
                        send(JSONTools.getJSON(tmp.toJSONObject()));

                    }
                }
                break;
                case ChangeStateEvent.eventTyp: {
                    ChangeStateEvent changeStateEvent = ChangeStateEvent.fromJSONObject(message.getJSONObject(JSONAble.ARGUMENTE));
                    String changeTo = changeStateEvent.getChangeToState();
                    switch (changeTo) {
                        case ChangeStateEvent.MAINMENU: {
                            if (gui == null)
                                gui = new JStoneGUI(this);
                            gui.changeState(ChangeStateEvent.MAINMENU);
                        }
                        break;
                        case ChangeStateEvent.FIGHTQUEUE: {
                            System.out.println("Debug: Betrete FightQueue");
                            gui.changeState(ChangeStateEvent.FIGHTQUEUE);
                        }
                        break;
                        case ChangeStateEvent.BATTLEFIELD: {
                            System.out.println("Debug: Es wurde ein Gegner gefunden! Betrete Schlachtfeld");
                            gui.changeState(ChangeStateEvent.BATTLEFIELD); //Gegner Informationen werden zusätzlich versendet
                        }
                        break;
                        case ChangeStateEvent.COLLECTION: {
                            gui.changeState(ChangeStateEvent.COLLECTION);
                            Thread.sleep(550);
                            RequestCollection requestCollection = new RequestCollection(0, RequestCollection.SELECT_ALL);
                            send(requestCollection);

                        }
                        break;
                        case ChangeStateEvent.PACKOPENING: {
                            gui.changeState(ChangeStateEvent.PACKOPENING);
                            Thread.sleep(550);
                            EmptyRequest emptyRequest = new EmptyRequest(EmptyRequest.REQUEST_PACKNAMES);
                            send(emptyRequest);
                        }
                        break;
                    }
                }
                break;

                case GameStartEvent.eventTyp: {
                    GameStartEvent event = new GameStartEvent(message.getJSONObject(JSONAble.ARGUMENTE));
                    battle = new ClientBattle(this, this.gui.getBattlefieldPanel(), event);
                }
                break;
                case TurnEvent.eventTyp: {
                    TurnEvent event = new TurnEvent(message.getJSONObject(JSONAble.ARGUMENTE));
                    this.battle.onTurnEvent(event);
                }
                break;
                case ManaChangeEvent.eventTyp: {
                    ManaChangeEvent manaChangeEvent = new ManaChangeEvent(message.getJSONObject(JSONAble.ARGUMENTE));
                    this.battle.onManaChangeEvent(manaChangeEvent);
                }
                break;
                case DrawCardEvent.eventTyp: {
                    this.battle.onDrawnCard(new DrawCardEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case PlayCardEvent.eventTyp: {
                    this.battle.onPlayCard(new PlayCardEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case DienerPlayedEvent.eventTyp: {
                    this.battle.onDienerPlayed(new DienerPlayedEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case AttackEvent.eventTyp: {
                    this.battle.onAttackEvent(new AttackEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case RespondCollection.eventTyp: {
                    this.gui.getDeckGUI().onCollectionRespond(new RespondCollection(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case AddCardToDeckEvent.eventTyp: {
                    this.gui.getDeckGUI().onAddCardToDeckRespond(new AddCardToDeckEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;

                case TextMessageEvent.eventTyp: {
                    TextMessageEvent textRespond = new TextMessageEvent(message.getJSONObject(JSONAble.ARGUMENTE));
                    if (textRespond.getRespondName().equals(TextMessageEvent.DECK_COUNT_RESPOND)) {
                        int deckCount = Integer.parseInt(textRespond.getRespondValue());
                        String m = JOptionPane.showInputDialog("Bitte wähle dein Deck von 1-" + deckCount);
                        int res = Integer.parseInt(m);
                        TextMessageEvent respond = new TextMessageEvent(TextMessageEvent.CHOSE_DECK_REQUEST, res + "");
                        send(respond);
                    } else if (textRespond.getRespondName().equals(TextMessageEvent.PACK_NAMES_RESPOND)) {

                        String[] packs = textRespond.getRespondValue().split("/");
                        gui.getPackopeningPanel().setPackNames(packs);
                    }
                    else if( textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.WARNING_MESSAGE)){
                        new TextFrame(new TextFrame.TextFrameOptions().changeMode(TextFrame.TextFrameOptions.WARNING_MODE).enableAccept(),
                                "Fehler:",textRespond.getRespondValue());
                    }
                    else if(textRespond.getRespondName().equalsIgnoreCase(TextMessageEvent.INFO_MESSAGE)){
                        new TextFrame(new TextFrame.TextFrameOptions().changeMode(TextFrame.TextFrameOptions.MESSAGE_MODE).enableAccept(),
                                "Information",textRespond.getRespondValue());
                    }
                }
                break;

                case OpenPackCards.eventTyp: {
                    gui.getPackopeningPanel().showPackOpening(new OpenPackCards(message.getJSONObject(JSONAble.ARGUMENTE)).getCards());
                }
                break;

                case DamageEvent.eventTyp: {
                    battle.onDamage(new DamageEvent(message.getJSONObject(JSONAble.ARGUMENTE)));
                }
                break;
                case ListEvent.eventTyp:{
                    ListEvent listEvent = new ListEvent(message.getJSONObject(JSONAble.ARGUMENTE));
                    String typ = listEvent.getTyp();
                    if(typ.equalsIgnoreCase(ListEvent.FRIEND_LIST)){
                        listEvent.getData().forEach(it -> friendFrame.addFriend(it));
                    }else if(typ.equalsIgnoreCase(ListEvent.FRIEND_REQUESTS)){
                        listEvent.getData().forEach(it -> friendFrame.addFreundschaftsanfrage(it));
                    }
                }break;
                case EmptyRequest.eventTyp:{
                    EmptyRequest emptyRequest = new EmptyRequest(message.getJSONObject(JSONAble.ARGUMENTE));
                    if(emptyRequest.getRequest().equalsIgnoreCase(EmptyRequest.REQUEST_FRIEND_UPDATE)){
                        this.friendFrame.dispose();
                        friendFrame.dispose();
                        friendFrame = new FriendFrame(friendManager);
                        EmptyRequest emptyRequest2 = new EmptyRequest(EmptyRequest.REQUEST_FRIEND_UPDATE);
                        send(emptyRequest);


                    }else if(emptyRequest.getRequest().equalsIgnoreCase(EmptyRequest.LOGIN_SUCCESSFUL)){
                        //Erfolgreich eingeloggt
                        preGUI.onLoginSuccessful();
                    }


                }break;

            }
        } catch (JSONTools.JSONMalformedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void betreteQueue() {
        ChangeStateEvent request = new ChangeStateEvent(ChangeStateEvent.FIGHTQUEUE);
        this.send(request);
    }


    public void betreteMainMenu() {
        ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.MAINMENU);
        send(changeStateEvent);
    }

    public void requestCollectionPage(int page) {
        RequestCollection requestCollection = new RequestCollection(page, RequestCollection.SELECT_ALL);
        send(requestCollection);
    }

    public String getUsername() {
        return username;
    }

    public void oeffneCollection() {
        ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.COLLECTION);
        send(changeStateEvent);
    }

    public void onShowFriendFrame(){
        this.friendManager =new FriendManager(this);
        this.friendFrame = new FriendFrame(friendManager);
        EmptyRequest emptyRequest = new EmptyRequest(EmptyRequest.REQUEST_FRIEND_UPDATE);
        send(emptyRequest);
    }


    /**
     * Sendet eine Anfrage an den Server eine Karte zu dem aktuellen Deck hinzuzufügen
     *
     * @param collectionPage Die aktuelle Seite
     * @param pNumber        Die Nummer der Karte auf der Seite
     */
    public void requestAddToDeck(int collectionPage, int pNumber) {
        AddCardToDeckEvent addEvent = new AddCardToDeckEvent(collectionPage, pNumber, RequestCollection.SELECT_ALL);
        send(addEvent);
    }

    public void requestDeckCount() {
        EmptyRequest emptyRequest = new EmptyRequest(EmptyRequest.REQUEST_DECKCOUNT);
        send(emptyRequest);
    }

    public void oeffnePackungen() {
        ChangeStateEvent changeStateEvent = new ChangeStateEvent(ChangeStateEvent.PACKOPENING);
        send(changeStateEvent);
    }

    public void requestOpenPack(String packSelected) {
        TextMessageEvent textMessageEvent = new TextMessageEvent(TextMessageEvent.PACK_OPEN_REQUEST, packSelected);
        send(textMessageEvent);
    }

    public class MessageThread extends Thread {
        /**
         * Erstellt einen neuen MessageThread.
         * Wird beendet, nachdem alle Fenster geschlossen wurden.
         */
        public MessageThread(){
            super();
            super.setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
            while (!this.isInterrupted()) {
                String tmp = JStoneClient.this.connect.receive();
                if (tmp != null)
                    processMessage(tmp);
                else {
                    this.interrupt();
                    new TextFrame(new TextFrame.TextFrameOptions().enableAccept().changeMode(TextFrame.TextFrameOptions.ERROR_MODE), "Verbindung",
                            "Die Verbindung zum Server wurde unterbrochen.", "Bitte starte die Anwendung neu.");
                }
            }
        }
    }
}
