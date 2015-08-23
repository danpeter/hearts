package com.danpeter.hearts.transfer;

import com.danpeter.hearts.deck.Card;

import java.util.List;

public class ReceivedTradeDto {
    public final String type = "RECEIVED_TRADE";
    public final List<Card> cards;
    public final String name;

    public ReceivedTradeDto(List<Card> cards, String name) {
        this.cards = cards;
        this.name = name;
    }
}
