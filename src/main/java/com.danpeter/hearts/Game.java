package com.danpeter.hearts;

import java.util.LinkedList;
import java.util.function.BinaryOperator;


public class Game {

    public static final int MAX_SCORE = 100;

    private Hand hand;
    private final LinkedList<Player> players;
    private TradeCards tradeCards = TradeCards.LEFT;

    public Game(LinkedList<Player> players) {
        this.players = players;
        this.hand = new Hand(this, players);
    }

    public void play() {
        hand.startTrading(tradeCards);
    }

    public void handIsFinished() {
        if (gameIsOver()) {
            Player winner = players.stream().reduce(lowestScoringPlayer).get();
            players.stream().forEach(player -> player.gameOver(winner, players));
        } else {
            startNewHand();
        }
    }

    private boolean gameIsOver() {
        return players.stream().filter(player -> player.getScore() >= MAX_SCORE)
                .findAny().isPresent();
    }

    private final BinaryOperator<Player> lowestScoringPlayer = (lowestScorer, player) -> lowestScorer.getScore() < player.getScore() ? lowestScorer : player;

    private void startNewHand() {
        tradeCards = tradeCards.next();
        hand = new Hand(this, players);
        if (tradeCards != TradeCards.NONE) {
            hand.startTrading(tradeCards);
        } else {
            hand.startHand();
        }
    }
}
