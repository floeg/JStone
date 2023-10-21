package de.loegler.jstone.client.main;

import de.loegler.jstone.core.event.Event;
import de.loegler.jstone.core.event.TextMessageEvent;

public class FriendManager {
    private JStoneClient client;

    public FriendManager(JStoneClient client){
        this.client=client; //FriendFrame nun in JStoneClient
    }


    public Event acceptFriendship(String username){
        TextMessageEvent event = new TextMessageEvent(TextMessageEvent.ACCEPT_FRIEND,username);
        client.send(event);
        return event;
    }

    public Event denyFriendship(String username){
        TextMessageEvent event = new TextMessageEvent(TextMessageEvent.DENY_FRIEND,username);
        client.send(event);
        return event;
    }

    public Event sendFriendshipRequest(String username){
        TextMessageEvent event = new TextMessageEvent(TextMessageEvent.REQUEST_FRIEND,username);
        client.send(event);
        return event;
    }



    public Event requestEcken(String username){
        TextMessageEvent textMessageEvent = new TextMessageEvent(TextMessageEvent.ECKEN_BEFREUNDET,username);
        client.send(textMessageEvent);
        return textMessageEvent;
    }
}
