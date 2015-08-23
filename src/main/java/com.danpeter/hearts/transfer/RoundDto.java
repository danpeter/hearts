package com.danpeter.hearts.transfer;

import com.danpeter.hearts.PlayerHand;
import com.danpeter.hearts.deck.Card;

import java.util.List;
import java.util.UUID;

public class RoundDto {
    public final String type = "NEW_ROUND";
    public final List<PlayerDto> players;
    public final PlayerDto startingPlayer;
    public final List<Card> hand;
    public final UUID playerId;

    public RoundDto(List<PlayerDto> players, PlayerDto startingPlayer, PlayerHand hand, UUID playerId) {
        this.players = players;
        this.startingPlayer = startingPlayer;
        this.hand = hand.getCards();
        this.playerId = playerId;
    }
}
