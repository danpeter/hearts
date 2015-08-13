package com.danpeter.hearts.transfer;

import com.danpeter.hearts.Player;

import java.util.UUID;

public class PlayerDto {

    private final String name;
    private final UUID id;
    private final int score;

    public PlayerDto(String name, UUID id, int score) {
        this.name = name;
        this.id = id;
        this.score = score;
    }

    public static PlayerDto from(Player player) {
        return new PlayerDto(player.getName(), player.getId(), player.getScore());
    }
}
