<!DOCTYPE html>		
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"
	integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
	crossorigin="anonymous"></script>
		<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
     <link rel="stylesheet" href="css/home.css">
		<!-- jQuery library -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
	<script src="https://www.gstatic.com/firebasejs/4.1.3/firebase.js"></script>
	<script src="js/home.js"></script>
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
<div id = "content">
 <center><h1>Welcome to MMO Checkers!</h1></center>
 </div>
 <style>
/*h1 {text-align:center}*/
button{text-align: center}
label{text-align: center}
</style>
</html>