package com.danpeter.hearts.transfer;

import com.danpeter.hearts.PlayerHand;
import com.danpeter.hearts.deck.Card;

import java.util.List;
import java.util.UUID;

public class RoundDto {
    private final String type = "NEW_ROUND";
    private final List<PlayerDto> players;
    private PlayerDto startingPlayer;
    private final List<Card> hand;
    private UUID playerId;

    public RoundDto(List<PlayerDto> players, PlayerDto startingPlayer, PlayerHand hand, UUID playerId) {
        this.players = players;
        this.startingPlayer = startingPlayer;
        this.hand = hand.getCards();
        this.playerId = playerId;
    }
}
