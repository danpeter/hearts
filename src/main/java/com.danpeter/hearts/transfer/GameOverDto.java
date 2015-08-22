package com.danpeter.hearts.transfer;

import java.util.List;

public class GameOverDto {
    private final PlayerDto winner;
    private final List<PlayerDto> players;

    public GameOverDto(PlayerDto winner, List<PlayerDto> players) {

        this.winner = winner;
        this.players = players;
    }
}
