package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.LinkedList;
import java.util.List;


public class Game {

    private final LinkedList<Player> players;
    private Hand hand;
    private TradeCards tradeCards = TradeCards.LEFT;

    public Game(LinkedList<Player> players) {
        this.players = players;
        this.hand = new Hand(players);
    }

    public void play() {
        hand.startTrading(tradeCards);
    }

    public void playsCard(Card card, Player playerWhoPlayed) {
        boolean handIsFinished = hand.playsCard(card, playerWhoPlayed);

        if (handIsFinished) {
            //TODO: Check if any player has > 100 score, then end the game
            startNewHand();
        }
    }

    private void startNewHand() {
        tradeCards = tradeCards.next();
        hand = new Hand(players);
        if (tradeCards != TradeCards.NONE) {
            hand.startTrading(tradeCards);
        } else {
            hand.startHand();
        }
    }

    public void tradesCard(Player fromPlayer, List<Card> cards) {
        getTradingToPlayer(fromPlayer).receivingCards(cards, fromPlayer);

        boolean tradingFinished = !players.stream().filter(Player::needsToTrade).findAny().isPresent();
        if (tradingFinished) {
            hand.startHand();
        }
    }

    public enum TradeCards {
        LEFT, RIGHT, ACROSS, NONE;

        public TradeCards next() {
            return values()[(this.ordinal() + 1) % 4];
        }
    }

    private Player getTradingToPlayer(Player fromPlayer) {
        int direction;
        switch (tradeCards) {
            case LEFT:
                direction = 3;
                break;
            case RIGHT:
                direction = 1;
                break;
            case ACROSS:
                direction = 2;
                break;
            default:
                throw new IllegalStateException("Trading with illegal state");
        }
        return players.get((players.indexOf(fromPlayer) + direction) % 4);
    }
}
