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

const BOARD_SIZE = 8;
const TILE_SIZE = 50;
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
	if (connected) {
		console.log("Ping");
  	let mess = {"type": MESSAGE_TYPE.PING};
  	conn.send(JSON.stringify(mess));
  }
}, 5000);
const sendMessage = event => {
  if (event !==undefined) {
    event.preventDefault();
  }
    let  message = $('#messageField').val();
    
    //emptys message field
    $('#messageField').val("");
    //sends message to the backend
    console.log("message sent: " + message)
    let mess = {"type" : MESSAGE_TYPE.MESSAGE, "message": message, "userName": userName, "isBlack": isBlack};
    conn.send(JSON.stringify(mess));
}

$(document).ready(function(){
    document.getElementById("chatButton").onclick = sendMessage;
document.querySelector("#messageField").addEventListener("keyup", function (e) {
    let key = e.keyCode;
    if (key === 13) { 
      sendMessage();
    }
});
	$("#boardDiv").hide();
	$("#chatting").hide();
	$("#otherChatElements").hide();
	$("#turnStatus").hide();
	$("#forfeit").hide();
	$("#loser").hide();
	$("#winner").hide();
 	firebase.auth().onAuthStateChanged(function(user) {
    	if (!user) {
    		window.location = "/login";
    	}
    	userEmail = user.email;
    	let parameters = {email: user.email};
    	$.post('/checkDuplicate', parameters, function (result){
    		let duplicate = JSON.parse(result).duplicate;
    		console.log(duplicate);
    		if (!duplicate) {
    			console.log("enter");
    			setup_connection();
   
    			console.log(conn);
    		}
    		else {
    			alert("You are currently connected to a game. Exit that session or play that session");
    		}
    	});
    	
    });
  });
