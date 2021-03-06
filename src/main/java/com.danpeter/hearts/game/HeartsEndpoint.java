package com.danpeter.hearts.game;

import com.danpeter.hearts.deck.Card;
import com.danpeter.hearts.transfer.GameErrorDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ServerEndpoint(value = "/websocket/chat")
public class HeartsEndpoint {

    private static final Log log = LogFactory.getLog(HeartsEndpoint.class);

    private Session session;
    private final GameManager gameManager = GameManager.getInstance();
    private Optional<Player> player = Optional.empty();
    private final Gson gson = new Gson();

    @OnOpen
    public void start(Session session) {
        this.session = session;
    }

    @OnClose
    public void end() {
        player.ifPresent(player -> {
            gameManager.leaveQueue(player);
            player.quitGame();
        });
    }

    @OnMessage
    public void incoming(String message) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(message).getAsJsonObject();
            String type = obj.get("type").getAsString();
            switch (type) {
                case "PLAY_CARD":
                    Card card = gson.fromJson(obj.get("card").getAsJsonObject(), Card.class);
                    player.get().playCard(card);
                    break;
                case "JOIN_GAME":
                    if (!player.isPresent()) {
                        player = Optional.of(new Player(obj.get("name").getAsString(), this));
                    }
                    gameManager.joinGame(player.get());
                    break;
                case "TRADE_CARDS":
                    List<Card> tradedCards = gson.fromJson(obj.get("cards"), new TypeToken<ArrayList<Card>>() {
                    }.getType());
                    player.get().tradingCards(tradedCards);
                    break;
            }
        } catch (GameRuleException e) {
            log.error("Rule exception, returning error to the client", e);
            send(new GameErrorDto(e.getMessage()));
        }
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error("Chat Error: " + t.toString(), t);
    }

    //TODO: Implement some kind of message interface to avoid using object here
    public void send(Object dto) {
        try {
            session.getBasicRemote().sendText(gson.toJson(dto));
        } catch (IOException e) {
            log.debug("Chat Error: Failed to send message to client", e);
            try {
                this.session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    public void setPlayer(Player player) {
        this.player = Optional.ofNullable(player);
    }
}
