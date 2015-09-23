package com.danpeter.hearts.statistics;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsManager {

    private static final StatisticsManager statisticsManagerSingleton = new StatisticsManager();
    private static final String STATISTICS_PRIMARY_KEY = "hearts";

    private AtomicInteger startedGamesCount;
    private AtomicInteger finishedGamesCount;
    private AtomicInteger abortedGamesCount;

    private final DynamoDBMapper dynamoDBMapper;
    private final ScheduledExecutorService scheduler;

    public StatisticsManager() {
        AmazonDynamoDB client = new AmazonDynamoDBClient(new InstanceProfileCredentialsProvider());
        dynamoDBMapper = new DynamoDBMapper(client);
        loadInitialStatistics();

        //Run the persist method every minute
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::persist, 1, 1, TimeUnit.MINUTES);
    }

    private void loadInitialStatistics() {
        try {
            Statistics statistics = dynamoDBMapper.load(Statistics.class, STATISTICS_PRIMARY_KEY);
            startedGamesCount = new AtomicInteger(statistics.getStartedGames());
            finishedGamesCount = new AtomicInteger(statistics.getFinishedGames());
            abortedGamesCount = new AtomicInteger(statistics.getAbortedGames());
        } catch (ResourceNotFoundException e) {
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

    private void persist() {
        dynamoDBMapper.save(new Statistics(STATISTICS_PRIMARY_KEY, startedGamesCount.get(), finishedGamesCount.get(), abortedGamesCount.get()));
    }

    public Statistics getStatistics() {
        return dynamoDBMapper.load(Statistics.class, STATISTICS_PRIMARY_KEY);
    }
}
