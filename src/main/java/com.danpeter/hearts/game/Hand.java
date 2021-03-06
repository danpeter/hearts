package com.danpeter.hearts.game;

import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.deck.Deck;
import com.danpeter.hearts.statistics.StatisticsManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Hand {

    public static final int MAX_SCORE_PER_HAND = 26;
    public static final int CARDS_IN_HAND = 13;

    private final Game game;
    private final LinkedList<Player> players;
    private Player currentPlayer;
    private final Deck deck = new Deck();
    private Trick trick = new Trick();
    private int tricks = 1;
    private boolean heartsBroken = false;
    private Optional<Trade> trade = Optional.empty();
    private final StatisticsManager statisticsManager = StatisticsManager.getInstance();

    public Hand(Game game, LinkedList<Player> players) {
        this.game = game;
        this.players = players;

        deck.shuffle();
        //deal cards
        players.stream().forEach(player -> {
//            //TODO: Deal in round robin order ...
            player.setHand(new PlayerHand(deck.dealCards(CARDS_IN_HAND)));
        });
    }

    public void startTrading(TradeDirection tradeDirection) {
        trade = Optional.of(new Trade(tradeDirection, players));
        players.stream().forEach(player -> player.startTrading(this, players, tradeDirection));
    }

    public void startHand() {
        this.currentPlayer = players.stream().filter(p -> p.getHand().contains(Card.TWO_OF_CLUBS))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No player has two of clubs"));

        players.stream().forEach(player -> player.playHand(this,
                players,
                currentPlayer));
    }

    public void tradesCard(Player fromPlayer, List<Card> cards) {
        Trade trade = this.trade.orElseThrow(() -> new IllegalStateException("Not in trading phase!"));
        trade.trade(fromPlayer, cards);

        if (trade.isTradingFinished()) {
            players.stream().forEach(player -> player.receiveCards(trade.getTrade(player)));
            startHand();
        }
    }

    public void playsCard(Card card, Player playerWhoPlayed) {
        if (currentPlayer == null) {
            throw new IllegalStateException("Current player not set!");
        }

        validatePlayerIsTheCurrentPlayer(playerWhoPlayed);
        validateNotAScoringCardInFirstTrick(card);
        validateIsFollowingSuit(playerWhoPlayed, card);
        validateIsAllowedToPlayHearts(card, playerWhoPlayed);

        if (card.getSuit() == Card.Suit.HEARTS) {
            heartsBroken = true;
        }

        trick.playCard(card, playerWhoPlayed);
        if (trick.isFinished()) {
            currentPlayer = trick.resolve();
            if (lastTrickInHand()) {
                handleScoring();
                players.stream().forEach(player -> player.notifyPlayedCard(card, playerWhoPlayed, currentPlayer));
                game.handIsFinished();
                return;
            } else {
                trick = new Trick();
                tricks++;
            }
        } else {
            currentPlayer = players.get((players.indexOf(playerWhoPlayed) + 1) % 4);
        }
        players.stream().forEach(player -> player.notifyPlayedCard(card, playerWhoPlayed, currentPlayer));
    }

    private void handleScoring() {
        Optional<Player> playerShotTheMoon = players.stream().filter(Player::hasShotTheMoon)
                .findAny();
        if (playerShotTheMoon.isPresent()) {
            players.stream().filter(p -> !p.equals(playerShotTheMoon.get()))
                    .forEach(Player::otherPlayerShotTheMoon);
        } else {
            players.stream().forEach(Player::updateScore);
        }

    }

    private void validatePlayerIsTheCurrentPlayer(Player playerWhoPlayed) {
        if (!currentPlayer.equals(playerWhoPlayed)) {
            throw new GameRuleException("The player is not allowed to play a card at this time");
        }
    }

    private void validateIsAllowedToPlayHearts(Card card, Player player) {
        if (trick.noCardsPlayed() && card.getSuit() == Card.Suit.HEARTS) {
            if (!heartsBroken && !player.getHand().onlyHeartsLeft()) {
                throw new GameRuleException("Cannot play hearts at this time.");
            }
        }
    }

    private void validateIsFollowingSuit(Player playerWhoPlayed, Card card) {
        if (!trick.noCardsPlayed()
                && !trick.firstPlayedCard().hasSameSuite(card)
                && playerWhoPlayed.getHand().handContainsSuite(trick.firstPlayedCard().getSuit())) {
            throw new GameRuleException("Player is not following suite!");
        }
    }

    private void validateNotAScoringCardInFirstTrick(Card card) {
        if (card.isScoringCard() && tricks == 1) {
            throw new GameRuleException("Scoring cards not allowed during the first trick!");
        }
    }

    private boolean lastTrickInHand() {
        return tricks == CARDS_IN_HAND;
    }

    public void quit(Player player) {
        statisticsManager.abortedGame();
        players.stream().filter(p -> !p.equals(player)).forEach(p -> p.playerQuit(player));
    }
}
