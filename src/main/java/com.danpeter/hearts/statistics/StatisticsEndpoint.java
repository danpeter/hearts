package com.danpeter.hearts.statistics;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/statistics")
public class StatisticsEndpoint extends HttpServlet {

    private final StatisticsManager statisticsManager = StatisticsManager.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getOutputStream().print(gson.toJson(statisticsManager.getStatistics()));
        resp.getOutputStream().flush();
    }
}
