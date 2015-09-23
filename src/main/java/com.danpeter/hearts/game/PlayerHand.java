package com.danpeter.hearts.game;

import com.danpeter.hearts.deck.Card;

import java.util.ArrayList;
import java.util.List;

public class PlayerHand {

    private final List<Card> cards;

    public PlayerHand(List<Card> cards) {
        if (cards.size() != 13) {
            throw new IllegalArgumentException("Hand must contain 13 cards.");
        }
        this.cards = cards;
    }

    public boolean handContainsSuite(Card.Suit suit) {
        return cards.stream().filter(card -> card.getSuit() == suit).findAny().isPresent();
    }

    public boolean onlyHeartsLeft() {
        return !cards.stream().filter(card -> card.getSuit() != Card.Suit.HEARTS).findAny().isPresent();
    }

    public void validateHasCard(Card card) {
        if (!cards.contains(card)) {
            throw new GameRuleException("Hand does not contain this card!");
        }
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public void removeAll(List<Card> cards) {
        this.cards.removeAll(cards);
    }

    public void addAll(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }
}
