package com.danpeter.hearts.transfer;

public class PlayerQuitDto {

    public final String type = "PLAYER_QUIT";
    public final String name;

    public PlayerQuitDto(String name) {
        this.name = name;
    }
}
