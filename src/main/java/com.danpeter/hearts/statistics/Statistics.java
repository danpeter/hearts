package com.danpeter.hearts.statistics;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "statistics")
public class Statistics {

    private String id;
    private int startedGames;
    private int finishedGames;
    private int abortedGames;

    public Statistics(String id, int startedGames, int finishedGames, int abortedGames) {
        this.id = id;
        this.startedGames = startedGames;
        this.finishedGames = finishedGames;
        this.abortedGames = abortedGames;
    }

    public Statistics() {
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "startedGames")
    public int getStartedGames() {
        return startedGames;
    }

    @DynamoDBAttribute(attributeName = "finishedGames")
    public int getFinishedGames() {
        return finishedGames;
    }

    @DynamoDBAttribute(attributeName = "abortedGames")
    public int getAbortedGames() {
        return abortedGames;
    }

    public int ongoingGamesCount() {
        return startedGames - finishedGames - abortedGames;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStartedGames(int startedGames) {
        this.startedGames = startedGames;
    }

    public void setFinishedGames(int finishedGames) {
        this.finishedGames = finishedGames;
    }

    public void setAbortedGames(int abortedGames) {
        this.abortedGames = abortedGames;
    }
}
