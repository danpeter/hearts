package com.danpeter.hearts;

import java.util.List;

public class QueueDto {
    public final String type = "QUEUE_STATUS";
    public final List<String> playerNames;

    public QueueDto(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