function setup_connection () {
	console.log("Hi");
	conn = new WebSocket("ws://localhost:4567/matches");
	conn.onopen = function(event) {
		connected = true;
	};
	conn.onerror = err => {
  };

  // Display leaver message
  conn.onclose = function(e) {
  };

  conn.onmessage = msg => {
  		let mess;
  		const data = JSON.parse(msg.data);
    	switch (data.type) {
      	default:
        console.log('Unknown message type!', data.type);
        break;


        case MESSAGE_TYPE.JOINGAME:
        userName = data.userName;
        console.log(userName);
        console.log("finished!"); 
        mess = {"type": MESSAGE_TYPE.JOINGAME, "userName": userName};
        conn.send(JSON.stringify(mess));
        break;

        case MESSAGE_TYPE.STARTGAME:
        isBlack = data.isBlack;
        $("#turnStatus").show();
        $("#boardDiv").show();
		$("#chatting").show();
		$("#otherChatElements").show();
        console.log(isBlack);
        isTurn = data.isTurn;
        opponent = data.opponent;
    	$("#opponent").append("You are facing " + opponent + "!");
    	$("#opponentsTurnText").append("It is " + opponent +"'s  turn!");
        if (data.isTurn) {
        	$("#isTurnText").show();
            blink($("#isTurnText"));
        	$("#opponentsTurnText").hide();
    	}
    	else {
    		console.log("isnotturn");
    		$("#isTurnText").hide();
    		$("#opponentsTurnText").show();
            blink($("#opponentsTurnText"));
    	}
        drawBoard();
        break;

        case MESSAGE_TYPE.ISTURN:
        console.log("turn!");
         $("#isTurnText").show();
         blink($("#isTurnText"));
         $("#opponentsTurnText").hide();
        isTurn = true;
        break;

        case MESSAGE_TYPE.GETPOSSIBLEMOVES:
        clicked = true;
        possibleMoves = undefined;
        possibleMoves = data.cords;
        console.log(possibleMoves);
        let moves = possibleMoves;
        piece.xCord = data.pieceXCord;
        piece.yCord = data.pieceYCord;
        console.log(piece.xCord);
        console.log(piece.yCord);
        console.log(data.moveType);
        console.log(data.cords);
        moveType = data.moveType;
         drawPossibleMoves(moves);
        break;
        case MESSAGE_TYPE.UPDATEBOARD:
        console.log("updateBoard");
        let xStart = data.pieceXStart;
        let yStart = data.pieceYStart;
        let xEnd = data.pieceXEnd;
        let yEnd = data.pieceYEnd;
        visuallyMovePiece(xStart, yStart, xEnd, yEnd, data.isBlack);
        if (data.moveType ==="jump") {
        	removeOpponentPiece(xStart, yStart, xEnd, yEnd, data.isBlack);
        }
        break;
        case MESSAGE_TYPE.ENDTURN:
        console.log("endTurn");
 		$("#isTurnText").hide();
        $("#opponentsTurnText").show();
        blink($("#opponentsTurnText"));
       isTurn = false;
        break;
        case MESSAGE_TYPE.WINNER:
        gameOver =true;
        $("#winner").show();
        blink($("#winner"));
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
        blink( $("#forfeit"));
        blink( $("#winner"));
        $("#isTurnText").hide();
        $("#opponentsTurnText").hide();

        break;
        case MESSAGE_TYPE.MESSAGE:
        console.log("message receieved!");
        console.log(data.message);
        console.log(data.userName);
        console.log(data.isBlack);
        getMessage(data.message.toString(), data.userName.toString(), data.isBlack);
        break;
    }
   
  };
}
function getMessage(message, messageName, messageIsBlack) {
    let string; 
    let color;
    console.log(messageName);
    console.log(userName);
  if (userName === messageName) {
    console.log("I");
    string = "Me: " + message;
  }
  else {
    console.log("other");
    string = messageName + ": " + message;
  }
  console.log(string);
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
	console.log(moves);
	for (key in moves) {
		console.log("key " + key);
		let xCord = key;
		let currYCord;
		console.log(moves[key]);
		let yCords = moves[key];
		if (isBlack) {
			xCord = 7-xCord;
		}
		for (let i = 0; i<yCords.length; i ++) {
			currYCord = yCords[i];
			if (isBlack) {
				currYCord = 7-currYCord;
			}
			ctx.beginPath();
			ctx.lineWidth = 0;
			ctx.arc(xCord*TILE_SIZE + TILE_SIZE/2, currYCord*TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
			ctx.fillStyle = "brown";
			ctx.fill();
			ctx.closePath();
		}
	}
}
function drawPossibleMoves(moves) {
	console.log(moves);
	for (let key in moves) {
		let xCord = key;
		let yCords = moves[key];
		console.log("key" + key);
		if (isBlack) {
			xCord = 7-xCord;
		}
		console.log("value " + yCords);
		for (let i = 0; i<yCords.length; i ++) {
			console.log("x Cord " + xCord);
			let currYCord = yCords[i];
            if (isBlack) {
                currYCord = 7-currYCord;
            }
			console.log("yCord " + currYCord);
			console.log("xCord " + xCord);
			console.log("yCord " + currYCord);
			ctx.beginPath();
			let x = xCord*TILE_SIZE + TILE_SIZE/2;
			console.log("xcord drawing " + x);
			ctx.arc(xCord*TILE_SIZE + TILE_SIZE/2, currYCord*TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2-2, 0, 2*Math.PI);
			ctx.fillStyle = "green";
			ctx.fill();
			ctx.closePath();
		}
	}
}
function visuallyMovePiece(xStart, yStart, xEnd, yEnd, pieceIsBlack) {
	if (isBlack) {
		xStart = 7-xStart;
		yStart = 7-yStart;
		xEnd = 7-xEnd;
		yEnd = 7-yEnd;
	}
	ctx.beginPath();
	ctx.arc(xStart * TILE_SIZE + TILE_SIZE/2, yStart * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
	ctx.fillStyle = "brown";
	ctx.fill();
	ctx.closePath();
	ctx.beginPath();
	ctx.arc(xEnd * TILE_SIZE + TILE_SIZE/2, yEnd * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2-2, 0, 2*Math.PI);
	if (pieceIsBlack) {
		ctx.fillStyle = "black";

	}
	else {
		ctx.fillStyle = "#F5DEB3";
	}
	ctx.fill();
	ctx.closePath();

}
function removeOpponentPiece(xStart, yStart, xEnd, yEnd) {
	if (isBlack) {
		xStart = 7-xStart;
		yStart = 7-yStart;
		xEnd = 7-xEnd;
		yEnd = 7-yEnd;
	}
	removePieceX = (xStart + xEnd)/2;
	removePieceY = (yStart + yEnd)/2;
	ctx.beginPath();
	ctx.arc(removePieceX * TILE_SIZE + TILE_SIZE/2, removePieceY * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2, 0, 2*Math.PI);
	ctx.fillStyle = "brown";
	ctx.fill();
	ctx.closePath();
}
function boardClick(e) {
	if (!gameOver) {
		console.log(isTurn);
		let parentOffset = $(this).parent().offset();
		let xCord = Math.floor((e.pageX-parentOffset.left)/50);
		let yCord = Math.floor((e.pageY-parentOffset.top)/50);
		if (isBlack) {
			xCord = 7-xCord;
			yCord =  7-yCord;
		}
		console.log(xCord);
		console.log(yCord);
		if (isTurn && xCord>=0 && yCord >=0) {
			let mess;
			if (clicked) {
				clicked = false;
				console.log("send move message");
				mess = {"moveType": moveType, "type": MESSAGE_TYPE.MOVE, "pieceXCord": piece.xCord, "pieceYCord": piece.yCord, "moveToXCord": xCord, "moveToYCord": yCord, "userName": userName, "isBlack": isBlack};
				conn.send(JSON.stringify(mess));
			}
			else {

				mess = {"type": MESSAGE_TYPE.GETCORDS, "pieceXCord": xCord, "pieceYCord": yCord, "userName": userName, "isBlack": isBlack};
				console.log("message sent");
				conn.send(JSON.stringify(mess)); 
			}
		}
		if (possibleMoves !==undefined) {
			let moves = possibleMoves;
			console.log("erase");
			getRidOfPossibleMoves(moves);
			possibleMoves = undefined;
		}
	}

}
function drawBoard() {
	console.log("Here");
	$("#boardDiv").show();
	$(".loader").hide();
	$("#text").hide();

	canvas = $('#board')[0];
    canvas.width = BOARD_SIZE * TILE_SIZE;
    canvas.height = BOARD_SIZE * TILE_SIZE;
    canvas.onclick = boardClick;
    console.log(canvas.width);
    console.log(canvas.height);
    console.log("We're in");
    ctx = canvas.getContext("2d");
    ctx.lineWidth = 0;

    // set line color
    ctx.strokeStyle = 'black';

    for (let i = 0; i<8; i ++) {
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
    
    // ctx.stroke();
    console.log(ctx);
    console.log(canvas);
    addPieces();
}
function addPieces() {
	for (let i = 0; i<3; i ++) {
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
			// ctx.stroke();
			ctx.closePath();

		}
	}
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
			// ctx.stroke();
			ctx.closePath();


		}
	}
}
function signOut() {
	console.log("signOut");
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