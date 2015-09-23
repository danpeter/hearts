package com.danpeter.hearts.game;

import com.danpeter.hearts.statistics.StatisticsManager;

import java.util.LinkedList;
import java.util.function.BinaryOperator;


public class Game {

    public static final int MAX_SCORE = 50;

    private Hand hand;
    private final LinkedList<Player> players;
    private TradeDirection tradeDirection = TradeDirection.LEFT;
    StatisticsManager statisticsManager = StatisticsManager.getInstance();

    public Game(LinkedList<Player> players) {
        this.players = players;
        this.hand = new Hand(this, players);
    }

    public void play() {
        statisticsManager.startedGame();
        hand.startTrading(tradeDirection);
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
        statisticsManager.finishedGame();
        return players.stream().filter(player -> player.getScore() >= MAX_SCORE)
                .findAny().isPresent();
    }

    private final BinaryOperator<Player> lowestScoringPlayer = (lowestScorer, player) -> lowestScorer.getScore() < player.getScore() ? lowestScorer : player;

    private void startNewHand() {
        tradeDirection = tradeDirection.next();
        hand = new Hand(this, players);
        if (tradeDirection != TradeDirection.NONE) {
            hand.startTrading(tradeDirection);
        } else {
            hand.startHand();
        }
    }
}
