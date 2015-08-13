package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.deck.Deck;

import java.util.LinkedList;

public class Hand {
    private final Deck deck = new Deck();
    private Trick trick = new Trick();
    private Player currentPlayer;
    public static final int TRICKS_IN_A_HAND = 13;
    private int tricks = 1;
    private final LinkedList<Player> players;
    private boolean heartsBroken = false;

    public Hand(LinkedList<Player> players) {
        this.players = players;

        deck.shuffle();
        //deal cards
        players.stream().forEach(player -> {
            PlayerHand hand = new PlayerHand();
            //TODO: Deal in round robin order ...
            for (int i = 1; i <= 13; i++) {
                hand.add(deck.dealCard());
            }
            player.setHand(hand);
        });
    }

    public void startTrading(Game.TradeCards tradeCards) {
            players.stream().forEach(player -> player.tradingStarted(players, tradeCards));
    }

    public void startHand() {
        this.currentPlayer = players.stream().filter(p -> p.getHand().contains(Card.TWO_OF_CLUBS))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No player has two of clubs"));

        players.stream().forEach(player -> player.newHandIsStarting(players,
                currentPlayer));
    }

    /**
     * @param card
     * @param playerWhoPlayed
     * @return boolean representing if the whole hand is finished
     */
    public boolean playsCard(Card card, Player playerWhoPlayed) {
        if(currentPlayer == null) {
            throw new IllegalStateException("Current player not set!");
        }

        if (!currentPlayer.equals(playerWhoPlayed)) {
            throw new IllegalStateException("The player is not allowed to play a card at this time");
        }

        if(isHeartsAndFirstCardPlayedInTrick(card)) {
            if(!heartsBroken && !currentPlayer.getHand().onlyHeartsLeft()) {
                throw new IllegalStateException("Cannot play hearts at this time.");
            }
        }

        if (!isFollowingSuitOrDiscarding(playerWhoPlayed, card)) {
            throw new IllegalStateException("Player is not following suite!");
        }

        if(card.getSuit() == Card.Suit.HEARTS) {
            heartsBroken = true;
        }

        trick.playCard(card, playerWhoPlayed);
        if (trick.isFinished()) {
            currentPlayer = trick.resolve();
            if (lastTrickInHand()) {
                players.stream().forEach(player -> {
                    player.notifyPlayedCard(card, playerWhoPlayed, currentPlayer);
                    player.updateScore();
                });
                return true;
            } else {
                trick = new Trick();
                tricks++;
            }
        } else {
            currentPlayer = players.get((players.indexOf(playerWhoPlayed) + 1) % 4);
        }
        players.stream().forEach(player -> player.notifyPlayedCard(card, playerWhoPlayed, currentPlayer));

        return false;
    }


    private boolean isHeartsAndFirstCardPlayedInTrick(Card card) {
        return trick.noCardsPlayed() && card.getSuit() == Card.Suit.HEARTS;
    }

    private boolean lastTrickInHand() {
        return tricks == TRICKS_IN_A_HAND;
    }

    private boolean isFollowingSuitOrDiscarding(Player player, Card card) {
        return trick.noCardsPlayed()
                || trick.firstPlayedCard().hasSameSuite(card)
                || !player.getHand().handContainsSuite(trick.firstPlayedCard().getSuit());
    }
}
