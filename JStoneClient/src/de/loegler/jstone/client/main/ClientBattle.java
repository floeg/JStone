package de.loegler.jstone.client.main;

import de.loegler.jstone.client.gui.BattlefieldPanel;
import de.loegler.jstone.core.Diener;
import de.loegler.jstone.core.Schlachtfeld;
import de.loegler.jstone.core.Spieler;
import de.loegler.jstone.core.event.*;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientBattle {

    private JStoneClient client;
    private BattlefieldPanel bGUI;
    private Schlachtfeld schlachtfeld = new Schlachtfeld();
    private Spieler[] spieler = new Spieler[2];
    private boolean amZug = false;
    /**
     * Die eigene Spielnummer, welche vom Server verwendet wird.
     * Der Gegner besitzt die Spielnummer 1-gameNumber.
     */
    private int gameNumber;

    public ClientBattle(JStoneClient client, BattlefieldPanel panel, GameStartEvent event) {
        this.client = client;
        this.gameNumber = event.getOwnGameNumber();
        spieler[gameNumber] = new Spieler();
        spieler[1 - gameNumber] = new Spieler();
        this.bGUI = panel;
        bGUI.setClientBattle(this);
        bGUI.updateNames(client.getUsername(), event.getEnemyName());
    }

    public void endTurnClient() {
        TurnEvent turnEvent = new TurnEvent(gameNumber, false);
        client.send(turnEvent);
        amZug = false;
    }


    public void onDrawnCard(DrawCardEvent event) {
        schlachtfeld.addCardToHand(event.getSide(), event.getKarte());
        if (event.getSide() == gameNumber) {
            bGUI.cardDrawn(1, event.getKarte());
        } else
            bGUI.cardDrawn(0, event.getKarte());
    }

    public void onTurnEvent(TurnEvent turnEvent) {
        if (turnEvent.getPlayerGameNumber() == gameNumber) {
            if (turnEvent.isStart()) {
                //Neuer Zug = Diener können (wieder) angreifen
                schlachtfeld.getDiener(gameNumber).forEach(it -> it.setCanAttack(true));
                amZug = true;
                bGUI.changeEndTurnState(true);
            } else {
                bGUI.changeEndTurnState(false);
            }
        }
        bGUI.zeichneDienerNeu(schlachtfeld.getDiener(gameNumber),getGUINumber(gameNumber));
        bGUI.zeichneDienerNeu(schlachtfeld.getDiener(1-gameNumber),getGUINumber(1-gameNumber));
    }

    public void onManaChangeEvent(ManaChangeEvent manaChangeEvent) {
        int side = 0;
        if (manaChangeEvent.getSide() == gameNumber)
            side = 1;
        bGUI.changeMana(manaChangeEvent.getMaxMana(), manaChangeEvent.getCurrentMana(), side);
        schlachtfeld.setMaxMana(manaChangeEvent.getSide(), manaChangeEvent.getMaxMana());
        schlachtfeld.setCurrentMana(manaChangeEvent.getSide(), manaChangeEvent.getCurrentMana());
    }


    public Schlachtfeld getSchlachtfeld() {
        return schlachtfeld;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void requestCardPlay(int handIndex) {
        PlayCardEvent playCardEvent = new PlayCardEvent(handIndex, getGameNumber());
        client.send(playCardEvent);
    }


    public void onPlayCard(PlayCardEvent playCardEvent) {
        int side = 0;
        if (playCardEvent.getSide() == getGameNumber())
            side = 1;
        schlachtfeld.removeCard(playCardEvent.getSide(), playCardEvent.getHandID());
        bGUI.zeichneHandNeu(schlachtfeld.getHand(playCardEvent.getSide()), side);
    }

    public void onDienerPlayed(DienerPlayedEvent dienerPlayedEvent) {
        Diener d = dienerPlayedEvent.getDiener();
        int side = dienerPlayedEvent.getSide();
        schlachtfeld.playDiener(side, d);
        int guiSide;
        if (side == this.gameNumber) {
            guiSide = 1;
        } else guiSide = 0;
        bGUI.zeichneDienerNeu(schlachtfeld.getDiener(side), guiSide);
    }

    public void requestAttackMinion(Diener source, Diener target) {
        AtomicInteger sourceNumber = new AtomicInteger(-2); //Wird zu -1, wenn sourceNumber nicht existiert
        int targetSide = 1 - gameNumber;
        AtomicInteger targetNumber = new AtomicInteger(-2);
        schlachtfeld.getDiener(gameNumber).forEachIndexed((it, index) -> {
            if (it == source) {
                sourceNumber.set(index);
            }
        });
        schlachtfeld.getDiener(1 - gameNumber).forEachIndexed((it, index) -> {
            if (it == target) {
                targetNumber.set(index);
            }
        });
        if (targetNumber.get() == -1 || sourceNumber.get() == -1) {
            bGUI.sendToMessageChannel("Diese Aktion kann nicht durchgeführt werden!"); //Eigene Diener können nicht angegriffen werden
        } else {
            //+1, da 0 für den Helden steht
            AttackEvent attackEvent = new AttackEvent(gameNumber, sourceNumber.get() + 1, targetSide, targetNumber.get() + 1);
            client.send(attackEvent);
        }
    }

    public void onAttackEvent(AttackEvent attackEvent) {
        int sourceNumber = attackEvent.getSourceNumber();
        int targetNumber = attackEvent.getTargetNumber();
        int targetSide = attackEvent.getTargetSide();
        int sourceSide = attackEvent.getSourceSide();
        if (sourceNumber >= 1) {
            Diener source = schlachtfeld.getDiener(sourceSide).get(sourceNumber - 1); //0 steht für den Helden, Diener sind bei 1-7
            if (targetNumber >= 1) {
                Diener target = schlachtfeld.getDiener(targetSide).get(targetNumber - 1);
                source.angreifen(target, schlachtfeld, sourceSide);
                if (target.getLeben() <= 0) {
                    schlachtfeld.removeDiener(targetSide, target);
                }
                bGUI.zeichneDienerNeu(schlachtfeld.getDiener(targetSide), getGUINumber(targetSide));
            } else {
                spieler[attackEvent.getTargetSide()].setLeben(spieler[attackEvent.getTargetSide()].getLeben() - source.getAngriff());
                bGUI.updateHeroHealth(getGUINumber(attackEvent.getTargetSide()), spieler[attackEvent.getTargetSide()].getLeben());
            }
            if (source.getLeben() <= 0) {
                schlachtfeld.removeDiener(sourceSide, source);
            }
            bGUI.zeichneDienerNeu(schlachtfeld.getDiener(sourceSide), getGUINumber(sourceSide));
        }
    }


    public int getGUINumber(int side) {
        if (side == gameNumber)
            return 1;
        else
            return 0;
    }

    public void requestAttackHero(Diener source) {
        int dienerIndex = schlachtfeld.getDiener(getGameNumber()).getIndex(source);
        AttackEvent attackEvent = new AttackEvent(getGameNumber(), dienerIndex + 1, 1 - getGameNumber(), 0);
        client.send(attackEvent);
    }

    public void onDamage(DamageEvent damageEvent) {
        int side = damageEvent.getTargetSide();
        this.spieler[side].setLeben(spieler[side].getLeben() - damageEvent.getDamage());
        if (spieler[side].getLeben() > 30)
            spieler[side].setLeben(30);
        int guiSide = getGUINumber(damageEvent.getTargetSide());
        bGUI.updateHeroHealth(guiSide, spieler[side].getLeben());
    }


    public boolean isAmZug() {
        return amZug;
    }
}
