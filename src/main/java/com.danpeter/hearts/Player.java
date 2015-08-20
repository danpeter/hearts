package com.danpeter.hearts;


import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.transfer.*;

import java.util.*;
import java.util.stream.Collectors;

public class Player {

    public static final int POINTS_FOR_HEARTS = 1;
    public static final int POINTS_FOR_QUEEN_OF_SPADES = 13;

    private UUID id;
    private final String name;
    private final HeartsEndpoint endpoint;
    private PlayerHand hand;
    private List<Card> lostTricks;
    private Optional<Game> currentGame = Optional.empty();
    private int score = 0;
    private boolean needsToTrade = false;

    public Player(String name, HeartsEndpoint endpoint) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.endpoint = endpoint;
        endpoint.setPlayer(this);
    }

    public void newHandIsStarting(LinkedList<Player> players, Player startingPlayer) {
        lostTricks = new ArrayList<>();

        players = putCurrentPlayerFirst(players);

        RoundDto dto = new RoundDto(players.stream().map(PlayerDto::from).collect(Collectors.toList()),
                PlayerDto.from(startingPlayer),
                hand,
                id);
        endpoint.send(dto);
    }

    public void notifyPlayedCard(Card card, Player playerWhoPlayed, Player currentPlayer) {
        endpoint.send(new PlayedCardDto(card, PlayerDto.from(playerWhoPlayed), PlayerDto.from(currentPlayer)));
    }

    public void tradingStarted(LinkedList<Player> players, Game.TradeCards tradeCards) {
        needsToTrade = true;
        TradingDto dto = new TradingDto(tradeCards.toString(),
                players.stream().map(PlayerDto::from).collect(Collectors.toList()),
                hand);
        endpoint.send(dto);
    }

    public void playCard(Card card) {
        hand.validateHasCard(card);
        hand.remove(card);
        currentGame.orElseThrow(() -> new IllegalStateException("Player is not participating in a game!"))
                .playsCard(card, this);
    }

    public void tradingCards(List<Card> cards) {
        if(!needsToTrade) {
            throw new IllegalStateException("Trading not allowed at this time.");
        }
        cards.stream().forEach(hand::validateHasCard);
        hand.removeAll(cards);
        needsToTrade = false;
        currentGame.orElseThrow(() -> new IllegalStateException("Player is not participating in a game!"))
                .tradesCard(this, cards);
    }

    public void receivingCards(List<Card> cards, Player fromPlayer) {
        hand.addAll(cards);
        endpoint.send(new ReceivedTradeDto(cards, fromPlayer.getName()));
    }

    /**
     * @param players
     * @return the player array with the this player first, while conserving the order
     */
    private LinkedList<Player> putCurrentPlayerFirst(LinkedList<Player> players) {
        LinkedList<Player> sorted = new LinkedList<>();
        int indexOfPlayer = players.indexOf(this);
        for (int i = 0; i < 4; i++) {
            sorted.add(players.get((indexOfPlayer + i) % 4));
        }
        return sorted;
    }

    public void updateScore() {
        score += lostTricks.stream().filter(Card::isScoringCard)
                .map(card -> card.getSuit() == Card.Suit.HEARTS ? POINTS_FOR_HEARTS : POINTS_FOR_QUEEN_OF_SPADES)
                .reduce((total, points) -> total += points)
                .orElse(0);
    }

    public void setHand(PlayerHand hand) {
        this.hand = hand;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public UUID getId() {
        return id;
    }

    public PlayerHand getHand() {
        return hand;
    }

    public void lostTrick(List<Card> cards) {
        lostTricks.addAll(cards);
    }

    public void participateInGame(Game game) {
        this.currentGame = Optional.of(game);
    }

    public boolean needsToTrade() {
        return needsToTrade;
    }
}
