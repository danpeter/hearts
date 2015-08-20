package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

public class PlayedCardDto {
    private final String type = "PLAYED_CARD";
    private final Card card;
    private final PlayerDto playerWhoPlayed;
    private final PlayerDto currentPlayer;

    public PlayedCardDto(Card card, PlayerDto playerWhoPlayed, PlayerDto currentPlayer) {
        this.card = card;
        this.playerWhoPlayed = playerWhoPlayed;
        this.currentPlayer = currentPlayer;
    }
}
