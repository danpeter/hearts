'use strict';

function Card(value, suit, points, x, y) {
    this.value = value;
    this.suit = suit;
    this.points = points;
    this.x = x;
    this.y = y;
}
//Height and width are static
Card.w = 125;
Card.h = 181;

Card.prototype.draw = function (ctx) {
    var card = this;
    var imageName = (this.value + '_of_' + this.suit).toLowerCase();
    ctx.drawImage(Game.canvasState.images[imageName], card.x, card.y, Card.w, Card.h);
};

Card.prototype.contains = function (mx, my) {
    return (this.x <= mx) && (this.x + Card.w >= mx) &&
        (this.y <= my) && (this.y + Card.h >= my);
};

Card.prototype.hasSameSuite = function (otherCard) {
    return this.suit === otherCard.suit;
};
