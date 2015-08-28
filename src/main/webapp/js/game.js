"use strict";

var Game = {
    firstHand: true,
    players: [],
    trick: [],
    currentPlayer: null,
    player: {},
    socket: null,
    canvasState: null,
    waitForTrickClear: false,
    heartsBroken: false,
    receivedTrade: []
};

Game.connect = (function (host) {
    if ('WebSocket' in window) {
        Game.socket = new WebSocket(host);
    } else if ('MozWebSocket' in window) {
        Game.socket = new MozWebSocket(host);
    } else {
        console.log('Error: WebSocket is not supported by this browser.');
        return;
    }

    Game.socket.onopen = function () {
        console.log('Info: WebSocket connection opened.');
        console.log('Info: Waiting for 4 players.');
        Game.canvasState.printMessageTop('Waiting for other players.');
        var command = {
            type: 'PLAYER_NAME',
            name: getParameterByName('playerName')
        };
        Game.socket.send(JSON.stringify(command));
    };

    Game.socket.onclose = function () {
        console.log('Info: WebSocket closed.');
    };

    Game.socket.onmessage = function (message) {
        function otherActionHappening() {
            return Game.receivedTrade.length != 0 || Game.waitForTrickClear;
        }

        var command = JSON.parse(message.data);
        switch (command.type) {
            case 'NEW_ROUND':
                var newRound = function () {
                    console.log('Game is starting! Dealing hands.');
                    Game.firstHand = true;
                    Game.player.id = command.playerId;
                    Game.players = command.players;
                    Game.currentPlayer = command.startingPlayer;
                    Game.drawHand(command);
                    Game.heartsBroken = false;
                    Game.canvasState.canvas.addEventListener('mousedown', Game.onMouseClickPlaying, true);
                };
                otherActionHappening ? window.setTimeout(newRound, 2000) : newRound();
                break;
            case 'PLAYED_CARD':
                if (command.card.suit == 'HEARTS') {
                    Game.heartsBroken = true;
                }
                if (Game.trick.length === 3) {
                    //We have completed the first trick
                    Game.firstHand = false;
                }
                Game.playTrick(command);
                break;
            case 'TRADING':
                var trading = function () {
                    Game.currentPlayer = null;
                    Game.players = command.players;
                    Game.drawHand(command);
                    Game.tradingCards = [];
                    Game.canvasState.canvas.addEventListener('mousedown', Game.onMouseClickTrading, true);
                    Game.canvasState.printMessageTop("Trade three cards  " + command.direction.toLowerCase());
                };
                otherActionHappening ? window.setTimeout(trading, 2000) : trading();
                break;
            case 'RECEIVED_TRADE':
                Game.receiveTrade(command);
                Game.canvasState.draw();
                break;
            case 'GAME_OVER':
                window.setTimeout(function () {
                    Game.currentPlayer = null;
                    Game.players = command.players;
                    Game.players[0].hand = new Hand([]);
                    Game.canvasState.draw();
                    Game.canvasState.printMessageTop("Game over! The winner is: " + command.winner.name);
                }, 2000);
                break;
            case 'GAME_ERROR':
                console.log(command.message);
                break;
            default:
                console.log('Unknown message');
        }
    };
});


Game.initialize = function () {
    var canvas = document.getElementById("myCanvas");
    Game.canvasState = new CanvasState(canvas, function () {
        if (window.location.protocol == 'http:') {
            Game.connect('ws://' + window.location.host + '/websocket/chat', canvas);
        } else {
            Game.connect('wss://' + window.location.host + '/websocket/chat', canvas);
        }
    });
};

