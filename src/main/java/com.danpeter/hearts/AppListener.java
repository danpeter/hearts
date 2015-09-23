package com.danpeter.hearts;

import com.danpeter.hearts.statistics.StatisticsManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class AppListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        StatisticsManager statisticsManager = StatisticsManager.getInstance();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        //Run the persist method every minute
        scheduler.scheduleAtFixedRate(statisticsManager::persist, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        scheduler.shutdown();
    }
}
