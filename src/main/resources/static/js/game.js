let conn;
let userName;
let isBlack;
let userEmail;
const MESSAGE_TYPE = {
  JOINGAME: 0,
  STARTGAME: 1

  // START: 3,
  // JOIN: 3,
  // WINNER: 4,
  // LOSER: 5,
  // VALID_ACTIONS: 6,
  // ERROR: 7,
  // MOVE: 8,
  // MESSAGE: 9,
  // PLAYER_INFORMATION: 10,
  // PING: 11,
  // LEAVER: 12,
};

$(document).ready(function(){
	
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
		console.log("Yay!!!!");
	};
	conn.onerror = err => {
    console.log('Connection error:', err);
  };

  // Display leaver message
  conn.onclose = function(e) {
  console.log("yooooo");
  };

  conn.onmessage = msg => {
  		let mess;
  		const data = JSON.parse(msg.data);
    	switch (data.type) {
      	default:
        console.log('Unknown message type!', data.type);
        break;
        case MESSAGE_TYPE.REQUESTEMAIL:
         console.log("recieved");
         mess = {"type": MESSAGE_TYPE.GETUSERNAME, "email": userEmail};
         conn.send(JSON.stringify(mess));
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
        console.log(isBlack);
        break;



    }
  };
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