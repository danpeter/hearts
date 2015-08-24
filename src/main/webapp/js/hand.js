'use strict';

function Hand(cards) {

    //Sort by suit first; clubs < spades < diamonds < hearts, then by points
    cards.sort(function (a, b) {
        if(a.suit === b.suit) {
            return a.points - b.points;
        } else {
            return suits[a.suit.toLowerCase()] - suits[b.suit.toLowerCase()];
        }
    });

    //Convert JSON to card objects with positions
    this.cards = cards.map(function (card, index) {
        return new Card(card.value, card.suit, card.points, 300 + index * 25, 545);
    });
}

Hand.prototype.containsSuit = function (suit) {
    var cardsOfSuit = this.cards.filter(function (card) {
        return card.suit === suit;
    });
    return cardsOfSuit.length > 0;
};

Hand.prototype.containsOnlyHearts = function() {
    var nonHeartsCards = this.cards.filter(function (card) {
        return card.suit !== 'HEARTS';
    });

    return nonHeartsCards.length == 0;
};

Hand.prototype.remove = function (cardToRemove) {
    this.cards = this.cards.filter(function (card) {
        return !(card.value == cardToRemove.value && card.suit == cardToRemove.suit);
    });
};

var suits = {
    clubs: 1,
    spades: 2,
    diamonds: 3,
    hearts: 4
};