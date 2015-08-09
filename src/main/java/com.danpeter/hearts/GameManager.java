package com.danpeter.hearts;

import com.danpeter.hearts.deck.Deck;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.LinkedList;

//TODO: This class needs synchronization
public class GameManager {

    private static final Log log = LogFactory.getLog(HeartsEndpoint.class);

    private static final GameManager GLOBAL_GAME_MANAGER = new GameManager();
    public static final int NUMBER_OF_PLAYERS_IN_HEARTS = 4;

    private final LinkedList<Player> playersWaitingForGame = new LinkedList<>();

    public static GameManager get() {
        return GLOBAL_GAME_MANAGER;
    }

    public void joinGame(HeartsEndpoint endpoint, String name) {
        Player player = new Player(name , endpoint);
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
            firstFourPlayers.stream().forEach(p -> p.participateInGame(game));
            log.info("Starting a game!");
            game.play();
        } else {
            log.info("There are now " + playersWaitingForGame.size() + " players in the queue.");
        }
    }

    public void leaveQueue(Player player) {
        playersWaitingForGame.remove(player);
        log.info("There are now " + playersWaitingForGame.size() + " players in the queue.");
    }
}
