package com.danpeter.hearts.deck;


public class Card {

    public static final Card TWO_OF_CLUBS = new Card(Suit.CLUBS, Value.TWO);

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
        this.points = value.getPoints();
    }

    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Value {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), ACE(14);

        private final int points;

        Value(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
    }

    private final Suit suit;

    private final Value value;

    private final int points;

    public Suit getSuit() {
        return suit;
    }

    public boolean isLoweAndSameSuitThan(Card card) {
        return this.suit.equals(card.suit) && this.points < card.points;
    }

    public String toString() {
        return value + " " + suit;
    }

    public boolean hasSameSuite(Card otherCard) {
        return this.getSuit() == otherCard.getSuit();
    }

    /**
     * Is this card a scoring card?
     * Either any hearts or Queen of Spades
     *
     * @return
     */
    public boolean isScoringCard() {
        return suit == Suit.HEARTS || (suit == Suit.SPADES && value == Value.QUEEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (points != card.points) return false;
        if (suit != card.suit) return false;
        return value == card.value;

    }

    @Override
    public int hashCode() {
        int result = suit != null ? suit.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + points;
        return result;
    }
}