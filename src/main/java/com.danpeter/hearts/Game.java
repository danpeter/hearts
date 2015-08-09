package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.LinkedList;


public class Game {

    private final LinkedList<Player> players;
    private Hand hand;
    /**
     * There are four round in hearts, this tracks which one the game is currently in.
     * The fourth and final round does not have card trading.
     */
    private int round = 1;

    public Game(LinkedList<Player> players) {
        this.players = players;
        this.hand = new Hand(players, round);
    }

    public void play() {
        hand.startHand();
    }

    public void playsCard(Card card, Player playerWhoPlayed) {
        boolean handIsFinished = hand.playsCard(card, playerWhoPlayed);

        if (handIsFinished) {
            //TODO: Check if any player has > 100 score, then end the game
            hand = new Hand(players, ++round);
            hand.startHand();
        }
    }
}
