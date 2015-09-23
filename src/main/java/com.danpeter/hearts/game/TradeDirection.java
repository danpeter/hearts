package com.danpeter.hearts.game;

import java.util.List;

public enum TradeDirection {
    LEFT, RIGHT, ACROSS, NONE;

    public TradeDirection next() {
        return values()[(this.ordinal() + 1) % 4];
    }

    public Player getTradingToPlayer(Player fromPlayer, List<Player> players) {
        int direction;
        switch (this) {
            case LEFT:
                direction = 3;
                break;
            case RIGHT:
                direction = 1;
                break;
            case ACROSS:
                direction = 2;
                break;
            default:
                throw new IllegalStateException("Trading with illegal state");
        }
        return players.get((players.indexOf(fromPlayer) + direction) % 4);
    }
}
