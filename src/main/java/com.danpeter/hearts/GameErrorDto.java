package com.danpeter.hearts;

//TODO: Extend with game and player information
public class GameErrorDto {
    private final String type = "GAME_ERROR";
    private final String message;

    public GameErrorDto(String message) {
        this.message = message;
    }
}
