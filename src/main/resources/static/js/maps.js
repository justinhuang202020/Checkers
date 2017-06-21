const tileSize = 0.003;
let zoomRatio = 0.009;
let canvasTopLat = 41.832;
let canvasTopLng = -71.406;
let canvasLowLat = 41.823;
let canvasLowLng = -71.397;
const totalPixels = 500;
//cached tiles
const cache = {};
//cached ways
const allWays = {};
let ctx;
let clickCount = 1;
//shortest path dict
let path = {};
let initialLoad = true;
let isStreet = false;

function getEventTarget(e) {
    e = e || window.event;
    return e.target || e.srcElement;
}
/**
*rounds each alt and long to the nearest tiile size
**/
function calculateTiles(lat1, long1, lat2, long2) {

    let newLat1 = Math.ceil(lat1 / tileSize) * tileSize;
    let newLat2 = Math.floor(lat2 / tileSize) * tileSize;
    let newLong1 = Math.floor(long1 / tileSize) * tileSize;
    let newLong2 = Math.ceil(long2 / tileSize) * tileSize;

    return getTiles(newLat1, newLong1, newLat2, newLong2);
}



/**
gets tiles within and outside the range of view. 
**/
function getTiles(newLat1, newLong1, newLat2, newLong2) {

    let array = [];
    for (let i = newLong1 - tileSize * 5; i < newLong2 + tileSize * 5; i += tileSize) {
        for (let j = newLat1 + tileSize * 5; j > newLat2 + tileSize * -5; j -= tileSize) {
            let subArray = [j, i];
            array.push(subArray);
        }
    }
    return array;
}


