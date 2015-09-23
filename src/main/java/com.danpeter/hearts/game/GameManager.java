package com.danpeter.hearts.game;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.LinkedList;

public class GameManager {

    private static final Log log = LogFactory.getLog(HeartsEndpoint.class);

    private static final GameManager gameManagerSingleton = new GameManager();
    public static final int NUMBER_OF_PLAYERS_IN_HEARTS = 4;

    private final LinkedList<Player> playersWaitingForGame = new LinkedList<>();

    public static GameManager getInstance() {
        return gameManagerSingleton;
    }

    public synchronized void joinGame(Player player) {
        playersWaitingForGame.add(player);
        startAGameIfEnoughPlayers();
    }

    private void startAGameIfEnoughPlayers() {
        if (playersWaitingForGame.size() >= NUMBER_OF_PLAYERS_IN_HEARTS) {
            final LinkedList<Player> firstFourPlayers = new LinkedList<>();
            firstFourPlayers.add(playersWaitingForGame.pop());
            firstFourPlayers.add(playersWaitingForGame.pop());
            firstFourPlayers.add(playersWaitingForGame.pop());
            firstFourPlayers.add(playersWaitingForGame.pop());
            Game game = new Game(firstFourPlayers);
            log.info("Starting a game!");
            game.play();
        } else {
            playersWaitingForGame.stream().forEach(p -> p.queueStatus(playersWaitingForGame));
            log.info("There are now " + playersWaitingForGame.size() + " players in the queue.");
        }
    }

    public synchronized void leaveQueue(Player player) {
        playersWaitingForGame.remove(player);
        playersWaitingForGame.stream().forEach(p -> p.queueStatus(playersWaitingForGame));
        log.info("There are now " + playersWaitingForGame.size() + " players in the queue.");
    }
}
