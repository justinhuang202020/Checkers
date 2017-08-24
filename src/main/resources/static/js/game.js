let conn;
let userName;
let isBlack;
let userEmail;
let opponent;
// Global reference to the canvas element.
let canvas;
let piece = {xCord: undefined, yCord: undefined};
let clicked = false;
let moveType;

// Global reference to the canvas' context.
let ctx;
let isTurn = false;

let possibleMoves;
let opponentRecord;
//size of one row of a checkers board
const BOARD_SIZE = 8;
const TILE_SIZE = 50;
//different message types sent via sockets
const MESSAGE_TYPE = {
  JOINGAME: 0,
  STARTGAME: 1,
  ISTURN: 2, 
  GETCORDS: 3, 
  GETPOSSIBLEMOVES: 4,
  MOVE: 5,
  UPDATEBOARD: 6,
  ENDTURN: 7, 
  WINNER: 8,
  LOSER: 9,
  WINBYFORFEIT: 10,
  PING: 11,
  MESSAGE: 12 
};
let connected = false;
let gameOver = false;
window.setInterval(function(){
    //sends a message to the backend every 5 seconds so program doesn't disconnect easily
	if (connected) {
  	let mess = {"type": MESSAGE_TYPE.PING};
  	conn.send(JSON.stringify(mess));
  }
}, 5000);
const sendMessage = event => {
  if (event !==undefined) {
    event.preventDefault();
  }
    let  message = $('#messageField').val();
    if (message != "\n" || message.length!=0) {  
        //emptys message field
        //sends message to the backend
        let mess = {"type" : MESSAGE_TYPE.MESSAGE, "message": message, "userName": userName, "isBlack": isBlack};
        conn.send(JSON.stringify(mess));
    }
    //clears the message field
    $('#messageField').val("");
}

$(document).ready(function(){

    document.getElementById("chatButton").onclick = sendMessage;
     document.getElementById("closer").onclick = function() {
    document.getElementById('myModal').style.display = "none";
    }
    document.querySelector("#messageField").addEventListener("keyup", function (e) {
    let key = e.keyCode;
    if (key === 13) { 
      sendMessage();
    }
});
    //hides all unecessary divs on startup
	$("#boardDiv").hide();
	$("#chatting").hide();
	$("#otherChatElements").hide();
	$("#turnStatus").hide();
	$("#forfeit").hide();
	$("#loser").hide();
	$("#winner").hide();
    //if not logged in redirect to login page
 	firebase.auth().onAuthStateChanged(function(user) {
    	if (!user) {
    		window.location = "/login";
    	}
    	userEmail = user.email;
        //sends message to backend checking if the user (based on their email) is already in a game
    	let parameters = {email: user.email};
    	$.post('/checkDuplicate', parameters, function (result){
    		let duplicate = JSON.parse(result).duplicate;
    		if (!duplicate) {
    			setup_connection();
    		}
    		else {
    			alert("You are currently connected to a game. Exit that session or play that session");
    		}
    	});
    	
    });
  });
