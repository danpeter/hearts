'use strict';

function PlayAgainButton() {
    this.image = Game.canvasState.images['play_again'];
    this.x = 300;
    this.y = 384;
    this.h = 57;
    this.w = 391;
}

PlayAgainButton.prototype.draw = function (ctx) {
    ctx.drawImage(this.image, this.x, this.y, this.w, this.h);
};

PlayAgainButton.prototype.contains = function (mx, my) {
    return (this.x <= mx) && (this.x + this.w >= mx) &&
        (this.y <= my) && (this.y + this.h >= my);
};