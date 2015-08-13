package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

public class PlayedCardDto {
    private final String type;
    private final Card card;
    private final PlayerDto playerWhoPlayed;
    private final PlayerDto currentPlayer;

    public PlayedCardDto(String type, Card card, PlayerDto playerWhoPlayed, PlayerDto currentPlayer) {
        this.type = type;
        this.card = card;
        this.playerWhoPlayed = playerWhoPlayed;
        this.currentPlayer = currentPlayer;
    }
}