Game.onMouseClickPlaying = (function (e) {
    function isTwoOfClubsIfFirstCardInFirstRound() {
        return Game.firstHand == false || Game.trick.length !== 0 || (card.suit === 'CLUBS' && card.value === "TWO");
    }

    function isFollowingSuit() {
        return Game.trick.length === 0
            || Game.trick[0].hasSameSuite(card)
            || !Game.players[0].hand.containsSuit(Game.trick[0].suit);
    }

    function isAllowedToPlayHearts() {
        var hand = Game.players[0].hand;
        if (card.suit === 'HEARTS' && !Game.heartsBroken) {
            return hand.containsOnlyHearts() || !hand.containsSuit(Game.trick[0].suit);
        }
        return true;
    }

    function isFirstTrickAndScoringCard() {
        return Game.firstHand && card.isScoringCard();
    }

    //is it your turn?
    if (Game.currentPlayer != null && Game.currentPlayer.id === Game.player.id) {
        var mouse = Game.canvasState.getMouse(e);
        var mx = mouse.x;
        var my = mouse.y;
        var l = Game.players[0].hand.cards.length;
        for (var i = l - 1; i >= 0; i--) {
            var card = Game.players[0].hand.cards[i];
            if (card.contains(mx, my)) {
                if (isTwoOfClubsIfFirstCardInFirstRound() && isFollowingSuit() && isAllowedToPlayHearts() && !isFirstTrickAndScoringCard() && !Game.waitForTrickClear) {
                    var command = {
                        type: 'PLAY_CARD',
                        card: Game.players[0].hand.cards[i],
                        player: Game.player.id
                    };
                    Game.socket.send(JSON.stringify(command));
                }
                //Should only be able to select the topmost card when they are on top of each other
                break;
            }

        }
    }
});

Game.onMouseClickTrading = (function (e) {
    if (Game.tradingCards.length === 3) return;

    var mouse = Game.canvasState.getMouse(e);
    var mx = mouse.x;
    var my = mouse.y;
    var l = Game.players[0].hand.cards.length;
    for (var i = l - 1; i >= 0; i--) {
        var card = Game.players[0].hand.cards[i];
        if (card.contains(mx, my)) {
            Game.tradingCards.push(card);
            Game.players[0].hand.remove(card);
            Game.canvasState.draw();
            if (Game.tradingCards.length === 3) {
                var command = {
                    type: 'TRADE_CARDS',
                    cards: Game.tradingCards
                };
                Game.socket.send(JSON.stringify(command));
            }
            //Should only be able to select the topmost card when they are on top of each other
            break;
        }
    }
});

Game.sendMessage = (function () {
    var message = document.getElementById('chat').value;
    if (message != '') {
        Game.socket.send(message);
        document.getElementById('chat').value = '';
    }
});

Game.drawHand = (function (command) {
    Game.receivedTrade = [];
    Game.players[0].hand = new Hand(command.hand);
    Game.canvasState.draw();
});

Game.receiveTrade = (function (command) {
    Game.receivedTrade = command.cards.map(function (card, index) {
        return new Card(card.value, card.suit, card.points, 300 + index * 25, 360);
    });
});

Game.playTrick = (function (command) {
    var playedCard;
    if (command.playerWhoPlayed.id === Game.player.id) {
        //remove card from own hand
        Game.players[0].hand.remove(command.card);
    }
    //someone else played the card
    Game.players.forEach(function (player, index) {
        if (player.id === command.playerWhoPlayed.id) {
            var x;
            var y;
            switch (index) {
                case 0: // South (you)
                    x = 370;
                    y = 340;
                    break;
                case 1: // West
                    x = 330;
                    y = 290;
                    break;
                case 2: // North
                    x = 390;
                    y = 260;
                    break;
                case 3: // East
                    x = 430;
                    y = 310;
                    break;
            }
            playedCard = new Card(command.card.value, command.card.suit, command.points, x, y);
        }
    });
    Game.currentPlayer = command.currentPlayer;
    Game.trick.push(playedCard);
    Game.canvasState.draw();
    if (Game.trick.length == 4) {
        Game.trick = [];
        Game.waitForTrickClear = true;
        //After 3 seconds clear the trick and set the looser as the current player
        window.setTimeout(function () {
            //Had problem with race condition between the timer and new played cards
            Game.canvasState.draw();
            Game.waitForTrickClear = false;
        }, 2000);
    }
});

document.addEventListener("DOMContentLoaded", function () {
    // Remove elements with "noscript" class - <noscript> is not allowed in XHTML
    var noscripts = document.getElementsByClassName("noscript");
    for (var i = 0; i < noscripts.length; i++) {
        noscripts[i].parentNode.removeChild(noscripts[i]);
    }
}, false);


//Read query parameter
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}