package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.ArrayList;
import java.util.List;

public class LostTricks {
    public static final int POINTS_FOR_HEARTS = 1;
    public static final int POINTS_FOR_QUEEN_OF_SPADES = 13;
    private final List<Card> cards = new ArrayList<>();

    public int currentPointsInLostTrick() {
        return cards.stream().filter(Card::isScoringCard)
                .map(card -> card.getSuit() == Card.Suit.HEARTS ? POINTS_FOR_HEARTS : POINTS_FOR_QUEEN_OF_SPADES)
                .reduce((total, points) -> total += points)
                .orElse(0);
    }

    public void addAll(List<Card> cards) {
        this.cards.addAll(cards);
    }
}