function handlePanning() {
    let isDragging = false;
    let endX;
    let endY;
    let startX;
    let startY;
    let clickX;
    let clickY;
    //calulates start position 
    $("#map").mousedown(function (e) {
        isDragging = false;
        let parentOffset = $(this).parent().offset();
        clickX = e.pageX;
        clickY = e.pageY - 86;
        startX = e.pageX - parentOffset.left;
        startY = e.pageY - parentOffset.top;
        //triggers if dragging happens
    }).mousemove(function (evt) {
        if (!(evt.pageX === startX && evt.pageY === startY)) {
            isDragging = true;
        }
    }).mouseup(function (e) {
        let parentOffset = $(this).parent().offset();
        endX = e.pageX - parentOffset.left;
        endY = e.pageY - parentOffset.top;
         //dragging is finished calcualte final position
        if (isDragging) {
            panMap(startX, startY, endX, endY);
        } else {
            //is not dragging but a click
            handleClick(clickX, clickY);
        }
        isDragging = false;

    });
}
function handleClick(x, y) {
    // if odd click, calcualte click and put lat, long values in textarea 1 and 2
    if (clickCount === 1) {
        let startLat = canvasTopLat + (canvasLowLat - canvasTopLat) * (y / 500);
        let startLng = canvasTopLng + (canvasLowLng - canvasTopLng) * (x / 500);
        $('#street1').val(startLat);
        $('#street2').val(startLng);
        clickCount += 1;
        //else put it in text area 3 and 4
    } else if (clickCount === 2) {
        let endLat = canvasTopLat + (canvasLowLat - canvasTopLat) * (y / 500);
        let endLng = canvasTopLng + (canvasLowLng - canvasTopLng) * (x / 500);

        $('#street3').val(endLat);
        $('#street4').val(endLng);
        clickCount = 1;
    }

}
//converts pixels to Map Coordinates
function convertToMapCord(currPixel, diff) {
    return (currPixel / totalPixels) * (zoomRatio) + diff;
}
/**
recalcualtes upper and bottom bound map coordiantes, and recalculates and re-draws map
**/
function panMap(startX, startY, endX, endY) {
    let startLat = convertToMapCord(startY, canvasLowLat);
    let startLng = convertToMapCord(startX, canvasLowLng);
    let endLat = convertToMapCord(endY, canvasLowLat);
    let endLng = convertToMapCord(endX, canvasLowLng);
    let deltaLat = endLat - startLat;
    let deltaLng = endLng - startLng;
    canvasTopLat += deltaLat;
    canvasTopLng -= deltaLng;
    canvasLowLat += deltaLat;
    canvasLowLng -= deltaLng;
    let tiles = calculateTiles(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
    postRequestTiles(tiles);
}
/**
redraw map, grabs tiles from cache and if has not been cached make a post request
**/
function postRequestTiles(neededTiles) {
    //resets canvas
    ctx.clearRect(0, 0, totalPixels,totalPixels);
    ctx.beginPath();
    //fills background with black
    ctx.rect(0, 0, totalPixels, totalPixels);
    ctx.fillStyle = "black";
    ctx.fill();
    let serverTiles = [];
    for (let i in neededTiles) {
        let key = neededTiles[i];
        //if tiles is in cache
        if (cache[key] !== undefined) {
            let tile = cache[key];
            //each tile knows canvas map coordinate bounds
            tile.updateCanvas(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
            tile.draw(ctx);
        } else {
            //tiles has not been make
            serverTiles.push(neededTiles[i]);
        }
    }

    const postParameters = {
        "coordinates": JSON.stringify(serverTiles),
        "size": tileSize
    };
    $.post("/tiles", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        let tiles = responseObject.tiles;
        let newTiles = [];
        for (let i = 0; i < tiles.length; i++) {
            let ways = [];
            let curr = tiles[i];
            //each tiles has access to all ways
            for (let key in curr.waysToNodes) {
                let nodes = curr.waysToNodes[key];
                let start = nodes[0];
                let end = nodes[1];
                 //start contains array with start[0] = latStart and start[1] contains latEnd
                let startPos = curr.nodeCoords[start];
                 //same but with end coordinates
                let endPos = curr.nodeCoords[end];
                let w = new Way(startPos, endPos, key);
                ways.push(w);
                allWays[key] = w;
            }
            let lat = curr.coordinates[0];
            let lng = curr.coordinates[1];
            let tile = new Tile(ways, lat, lng, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, 500);
            cache[[lat, lng]] = tile;
            newTiles.push(tile);
        }
        //getTraffic attatched on first call because javascript is asynchronous
        if (initialLoad) {
            getTraffic();
            initialLoad = false;
        }
        //draws new tiiles
        for (t in newTiles) {
            let currTile = newTiles[t];
            currTile.draw(ctx);
        }
    });
    //redraws shortest path in case of pan/zoom
    for (key in path) {
        let currWay = path[key];
        currWay.draw(ctx, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, totalPixels, "white");
    }
}
/**
handles zoom by increasing upper and bottom bound by a constant. zooms in and out from the center 
**/
function handleZoom(event) {
    event.preventDefault();
    //if zoom out 
    if (event.wheelDelta > 0) {
        canvasTopLat += .0005;
        canvasTopLng -= .0005;
        canvasLowLat -= .0005;
        canvasLowLng += .0005;
        let tiles = calculateTiles(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
        postRequestTiles(tiles);
    }
    //if zoom in 
    else if (event.wheelDelta < 0) {
        canvasTopLat -= .0005;
        canvasTopLng += .0005;
        canvasLowLat += .0005;
        canvasLowLng -= .0005;
        let tiles = calculateTiles(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
        postRequestTiles(tiles);
    }


}
//when the form is submited 
function submitForm(click) {
    //clears the path 
    clearPath();
//gets values of text boxes
    const $street1 = $('#street1').val().trim();
    const $street2 = $('#street2').val().trim();
    const $street3 = $('#street3').val().trim();
    const $street4 = $('#street4').val().trim();
    let isFilled = true;
    let values = [$street1, $street2, $street3, $street4];
    //checks if any boxes are empty
    for (let i = 0; i < values.length; i++) {
        if (!values[i]) {
            isFilled = false;
            break;
        }
    }
    if (isFilled) {
        
        let postParameters = {
            oneLat1: $street1,
            twoLng1: $street2,
            threeLat2: $street3,
            fourLng2: $street4
        }

        $.post("/route", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const $result = $('#result');
            // if no path is found 
            if (responseObject.status === "fail") {
                $result.empty();
                $result.html("No path found. Check inputs");
            } else {
                const ways = responseObject.path;
                for (let key in ways) {
                    //puts each way in shortest path in a dict
                    let currWay;
                    let coords = ways[key];
                    let start = [coords[0], coords[1]];
                    let end = [coords[2], coords[3]];
                    currWay = new Way(start, end, key);
                    path[currWay.id] = currWay;
                    currWay.draw(ctx, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, totalPixels, 'white');
                }
                ctx.strokeStyle = 'green';
                $result.empty();
                $result.html("Path found!");
            }
        });
    }
}
function clearPath() {
    //clears message area
    $("#result").empty();
    for (key in path) {
        //clears path by updating its reference to whether its inthe path or not and redraws it with its referecne to the traffic color
        let currWay = path[key];
        currWay.notPath();
        currWay.draw(ctx, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, totalPixels, currWay.color);
    }
    path = {};
}
function getTraffic() {
    let tiles = calculateTiles(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
    const viewTiles = [];
    let ways = [];
//gets Tiles
    for (let i = 0; i < tiles.length; i++) {
        let key = tiles[i];
        if (cache[key] !== undefined) {
            let currTile = cache[key];
            viewTiles.push(currTile);
        }
    }
    postParameters = {};
    $.post("/traffic", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);

        const traffic = responseObject.traffic;
        //for each tiles
        for (let i = 0; i < viewTiles.length; i++) {
            //for ways in the tile
            let currTileWays = viewTiles[i].ways;
            for (let j = 0; j < currTileWays.length; j++) {
                //for each way
                const currWay = currTileWays[j];
                //initial color of way
                let initialColor = currWay.color;
                //gets traffic value
                currWayTrafficValue = traffic[currWay.id];
                if (currWayTrafficValue !== undefined) {
                    //updates traffic status which updates way's referene to its color
                    currWay.changeTrafficStatus(currWayTrafficValue);
                    // draws ways if traffic status hasn't changed and is not part of path
                    if (path[currWay.id] === undefined && currWay.color!= initialColor) {
                        currWay.draw(ctx, canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng, totalPixels, currWay.color);
                    }
                }
            }
        }

    });


}
function toggleSuggestions() {
    const $suggestionsList = $(".suggestions");
    $box = $("#box");
    $box.on('click', event => {
        //shows suggetsions list if checked
        if ($box.is(':checked')) {
            isStreet = true;
            $suggestionsList.show();
        }
        else {
            isStreet = false;
            $suggestionsList.hide();
        }
    });

}
function sendSuggestion(text, list) {
    list.empty();
    const val = text.value;
    if ((text.val().trim())) {
        const postParameters = {
            phrase: text.val()
        };
        $.post("/suggestions", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const suggestions = responseObject.suggestions;
            let length;
            //if array is less than 5 than will only go the length of suggestions to avoid index out of bounds
            if (suggestions.length < 5) {
                length = suggestions.length;
            }
            else {
                length = 5;
            }
            for (let i = 0; i < length; i++) {
                let currSuggestion = suggestions[i];
                addToList(currSuggestion, list);
            }
        });
    }
}
function addToList(suggestion, suggestionList) {
    //embeds an a tag in a li tag which is added to the list
    const li = $("<li></li>");
    const a = $("<a> </a>");
    a.html(suggestion + '<br>');
    li.append(a);
    suggestionList.append(li);
}
/**
sends suggestions if user types in text box
**/
function getSuggestions() {
    $suggestionsList1 = $("#suggestion1");
    $suggestionsList2 = $("#suggestion2");
    $suggestionsList3 = $("#suggestion3");
    $suggestionsList4 = $("#suggestion4");
    $textBox1 = $("#street1");
    $textBox2 = $("#street2");
    $textBox3 = $("#street3");
    $textBox4 = $("#street4");
    $textBox1.keyup(event => {
        sendSuggestion($textBox1, $suggestionsList1);
    });
    $textBox2.keyup(event => {
        sendSuggestion($textBox2, $suggestionsList2);
    });
    $textBox3.keyup(event => {
        sendSuggestion($textBox3, $suggestionsList3);
    });
    $textBox4.keyup(event => {
        sendSuggestion($textBox4, $suggestionsList4);
    });
}

