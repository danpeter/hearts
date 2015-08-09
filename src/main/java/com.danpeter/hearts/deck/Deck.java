package com.danpeter.hearts.deck;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {

    private LinkedList<Card> deck;

    public Deck() {
        deck = new LinkedList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                deck.add(new Card(suit, value));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        if (deck.isEmpty())
            throw new IllegalStateException("No cards are left in the deck.");
        return deck.pop();
    }
}