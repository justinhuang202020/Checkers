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
	<!-- <div class = "container"> -->
	<div class = "loader">
	</div> <br>
	<!-- </div> -->
	<div id = "text">
		<h1>Loading...<br>Finding Game</h1>
	</div>
	</body>
	</html>