function setModal() {
    document.getElementById('myModal').style.display = "block";
}
function setup_connection () {
	// conn = new WebSocket("ws://localhost:4567/matches");
    conn = new WebSocket("ws://104.131.23.151/matches");
	conn.onopen = function(event) {
		connected = true;
	};
	conn.onerror = err => {
  };

  // Display leaver message
  conn.onclose = function(e) {
    connected = false;
  };

  conn.onmessage = msg => {
  		let mess;
  		const data = JSON.parse(msg.data);
    	switch (data.type) {
      	default:
        break;


        case MESSAGE_TYPE.JOINGAME:
        console.log("Join Game")
        userName = data.userName;
        mess = {"type": MESSAGE_TYPE.JOINGAME, "userName": userName};
        conn.send(JSON.stringify(mess));
        break;

        case MESSAGE_TYPE.STARTGAME:
        //sets if the player is black
        isBlack = data.isBlack;
        opponentRecord = data.opponentRecord;
        //sets the record of the opponent into the modal to be cached
        document.getElementById('wins').innerHTML = "Wins: " + opponentRecord[0];
        document.getElementById('losses').innerHTML = "Losses: " + opponentRecord[1];
        document.getElementById('winsByForfeit').innerHTML = "Wins By Forfeit: " + opponentRecord[2];
        document.getElementById("forfeits").innerHTML = "Forfeits: " + opponentRecord[3];
        console.log(opponentRecord);
        $("#turnStatus").show();
        $("#boardDiv").show();
		$("#chatting").show();
		$("#otherChatElements").show();
        isTurn = data.isTurn;
        opponent = data.opponent;
        $opponent = $("<span id = 'opponentName'></span>");
        $opponent.text(opponent);
        $("#opponent").append("You are facing ");
        $("#opponent").append($opponent);
        $("#opponent").append("!");
    	$("#opponentsTurnText").append("It is " + opponent +"'s  turn!");
         document.getElementById('opponentName').onclick = setModal;
        if (data.isTurn) {
        	$("#isTurnText").show();
        	$("#opponentsTurnText").hide();
    	}
    	else {
    		$("#isTurnText").hide();
    		$("#opponentsTurnText").show();
    	}
        drawBoard();
        break;

        case MESSAGE_TYPE.ISTURN:
         $("#isTurnText").show();
         $("#opponentsTurnText").hide();
        isTurn = true;
        break;

        case MESSAGE_TYPE.GETPOSSIBLEMOVES:
        clicked = true;
        possibleMoves = undefined;
        //gets the cords of possible moves of a piece stored in a map with an x coordinate key and a value of an array of y coordiantes
        possibleMoves = data.cords;
        let moves = possibleMoves;
        //gets the x cord and y cord of the piece
        piece.xCord = data.pieceXCord;
        piece.yCord = data.pieceYCord;
        //gets the movetype of the piece (normal move or jump move)
        moveType = data.moveType;
         drawPossibleMoves(moves);
        break;
        case MESSAGE_TYPE.UPDATEBOARD:
        let xStart = data.pieceXStart;
        let yStart = data.pieceYStart;
        let xEnd = data.pieceXEnd;
        let yEnd = data.pieceYEnd;
        let isKing = data.isKing;
        visuallyMovePiece(xStart, yStart, xEnd, yEnd, data.isBlack, isKing);
        if (data.moveType ==="jump") {
        	removeOpponentPiece(xStart, yStart, xEnd, yEnd, data.isBlack);
        }
        break;
        case MESSAGE_TYPE.ENDTURN:
 		$("#isTurnText").hide();
        $("#opponentsTurnText").show();
       isTurn = false;
        break;
        case MESSAGE_TYPE.WINNER:
        gameOver =true;
        $("#winner").show();
        $("#isTurnText").hide();
        $("#opponentsTurnText").hide();
        break;
        case MESSAGE_TYPE.LOSER:
        gameOver = true;
        $("#loser").show();
        $("#isTurnText").hide();
        $("#opponentsTurnText").hide();
        break;
        case MESSAGE_TYPE.WINBYFORFEIT:
        gameOver = true;
        $("#forfeit").show();
        $("#winner").show();
        $("#isTurnText").hide();
        $("#opponentsTurnText").hide();

        break;
        //sends a chat message 
        case MESSAGE_TYPE.MESSAGE:
        getMessage(data.message.toString(), data.userName.toString(), data.isBlack);
        break;
    }
   
  };
}
function getMessage(message, messageName, messageIsBlack) {
    let string; 
    let color;
  if (userName === messageName) {
    string = "Me: " + message;
  }
  else {
    string = messageName + ": " + message;
  }
  $li = $("<li class = 'chat'></li>");
  $li.html(string);

  $("#chatting").append($li);
  if (messageIsBlack) {
    color = "black"
  }
  else {
    color = "#F5DEB3";
  }
  $li.css("border", "1px solid " + color);
    $li.css("border-left", "6px solid " + color);
    blink($li);

    $("#chatting").scrollTop($("#chatting")[0].scrollHeight);
}

