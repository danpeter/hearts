package com.danpeter.hearts.transfer;

//TODO: Extend with game and player information
public class GameErrorDto {
    public final String type = "GAME_ERROR";
    public final String message;

    public GameErrorDto(String message) {
        this.message = message;
    }
}
