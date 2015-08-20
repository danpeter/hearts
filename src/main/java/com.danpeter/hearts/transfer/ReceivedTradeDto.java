package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

import java.util.List;

public class ReceivedTradeDto {
    private final String type = "RECEIVED_TRADE";
    private final List<Card> cards;
    private final String name;

    public ReceivedTradeDto(List<Card> cards, String name) {
        this.cards = cards;
        this.name = name;
    }
}
