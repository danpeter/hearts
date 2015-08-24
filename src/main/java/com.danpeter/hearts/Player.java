package com.danpeter.hearts;


import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.transfer.*;

import java.util.*;
import java.util.stream.Collectors;

public class Player {

    public static final int POINTS_FOR_HEARTS = 1;
    public static final int POINTS_FOR_QUEEN_OF_SPADES = 13;

    private final UUID id;
    private final String name;
    private final HeartsEndpoint endpoint;
    private PlayerHand playerHand;
    private List<Card> lostTricks;
    private Optional<Hand> currentHand = Optional.empty();
    private int score = 0;
    private boolean trading = false;

    public Player(String name, HeartsEndpoint endpoint) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.endpoint = endpoint;
        endpoint.setPlayer(this);
    }

    public void playHand(Hand hand, LinkedList<Player> players, Player startingPlayer) {
        currentHand = Optional.of(hand);
        lostTricks = new ArrayList<>();

        players = putCurrentPlayerFirst(players);

        RoundDto dto = new RoundDto(players.stream().map(PlayerDto::from).collect(Collectors.toList()),
                PlayerDto.from(startingPlayer),
                playerHand,
                id);
        endpoint.send(dto);
    }

    public void notifyPlayedCard(Card card, Player playerWhoPlayed, Player currentPlayer) {
        endpoint.send(new PlayedCardDto(card, PlayerDto.from(playerWhoPlayed), PlayerDto.from(currentPlayer)));
    }

    public void startTrading(Hand hand, LinkedList<Player> players, TradeDirection tradeDirection) {
        currentHand = Optional.of(hand);
        trading = true;
        TradingDto dto = new TradingDto(tradeDirection.toString(),
                players.stream().map(PlayerDto::from).collect(Collectors.toList()),
                playerHand);
        endpoint.send(dto);
    }

    public void playCard(Card card) {
        playerHand.validateHasCard(card);
        playerHand.remove(card);
        currentHand.orElseThrow(() -> new GameRuleException("Player is not participating in a hand!"))
                .playsCard(card, this);
    }

    public void tradingCards(List<Card> cards) {
        if (!trading) {
            throw new GameRuleException("Trading not allowed at this time.");
        }
        cards.stream().forEach(playerHand::validateHasCard);
        playerHand.removeAll(cards);
        trading = false;
        currentHand.orElseThrow(() -> new GameRuleException("Player is not participating in a hand!"))
                .tradesCard(this, cards);
    }

    public void gameOver(Player winner, List<Player> players) {
        GameOverDto dto = new GameOverDto(PlayerDto.from(winner),
                players.stream().map(PlayerDto::from).collect(Collectors.toList()));
        endpoint.send(dto);
    }

    public void receiveCards(List<Card> cards) {
        playerHand.addAll(cards);
        endpoint.send(new ReceivedTradeDto(cards));
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
        score += currentPointsInLostTrick();
    }

    public int currentPointsInLostTrick() {
        return lostTricks.stream().filter(Card::isScoringCard)
                .map(card -> card.getSuit() == Card.Suit.HEARTS ? POINTS_FOR_HEARTS : POINTS_FOR_QUEEN_OF_SPADES)
                .reduce((total, points) -> total += points)
                .orElse(0);
    }

    public void setHand(PlayerHand hand) {
        this.playerHand = hand;
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
        return playerHand;
    }

    public void lostTrick(List<Card> cards) {
        lostTricks.addAll(cards);
    }

    public boolean needsToTrade() {
        return trading;
    }

    public void otherPlayerShotTheMoon() {
        score += Hand.MAX_SCORE_PER_HAND;
    }
}
