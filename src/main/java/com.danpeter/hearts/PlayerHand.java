package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.ArrayList;

public class PlayerHand extends ArrayList<Card> {

    public boolean handContainsSuite(Card.Suit suit) {
        return this.stream().filter(card -> card.getSuit() == suit).findAny().isPresent();
    }

    public boolean onlyHeartsLeft() {
        return !this.stream().filter(card -> card.getSuit() != Card.Suit.HEARTS).findAny().isPresent();
    }

    public void validateHasCard(Card card) {
        if (!this.contains(card)) {
            throw new IllegalStateException("This player does not have this card!");
        }
    }
}
