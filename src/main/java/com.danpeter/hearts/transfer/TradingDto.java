package com.danpeter.hearts.transfer;

import com.danpeter.hearts.PlayerHand;
import com.danpeter.hearts.deck.Card;

import java.util.List;

public class TradingDto {
    public final String type = "TRADING";
    public final String direction;
    public final List<PlayerDto> players;
    private List<Card> hand;

    public TradingDto(String direction, List<PlayerDto> players, PlayerHand hand) {
        this.direction = direction;
        this.players = players;
        this.hand = hand.getCards();
    }
}
