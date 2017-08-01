$(document).ready(function(){
	
 	firebase.auth().onAuthStateChanged(function(user) {
    	if (!user) {
    		window.location = "/login";
    	}
    	else {
    		let email = user.email;
    		let parameters = {email: email};
    		$.post('/getRecord', parameters, function (result){
    			var div = document.getElementById("content");
    			console.log(result);
    			let record = JSON.parse(result);
    			var h = document.createElement("H1");
    			var text = "Your record is " + record.record[0] + "-" + record.record[1];
    			console.log(text);               
				var t1 = document.createTextNode(text); 
				h.appendChild(t1);
				div.appendChild(h);   
				var b = document.createElement("B");
    			var label = document.createElement("LABEL");
    			var text = "Click Below To Start A Game!";
    			var textNode = document.createTextNode(text);
    			b.appendChild(textNode);
    			label.append(b); 
    			div.appendChild(label);
    			div.appendChild(document.createElement("BR"));
    			var button = document.createElement("BUTTON");
    			button.addEventListener("click", redirect, false); 
    			button.innerHTML = "start";
    			div.appendChild(button);
    		});
    	}
    });
});
function redirect() {
	window.location = "/game";
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