package com.danpeter.hearts.game;


import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.transfer.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Player {

    public static final int MAX_NAME_LENGTH = 20;
    private final UUID id;
    private final String name;
    private final HeartsEndpoint endpoint;
    private PlayerHand playerHand;
    private LostTricks lostTricks;
    private Optional<Hand> currentHand = Optional.empty();
    private int score = 0;
    private boolean trading = false;

    public Player(String name, HeartsEndpoint endpoint) {
        this.id = UUID.randomUUID();
        if (name.isEmpty()) {
            this.name = "John Doe";
        } else if (name.length() > MAX_NAME_LENGTH) {
            this.name = name.substring(0, MAX_NAME_LENGTH);
        } else {
            this.name = name;
        }
        this.endpoint = endpoint;
        endpoint.setPlayer(this);
    }

    public void playHand(Hand hand, LinkedList<Player> players, Player startingPlayer) {
        currentHand = Optional.of(hand);
        lostTricks = new LostTricks();

        players = putCurrentPlayerFirst(players);

        HandDto dto = new HandDto(players.stream().map(PlayerDto::from).collect(Collectors.toList()),
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

        players = putCurrentPlayerFirst(players);

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
        currentHand = Optional.empty();
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
        score += lostTricks.currentPointsInLostTrick();
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

    public void otherPlayerShotTheMoon() {
        score += Hand.MAX_SCORE_PER_HAND;
    }

    public boolean hasShotTheMoon() {
        return lostTricks.currentPointsInLostTrick() == Hand.MAX_SCORE_PER_HAND;
    }

    public void queueStatus(List<Player> playersWaitingForGame) {
        QueueDto queueDto = new QueueDto(playersWaitingForGame.stream().map(Player::getName).collect(Collectors.toList()));
        endpoint.send(queueDto);
    }

    public void quitGame() {
        currentHand.ifPresent(hand -> hand.quit(this));
    }

    public void playerQuit(Player player) {
        PlayerQuitDto dto = new PlayerQuitDto(player.getName());
        endpoint.send(dto);
        //As the endpoint is the object root, from a garbage collection point of view, setting all player references to null
        //should remove all Game/Hand objects
        endpoint.setPlayer(null);
    }
}
