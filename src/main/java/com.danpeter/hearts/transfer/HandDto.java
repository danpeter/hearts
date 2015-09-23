package com.danpeter.hearts.transfer;

import com.danpeter.hearts.game.PlayerHand;
import com.danpeter.hearts.deck.Card;

import java.util.List;
import java.util.UUID;

public class HandDto {
    public final String type = "NEW_HAND";
    public final List<PlayerDto> players;
    public final PlayerDto startingPlayer;
    public final List<Card> hand;
    public final UUID playerId;

    public HandDto(List<PlayerDto> players, PlayerDto startingPlayer, PlayerHand hand, UUID playerId) {
        this.players = players;
        this.startingPlayer = startingPlayer;
        this.hand = hand.getCards();
        this.playerId = playerId;
    }
}
