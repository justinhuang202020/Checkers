<!DOCTYPE html>		
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"
	integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
	crossorigin="anonymous"></script>
		<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<!-- jQuery library -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
	<script src="https://www.gstatic.com/firebasejs/4.1.3/firebase.js"></script>
	<script src="js/login.js"></script>
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
	<TITLE>MMO Checkers!</TITLE>
	<h1>Welcome to MMO Checkers!</h1>
	 <link rel="stylesheet" href="css/login.css">
  <div class="imgcontainer">
    <img src="https://www.northshire.com/sites/northshire.com/files/0679952503220.jpg" alt="Avatar" class="avatar">
  </div>
  <div id = "options">
  <button id = "loginOption" class = "options" onclick="switchToLogin()">login </button>
  <button id = "signupOption" class = "options" onclick = "switchToSignUp()">signup</button>
  </div>
  <div class="container" id = "login">
    <label><b>Email</b></label>
    <input id = "loginEmail" type="email" placeholder="Enter Email" name="uname" required>

    <label><b>Password</b></label>
    <input  id = "loginPassword" type="password" placeholder="Enter Password" name="psw" required>

    <button type="submit"  class = "submit" id = "submitLogin">Login</button>
   
  </div>
  <div class="container" id = "signup">
    <label><b>First Name</b></label>
    <input id = "firstName" type= "text" placeholder="Enter First Name" name="uname" required>
    <label><b>Last Name</b></label>
    <input id = "lastName" type="text" placeholder="Enter Last Name" name="uname" required>
    <label><b>userName</b></label>
    <input id = "userName" type="text" placeholder="Enter User Name" name="uname" required>
    <label><b>Email</b></label>
    <input id = "signupEmail" type="email" placeholder="Enter Email" name="uname" required>
    <label><b>Confirm Email</b></label>
    <input id = "confirmEmail" type="email" placeholder="Confirm" name="uname" required>
    <label><b>Password</b></label>
    <input id = "signupPassword" type="password" placeholder="Enter Password" name="psw" required>
    <label><b>confirm Password</b></label>
    <input id = "confirmPassword" type="password" placeholder="Enter Password" name="psw" required>

    <button type="submit"  class = "submit" id = "submitSignUp">Signup</button>
   
  </div>
  <div class="container" id = "forgotPasswordField">
    <label><b>Enter email and we will send you instructions to reset email</b></label>
    <input id = "forgotPasswordEmail" type="email" placeholder="Enter Email" name="uname" required>

    <button type="submit"  class = "submit" id = "submitForgotPassword">Send Email</button>
   
  </div>

  <div class="container" style="background-color:#f1f1f1" id = "forgotPassword">
    <span class="psw">Forgot <a href = "#" onclick = "forgotPassword()">password?</a></span>
  </div>

</html>