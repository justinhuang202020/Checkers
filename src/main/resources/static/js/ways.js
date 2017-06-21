function caPixels(minL, maxL, currMapCord, totalPixels) {
    return (currMapCord - minL) / (maxL - minL) * totalPixels;
}


function Way(start, end, id) {
    this.latStart = start[0];
    this.longStart = start[1];
    this.longEnd = end[1];
    this.latEnd = end[0];
    this.id = id;
    this.color = 'green';
    this.isPath = false;
    Way.prototype = {
        //draws itself with knowledge of the upper and lower bound of lat, long coordiantes. Draws outside of the canvas too
        //in case  part of the path is within the canvas
        draw: function (ctx, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, pixels, color) {
            ctx.strokeStyle = color;
            let yStart = caPixels(canvasTopLat, canvasLowLat, this.latStart, pixels);
            let yEnd = caPixels(canvasTopLat, canvasLowLat, this.latEnd, pixels);
            let xStart = caPixels(canvasTopLng, canvasLowLng, this.longStart, pixels);
            let xEnd = caPixels(canvasTopLng, canvasLowLng, this.longEnd, pixels);
            if ((xStart >= -200 && xStart <= 700 && yStart >= -200 && yStart <= 700)
                || (xEnd >= -200 && xEnd <= 700 && yEnd >= -200 && yEnd <= 700)) {
                ctx.beginPath();
                ctx.moveTo(xStart, yStart);
                ctx.lineTo(xEnd, yEnd);
                ctx.stroke();
            }
        },
        //based on traffic value, update its color
        changeTrafficStatus: function(value) {
            if (value<=1.5) {
                this.color = 'green';
            }
            else if (value >1.5 && value <=3) {
                this.color = 'yellow';
            }
            else if (value >3) {
                this.color = 'red';
            }
        }, 
        //f way is part of shortestpath
        isPath: function() {
            this.isPath = true;
        },
        notPath: function() {
            this.isPath = false;
        }
    };
}