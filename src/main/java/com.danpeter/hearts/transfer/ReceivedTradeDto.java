package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

import java.util.List;

public class ReceivedTradeDto {
    private final String type;
    private final List<Card> cards;
    private final String name;

    public ReceivedTradeDto(String type, List<Card> cards, String name) {
        this.type = type;
        this.cards = cards;
        this.name = name;
    }
}
