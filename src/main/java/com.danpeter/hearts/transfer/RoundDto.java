package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

import java.util.List;
import java.util.UUID;

public class RoundDto {
    private final String type;
    private final List<PlayerDto> players;
    private PlayerDto startingPlayer;
    private final List<Card> hand;
    private UUID playerId;

    public RoundDto(String type, List<PlayerDto> players, PlayerDto startingPlayer, List<Card> hand, UUID playerId) {
        this.type = type;
        this.players = players;
        this.startingPlayer = startingPlayer;
        this.hand = hand;
        this.playerId = playerId;
    }
}
