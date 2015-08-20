'use strict';

function CanvasState(canvas, finishedLoading) {
    function loadImages(sources, callback) {
        var images = {};
        var loadedImages = 0;
        var numImages = 0;
        // get num of sources
        for (var src in sources) {
            numImages++;
        }
        for (var src in sources) {
            images[src] = new Image();
            images[src].onload = function () {
                if (++loadedImages >= numImages) {
                    callback(images);
                }
            };
            images[src].src = sources[src];
        }
    }

    this.canvas = canvas;
    this.width = canvas.width;
    this.height = canvas.height;
    this.ctx = canvas.getContext('2d');

    var canvasState = this;
    loadImages(image_src, function (images) {
        canvasState.images = images;
        //canvasState.draw();
        finishedLoading();
    });

    var stylePaddingLeft, stylePaddingTop, styleBorderLeft, styleBorderTop;
    if (document.defaultView && document.defaultView.getComputedStyle) {
        this.stylePaddingLeft = parseInt(document.defaultView.getComputedStyle(canvas, null)['paddingLeft'], 10) || 0;
        this.stylePaddingTop = parseInt(document.defaultView.getComputedStyle(canvas, null)['paddingTop'], 10) || 0;
        this.styleBorderLeft = parseInt(document.defaultView.getComputedStyle(canvas, null)['borderLeftWidth'], 10) || 0;
        this.styleBorderTop = parseInt(document.defaultView.getComputedStyle(canvas, null)['borderTopWidth'], 10) || 0;
    }
    // Some pages have fixed-position bars (like the stumbleupon bar) at the top or left of the page
    // They will mess up mouse coordinates and this fixes that
    var html = document.body.parentNode;
    this.htmlTop = html.offsetTop;
    this.htmlLeft = html.offsetLeft;
}

CanvasState.prototype.getMouse = function (e) {
    var element = this.canvas, offsetX = 0, offsetY = 0, mx, my;

    // Compute the total offset
    if (element.offsetParent !== undefined) {
        do {
            offsetX += element.offsetLeft;
            offsetY += element.offsetTop;
        } while ((element = element.offsetParent));
    }

    // Add padding and border style widths to offset
    // Also add the <html> offsets in case there's a position:fixed bar
    offsetX += this.stylePaddingLeft + this.styleBorderLeft + this.htmlLeft;
    offsetY += this.stylePaddingTop + this.styleBorderTop + this.htmlTop;

    mx = e.pageX - offsetX;
    my = e.pageY - offsetY;

    // We return a simple javascript object (a hash) with x and y defined
    return {x: mx, y: my};
};

CanvasState.prototype.clear = function () {
    this.ctx.clearRect(0, 0, this.width, this.height);
};

// While draw is called as often as the INTERVAL variable demands,
// It only ever does something if the canvas gets invalidated by our code
CanvasState.prototype.draw = function () {

    function printCurrentPlayer() {
        if (Game.currentPlayer != null) {
            ctx.font = "30px Arial";
            ctx.fillStyle = "white";
            ctx.fillText(Game.currentPlayer.name + '\'s turn.', 350, 30);
        }
    }

    function printScore() {
        ctx.font = '30px Arial';
        ctx.fillStyle = 'white';
        ctx.fillText('Score', 10, 30);
        Game.players.forEach(function (player, i) {
            ctx.fillText(player.name + ': ' + player.score, 10, 55 + i * 25);
        });
    }

    var ctx = this.ctx;
    this.clear();

    Game.players[0].hand.cards.forEach(function (card) {
        card.draw(ctx);
    });
    Game.trick.forEach(function (card) {
        card.draw(ctx);
    });

    Game.receivedTrade.forEach(function (card) {
        card.draw(ctx);
    });
    printCurrentPlayer();
    printScore();
};

CanvasState.prototype.printMessageTop = function (message) {
    var ctx = this.ctx;
    ctx.font = "30px Arial";
    ctx.fillStyle = "white";
    ctx.fillText(message, 350, 30);
};


//Image sources
var image_src = {
    two_of_clubs: 'images/2_of_clubs.png',
    three_of_clubs: 'images/3_of_clubs.png',
    four_of_clubs: 'images/4_of_clubs.png',
    five_of_clubs: 'images/5_of_clubs.png',
    six_of_clubs: 'images/6_of_clubs.png',
    seven_of_clubs: 'images/7_of_clubs.png',
    eight_of_clubs: 'images/8_of_clubs.png',
    nine_of_clubs: 'images/9_of_clubs.png',
    ten_of_clubs: 'images/10_of_clubs.png',
    jack_of_clubs: 'images/jack_of_clubs.png',
    queen_of_clubs: 'images/queen_of_clubs.png',
    king_of_clubs: 'images/king_of_clubs.png',
    ace_of_clubs: 'images/ace_of_clubs.png',

    two_of_spades: 'images/2_of_spades.png',
    three_of_spades: 'images/3_of_spades.png',
    four_of_spades: 'images/4_of_spades.png',
    five_of_spades: 'images/5_of_spades.png',
    six_of_spades: 'images/6_of_spades.png',
    seven_of_spades: 'images/7_of_spades.png',
    eight_of_spades: 'images/8_of_spades.png',
    nine_of_spades: 'images/9_of_spades.png',
    ten_of_spades: 'images/10_of_spades.png',
    jack_of_spades: 'images/jack_of_spades.png',
    queen_of_spades: 'images/queen_of_spades.png',
    king_of_spades: 'images/king_of_spades.png',
    ace_of_spades: 'images/ace_of_spades.png',

    two_of_diamonds: 'images/2_of_diamonds.png',
    three_of_diamonds: 'images/3_of_diamonds.png',
    four_of_diamonds: 'images/4_of_diamonds.png',
    five_of_diamonds: 'images/5_of_diamonds.png',
    six_of_diamonds: 'images/6_of_diamonds.png',
    seven_of_diamonds: 'images/7_of_diamonds.png',
    eight_of_diamonds: 'images/8_of_diamonds.png',
    nine_of_diamonds: 'images/9_of_diamonds.png',
    ten_of_diamonds: 'images/10_of_diamonds.png',
    jack_of_diamonds: 'images/jack_of_diamonds.png',
    queen_of_diamonds: 'images/queen_of_diamonds.png',
    king_of_diamonds: 'images/king_of_diamonds.png',
    ace_of_diamonds: 'images/ace_of_diamonds.png',

    two_of_hearts: 'images/2_of_hearts.png',
    three_of_hearts: 'images/3_of_hearts.png',
    four_of_hearts: 'images/4_of_hearts.png',
    five_of_hearts: 'images/5_of_hearts.png',
    six_of_hearts: 'images/6_of_hearts.png',
    seven_of_hearts: 'images/7_of_hearts.png',
    eight_of_hearts: 'images/8_of_hearts.png',
    nine_of_hearts: 'images/9_of_hearts.png',
    ten_of_hearts: 'images/10_of_hearts.png',
    jack_of_hearts: 'images/jack_of_hearts.png',
    queen_of_hearts: 'images/queen_of_hearts.png',
    king_of_hearts: 'images/king_of_hearts.png',
    ace_of_hearts: 'images/ace_of_hearts.png'
};