$(document).ready(() => {
    //next four sets of similar code makes suggestions clickable and puts the suggestion in the text box
    document.getElementById('suggestion1').onclick = function (event) {
        $text = $("#street1");
        const target = getEventTarget(event);
        $text.val(target.text);
    }
    document.getElementById('suggestion2').onclick = function (event) {
        $text = $("#street2");
        const target = getEventTarget(event);
        $text.val(target.text);
    }
    document.getElementById('suggestion3').onclick = function (event) {
        $text = $("#street3");
        const target = getEventTarget(event);
        $text.val(target.text);
    }
    document.getElementById('suggestion4').onclick = function (event) {
        $text = $("#street4");
        const target = getEventTarget(event);
        $text.val(target.text);
    }
    toggleSuggestions();
    getSuggestions();
    $(".suggestions").hide();
    const submit = document.getElementById('submit');
    const clear = document.getElementById('clear');
    submit.onclick = submitForm;
    clear.onclick = clearPath;
    //updates trafffic every second
    setInterval(getTraffic, 1000);
    let canvas = $('#map')[0];
    ctx = canvas.getContext("2d");
    //initial load
    let array = getTiles(canvasTopLat, canvasTopLng, canvasLowLat, canvasLowLng);
    postRequestTiles(array);
    ctx.lineWidth = 1;
    ctx.strokeStyle = 'green';
    handlePanning();

    document.getElementById("map").addEventListener("wheel", handleZoom);
});