//used to flash message
function blink(selector){
    let count = 0;
  while(count<6) {
  $(selector).fadeOut('slow', function(){
      $(this).fadeIn('slow', function(){
          blink(this);
      });
  });
  count++;
  }
}
function getRidOfPossibleMoves(moves) {
	for (key in moves) {
		let xCord = key;
		let currYCord;
		let yCords = moves[key];
        // if black, board is flipped and cords must be accounted as such
		if (isBlack) {
			xCord = 7-xCord;
		}
        // for each y cord associated with the x cord
		for (let i = 0; i<yCords.length; i ++) {
			currYCord = yCords[i];
			if (isBlack) {
				currYCord = 7-currYCord;
			}
			ctx.beginPath();
			ctx.lineWidth = 0;
            // replaces the green circle with the brown color
			ctx.arc(xCord*TILE_SIZE + TILE_SIZE/2, currYCord*TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
			ctx.fillStyle = "brown";
			ctx.fill();
			ctx.closePath();
		}
	}
}
function drawPossibleMoves(moves) {
	for (let key in moves) {
		let xCord = key;
		let yCords = moves[key];
		if (isBlack) {
			xCord = 7-xCord;
		}
		for (let i = 0; i<yCords.length; i ++) {
			let currYCord = yCords[i];
            if (isBlack) {
                currYCord = 7-currYCord;
            }
			ctx.beginPath();
			let x = xCord*TILE_SIZE + TILE_SIZE/2;
            // the radius is slightly smaller than TILE_SIZE/2 so that when the green circle is erased all of it will be erased
			ctx.arc(xCord*TILE_SIZE + TILE_SIZE/2, currYCord*TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2-2, 0, 2*Math.PI);
			ctx.fillStyle = "green";
			ctx.fill();
			ctx.closePath();
		}
	}
}
function visuallyMovePiece(xStart, yStart, xEnd, yEnd, pieceIsBlack, isKing) {
	if (isBlack) {
		xStart = 7-xStart;
		yStart = 7-yStart;
		xEnd = 7-xEnd;
		yEnd = 7-yEnd;
	}
    let textColor;
	ctx.beginPath();
    //replaces the piece that is to be moved with the brown background
	ctx.arc(xStart * TILE_SIZE + TILE_SIZE/2, yStart * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
	ctx.fillStyle = "brown";
	ctx.fill();
	ctx.closePath();
	ctx.beginPath();
    //replaces the new spot with the piece's color
	ctx.arc(xEnd * TILE_SIZE + TILE_SIZE/2, yEnd * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2-2, 0, 2*Math.PI);
	if (pieceIsBlack) {
		ctx.fillStyle = "black";
        textColor = "#F5DEB3";

	}
	else {
		ctx.fillStyle = "#F5DEB3";
        textColor = "black";
	}
	ctx.fill();
	ctx.closePath();
    if (isKing) {
        ctx.fillStyle = textColor;
        ctx.fillText("K", xEnd*TILE_SIZE + TILE_SIZE/2-3, yEnd*TILE_SIZE + TILE_SIZE/2 + 2, 46);
    }

}
function removeOpponentPiece(xStart, yStart, xEnd, yEnd) {
    // replaces the opponent piece with the brown board color
	if (isBlack) {
		xStart = 7-xStart;
		yStart = 7-yStart;
		xEnd = 7-xEnd;
		yEnd = 7-yEnd;
	}
	let removePieceX = (xStart + xEnd)/2;
	let removePieceY = (yStart + yEnd)/2;
	ctx.beginPath();
	ctx.arc(removePieceX * TILE_SIZE + TILE_SIZE/2, removePieceY * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
	ctx.fillStyle = "brown";
	ctx.fill();
	ctx.closePath();
}
// handles board clicks
function boardClick(e) {
	if (!gameOver) {
		let parentOffset = $(this).parent().offset();
		let xCord = Math.floor((e.pageX-parentOffset.left)/50);
		let yCord = Math.floor((e.pageY-parentOffset.top)/50);
		if (isBlack) {
			xCord = 7-xCord;
			yCord =  7-yCord;
		}
        // if it's the players turn and the click is in bounds
		if (isTurn && xCord>=0 && yCord >=0) {
			let mess;
            // if this click comes after a click that revealed a piece's possible moves, send the information to the backend
			if (clicked) {
				clicked = false;
				mess = {"moveType": moveType, "type": MESSAGE_TYPE.MOVE, "pieceXCord": piece.xCord, "pieceYCord": piece.yCord, "moveToXCord": xCord, "moveToYCord": yCord, "userName": userName, "isBlack": isBlack};
				conn.send(JSON.stringify(mess));
			}
			else {
                // else send an information request for possible moves for a piece
				mess = {"type": MESSAGE_TYPE.GETCORDS, "pieceXCord": xCord, "pieceYCord": yCord, "userName": userName, "isBlack": isBlack};
				conn.send(JSON.stringify(mess)); 
			}
		}
        // get rid of any possiblemoves (green circles) if they exist
		if (possibleMoves !==undefined) {
			let moves = possibleMoves;
			getRidOfPossibleMoves(moves);
			possibleMoves = undefined;
		}
	}

}
function drawBoard() {
	$("#boardDiv").show();
	$(".loader").hide();
	$("#text").hide();

	canvas = $('#board')[0];
    canvas.width = BOARD_SIZE * TILE_SIZE;
    canvas.height = BOARD_SIZE * TILE_SIZE;
    canvas.onclick = boardClick;
    ctx = canvas.getContext("2d");
    ctx.lineWidth = 0;

    // set line color
    ctx.strokeStyle = 'black';
// Depending on what row it is on (j), the ordering of whether brown or light brown goes first in the column
    for (let i = 0; i<8; i ++) {
        //arbritary name to determine whether brown or light brown color is put first on the first column of the row
    	let black;
        if (i%2 === 0) {
            black = 0;
        }
        else {
            black = 1;
        }
    	for (let j = 0; j<8; j++) {
    		if (j % 2 ==0) {
    			if(black === 0) {
    				ctx.fillStyle = "brown";
    			}
    			else {
    				ctx.fillStyle = "#F5DEB3";
    			}
    		}
    		else {
    			if (black === 0) {
    				ctx.fillStyle = "#F5DEB3";
    			}
    			else {
    				ctx.fillStyle = "brown";
    			}
    		}
    		ctx.fillRect(i*TILE_SIZE, j*TILE_SIZE, i*TILE_SIZE + TILE_SIZE, j*TILE_SIZE + TILE_SIZE);

    	}
    } 
    addPieces();
}

function addPieces() {
    //same as back end. Whether the player is black or not affects this
    //this for loop handles the first 3 rows of pieces
	for (let i = 0; i<3; i ++) {
        //determines which column should the pieces start being placed
		let start;
		 if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
        let color;
        ctx.lineWidth = 0;
        if (isBlack) {
        	color =  "#F5DEB3";
        }
        else {
        	color = "black";
        }
		for (let j = start; j < 8; j+=2) {
			ctx.beginPath();
			ctx.arc(j*TILE_SIZE + TILE_SIZE/2, i*TILE_SIZE + TILE_SIZE/2,23,0,2*Math.PI);

			ctx.fillStyle = color;
			ctx.fill();
			ctx.closePath();
		}
	}
    //handles the last three rows 
	for (let i = 5; i<8; i ++) {
		let start;
		 if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
        let color;
        if (isBlack) {
        	color =  "black";
        }
        else {
        	color = "#F5DEB3";
        }
		for (let j = start; j < 8; j+=2) {
			ctx.beginPath();
			ctx.arc(j*TILE_SIZE + TILE_SIZE/2, i*TILE_SIZE + TILE_SIZE/2,23,0,2*Math.PI);

			ctx.fillStyle = color;
			ctx.fill();
			ctx.closePath();


		}
	}
}
function signOut() {
	firebase.auth().signOut().then(function() {
    window.location = "/";
  	}).catch(function(error) {
    	alert(error.message);
    	window.location = "/";

  });	
}
window.onbeforeunload = function() {
	return "exiting will cause you to forfeit the game";
};