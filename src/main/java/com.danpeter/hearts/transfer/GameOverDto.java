package com.danpeter.hearts.transfer;

import java.util.List;

public class GameOverDto {
    public final String type = "GAME_OVER";
    public final PlayerDto winner;
    public final List<PlayerDto> players;

    public GameOverDto(PlayerDto winner, List<PlayerDto> players) {
        this.winner = winner;
        this.players = players;
    }
}
