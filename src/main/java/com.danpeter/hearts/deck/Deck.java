package com.danpeter.hearts.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    public List<Card> dealCards(int count) {
        if (deck.size() < count) {
            throw new IllegalStateException("Not enough cards in the deck");
        }
        List<Card> temp = deck.subList(0, count);
        List<Card> dealtCards = new ArrayList<>(temp);
        temp.clear();
        return dealtCards;
    }
}