function cPixels(minL, maxL, currMapCord, totalPixels) {
    return (currMapCord - minL) / (maxL - minL) * totalPixels;
}
function Tile(ways, lat, long, minLat, minLong, maxLat, maxLong, pixels) {
    this.latStart = lat;
    //end ocordinates are offseted by .003 since that's the tile size
    this.latEnd = lat - .003;
    this.longStart = long;
    this.longEnd = long + .003;
    this.ways = ways;
    this.minLat = minLat;
    this.maxLat = maxLat;
    this.minLong = minLong;
    this.maxLong = maxLong;
    this.pixels = pixels;
}
Tile.prototype = {
    //updates its references to the top/bottom of canvas in terms of map coordiantes
    updateCanvas: function (newMinLat, newMinLong, newMaxLat, newMaxLong) {
        this.minLat = newMinLat;
        this.maxLat = newMaxLat;
        this.minLong = newMinLong;
        this.maxLong = newMaxLong;
    },
    draw: function (ctx) {
        for (let i = 0; i < this.ways.length; i++) {
            let curr = this.ways[i];
            //makes sure that none of the ways are in the path
            if (!curr.isPath) {
                 ctx.strokeStyle = curr.color;
            let yStart = cPixels(this.minLat, this.maxLat, curr.latStart, this.pixels);
            let yEnd = cPixels(this.minLat, this.maxLat, curr.latEnd, this.pixels);
            let xStart = cPixels(this.minLong, this.maxLong, curr.longStart, this.pixels);
            let xEnd = cPixels(this.minLong, this.maxLong, curr.longEnd, this.pixels);
            if ((xStart >= -200 && xStart <= 700 && yStart >= -200 && yStart <= 700)
                || (xEnd >= 0 && xEnd <= 500 && yEnd >= 0 && yEnd <= 500)) {
                ctx.beginPath();
                ctx.moveTo(xStart, yStart);
                ctx.lineTo(xEnd, yEnd);
                ctx.stroke();
                }
             

        }

    }


}
};
