package com.danpeter.hearts.statistics;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsManager {

    private static final StatisticsManager statisticsManagerSingleton = new StatisticsManager();
    private static final String STATISTICS_PRIMARY_KEY = "hearts";

    private AtomicInteger startedGamesCount;
    private AtomicInteger finishedGamesCount;
    private AtomicInteger abortedGamesCount;

    private final DynamoDBMapper dynamoDBMapper;

    public StatisticsManager() {
        AmazonDynamoDB client = new AmazonDynamoDBClient(
                new AWSCredentialsProviderChain(
                        // First we'll check for EC2 instance profile credentials.
                        new InstanceProfileCredentialsProvider(),
                        // If we're not on an EC2 instance, fall back to checking for
                        // credentials in the local credentials profile file.
                        new ProfileCredentialsProvider()));
        client.setRegion(Region.getRegion(Regions.EU_WEST_1)); //Unfortunate that this needs to be here, could it not read it from ElasticBeanstalk? ;(
        dynamoDBMapper = new DynamoDBMapper(client);
        loadInitialStatistics();
    }

    private void loadInitialStatistics() {
        Statistics statistics = dynamoDBMapper.load(Statistics.class, STATISTICS_PRIMARY_KEY);
        if (statistics != null) {
            startedGamesCount = new AtomicInteger(statistics.getStartedGames());
            finishedGamesCount = new AtomicInteger(statistics.getFinishedGames());
            abortedGamesCount = new AtomicInteger(statistics.getAbortedGames());
        } else {
            //There are no statistics to load
            startedGamesCount = new AtomicInteger();
            finishedGamesCount = new AtomicInteger();
            abortedGamesCount = new AtomicInteger();
        }
    }

    public static StatisticsManager getInstance() {
        return statisticsManagerSingleton;
    }

    public void startedGame() {
        startedGamesCount.incrementAndGet();
    }

    public void finishedGame() {
        finishedGamesCount.incrementAndGet();
    }

    public void abortedGame() {
        abortedGamesCount.incrementAndGet();
    }

    public void persist() {
        dynamoDBMapper.save(new Statistics(STATISTICS_PRIMARY_KEY,
                startedGamesCount.get(),
                finishedGamesCount.get(),
                abortedGamesCount.get()));
    }

    public Statistics getStatistics() {
        return dynamoDBMapper.load(Statistics.class, STATISTICS_PRIMARY_KEY);
    }
}
