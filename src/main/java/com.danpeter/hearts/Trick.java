package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Trick {
    private final LinkedList<PlayedCard> playedCards = new LinkedList<>();

    public void playCard(Card card, Player player) {
        playedCards.addLast(new PlayedCard(card, player));
    }

    public boolean noCardsPlayed() {
        return playedCards.isEmpty();
    }

    public Card firstPlayedCard() {
        return playedCards.getFirst().card;
    }

    public boolean isFinished() {
        return playedCards.size() == 4;
    }

    public Player resolve() {
        PlayedCard highestPlayedCard = playedCards.stream().reduce(playedCards.getFirst(), (highest, pc) -> highest.card.isLoweAndSameSuitThan(pc.card) ? pc : highest);

        Player loosingPlayer = highestPlayedCard.player;
        loosingPlayer.lostTrick(playedCards.stream().map(pc -> pc.card).collect(Collectors.toList()));
        return loosingPlayer;
    }

    private class PlayedCard {
        public final Card card;
        public final Player player;

        private PlayedCard(Card card, Player player) {
            this.card = card;
            this.player = player;
        }
    }
}


