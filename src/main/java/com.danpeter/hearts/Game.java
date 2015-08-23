package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;


public class Game {

    public static final int MAX_SCORE = 100;

    private Hand hand;
    private final LinkedList<Player> players;
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
            if (gameIsOver()) {
                Player winner = players.stream().reduce(lowestScoringPlayer).get();
                players.stream().forEach(player -> player.gameOver(winner, players));
            } else {
                startNewHand();
            }
        }
    }

    private boolean gameIsOver() {
        return players.stream().filter(player -> player.getScore() >= MAX_SCORE)
                .findAny().isPresent();
    }

    private final BinaryOperator<Player> lowestScoringPlayer = (lowestScorer, player) -> lowestScorer.getScore() < player.getScore() ? lowestScorer : player;

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
