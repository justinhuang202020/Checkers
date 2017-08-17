<!DOCTYPE html>		
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"
	integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
	crossorigin="anonymous"></script>
	<script src="js/game.js"></script>
		<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<!-- jQuery library -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	  <link rel="stylesheet" href="css/game.css">
	<script src="https://www.gstatic.com/firebasejs/4.1.3/firebase.js"></script>
<script>
  // Initialize Firebase
  var config = {
    	apiKey: "AIzaSyBkLLymXvSPFY9UVvYhhaXbwOX7ofcXhcA",
    	authDomain: "mmo-checkers.firebaseapp.com",
    	databaseURL: "https://mmo-checkers.firebaseio.com",
    	projectId: "mmo-checkers",
    	storageBucket: "mmo-checkers.appspot.com",
    	messagingSenderId: "424431155329"
  		};
  	firebase.initializeApp(config);
	</script>
	<body>
	<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="/">MMO Checkers</a>
    </div>
    <ul class="nav navbar-nav">
      <li><a href="/game">Join New Game</a></li>
      <li><a href = "" id = "signout" onclick = "signOut()">Sign Out</a></li>
    </ul>
  </div>
</nav>
	<h1 id = "opponent"></h1>
	<h1 id = "winner">You won!</h1>
	<h1 id = "forfeit">You won by forfeit!</h1>
	<h1 id = "loser">You lost!</h1>
	<div id - "container">
	<center><div class = "loader">
	</div></center> <br>
	<div id = "text">
	<center><h1 id = "loading">Loading...<br>Finding Game</h1></center>
	</div>
	</div>
	<div id = "boardDiv">
		<canvas id = "board"  style="border:1px solid" ></canvas>
		<div id = "turnStatus">
		<h1 id = "isTurnText">It's your turn!</h1>
		<h1 id = "opponentsTurnText"></h1>
		</div>
	</div>
	 <div class="col-sm-2 sidenav" id = "leftNav">
	<div id="chatting" class="list-group">
    </div>
    <div id = "otherChatElements">
    <textarea name="message" value="" id="messageField"
     placeholder="Enter Message!"></textarea><br>
    <Button id = "chatButton" >Send</Button>
    </div>
    </div>
	</body>
	</html>