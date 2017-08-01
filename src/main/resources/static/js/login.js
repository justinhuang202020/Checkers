var color;
function switchToLogin() {
	let $login = $("#loginOption");
	let $signup = $("#signupOption");
	// console.log($login.css('backgroundColor').toString());
	$login.css('background-color', '#696969');
	$signup.css('background-color', '#C0C0C0');
	$("#signup").hide();
	$("#login").show();
	$("#forgotPassword").show();
	$("#forgotPasswordField").hide();
}
	
function switchToSignUp() {
	let $login = $("#loginOption");
	let $signup = $("#signupOption");
	$("#login").hide();
	$signup.css('background-color', '#696969');
	$login.css('background-color', '#C0C0C0');
	$("#login").hide();
	$("#forgotPassword").hide();
	$("#signup").show();
	$("#forgotPasswordField").hide();

	// console.log($signup.css('backgroundColor').toString());
}
function forgotPassword() {
		let $login = $("#loginOption");
	let $signup = $("#signupOption");
	$("#login").hide();
	$("#forgotPassword").hide();
	$("#forgotPasswordField").show();
	$signup.css('background-color', '#C0C0C0');
	$login.css('background-color', '#C0C0C0');

}
$(document).ready(function() {
	$("#signup").hide();
	$("#forgotPasswordField").hide();
	
  	firebase.auth().onAuthStateChanged(function(user) {
  		if (user && user.emailVerified) {
  			window.location = "/";					
  		} else {
  			// document.querySelector('#reset').addEventListener('keypress', function (e) {
  			// 	console.log("Hi");
  			// 	var key = e.which || e.keyCode;
  			// 	if (key === 13) {
  			// 		passwordReset(e);
  			// 	}
  			// });
  			
  			
  			$('#submitLogin').on('click', function(e){
  				e.preventDefault();
  				login();
  			});
  			$('#submitSignUp').on('click', function(e){
  				e.preventDefault();
  				createAccount();
  			});
  			$('#submitForgotPassword').on('click', function(e){
  				e.preventDefault();
  				passwordReset();
  			});
  		}

  	});
  });

	function passwordReset() {
		
		let email = $("#forgotPasswordEmail").val();
		console.log(email);
		let  auth = firebase.auth();

		auth.sendPasswordResetEmail(email).then(function() {

			window.location = "/";
		}, function(error) {
			console.log(error);
			alert(error);
		});
	}
	


function login() {
			console.log("logging in...");
			let email = $("#loginEmail").val().trim();
			let password = $("#loginPassword").val();
			firebase.auth().signInWithEmailAndPassword(email, password).then(function() {
				if (firebase.auth().currentUser.emailVerified) {
					window.location = "/";
				}
				else {
					alert("Email has not been verified");
					firebase.auth().signOut().then(function() {
  // Sign-out successful.
}).catch(function(error) {
	alert(error.message);
});
}
}, function(error) {
	var errorCode = error.code;
	var errorMessage = error.message;
	alert(errorMessage);
  // ...
});

		}
function createAccount() {

	//extract the values from the submit forms
	// Get the value from a dropdown select directly
	let firstName = $("#firstName").val().trim();
	let lastName = $("#lastName").val().trim();
	let email = $( "#signupEmail" ).val().trim();
	let confirmEmail = $("#confirmEmail").val().trim();
	let pass = $( "#signupPassword" ).val();
	let confirmPass = $("#confirmPassword").val();
	let userName = $("#userName").val();

	if (pass ===confirmPass && email ===confirmEmail && firstName.length!==0 && lastName.length!==0 && userName.length !==0) {

	//do the stuff with Firebase locally and not with the server? perhaps not
	firebase.auth().createUserWithEmailAndPassword(email, pass).then(function() {
		sendEmailVerification(email, firstName, lastName, userName);

	}, function(error) {

		var errorCode = error.code;
		var errorMessage = error.message;
		alert(errorMessage);

		console.log(errorMessage);

	});
}
else {
	alert("email or password doesn't match or a field is blank");
	}
}
function sendEmailVerification(email, firstName, lastName, userName) {
    	var user = firebase.auth().currentUser;
      	user.sendEmailVerification().then(function() {
      		addToDatabase(email, firstName, lastName, userName);
      	alert("Email verification sent. Verify your email then sign in");
		}, function(error) {

			user.delete().then(function() {
			alert("email verifcation failed. Please sign up again");
		}, function(error) {
		alert("Unfortunately there has been an internal error. Please sign up with a different email or call customer service 1800-Checker");
	});

});
}
function addToDatabase(email, firstName, lastName, userName) {

	let parameters = {email: email, firstName: firstName,  lastName: lastName, userName: userName};

	$.post('/createPlayer', parameters, function (success){
		console.log(success);
		let result = JSON.parse(success);
		console.log(result.success);
		if (!result.success) {
			
			alert("Error: either user name exists or there's an internal error. Please sign up again");
			let user = firebase.auth().currentUser;


			user.delete().then(function() {
			}, function(error) {
				console.log("error3");
				alert("Unfortunately there has been an internal error. Please sign up with a different email or call customer service 1800-Checker");
			});
		}
		else {
			firebase.auth().signOut().then(function() {
				window.location = "/";
  		// Sign-out successful.
  	}).catch(function(error) {
  		alert(error.message);
  	});
		}
	});

			
  }

