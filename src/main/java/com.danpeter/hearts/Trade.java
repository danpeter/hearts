package com.danpeter.hearts;

import com.danpeter.hearts.deck.Card;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Trade {
    public static final int NUMBER_OF_PLAYERS = 4;
    private final TradeDirection tradeDirection;
    private final LinkedList<Player> players;
    private final Map<Player, List<Card>> trades;

    public Trade(TradeDirection tradeDirection, LinkedList<Player> players) {
        this.tradeDirection = tradeDirection;
        this.players = players;
        this.trades = new HashMap<>();
    }

    public void trade(Player fromPlayer, List<Card> cards) {
        Player toPlayer = tradeDirection.getTradingToPlayer(fromPlayer, players);
        trades.put(toPlayer, cards);
    }

    public List<Card> getTrade(Player player) {
        return trades.get(player);
    }

    public boolean isTradingFinished() {
        return trades.size() == NUMBER_OF_PLAYERS;
    }
}
