package com.danpeter.hearts.transfer;

import com.danpeter.hearts.PlayerHand;

import java.util.List;

public class TradingDto {
    public final String type;
    public final String direction;
    public final List<PlayerDto> players;
    private PlayerHand hand;

    public TradingDto(String type, String direction, List<PlayerDto> players, PlayerHand hand) {
        this.type = type;
        this.direction = direction;
        this.players = players;
        this.hand = hand;
    }
}
