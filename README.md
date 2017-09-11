# Checkers
Contributor: Justin Huang justin_huang@brown.edu
# Project Requirements
 	This project fulfills the basic requirements of an online multiplayer checkers game. A user must sign up for an account before they can play the game. A record is created for each player. After the user successfully signs up and signs in, they can enter the game lobby which automatically adds the current player to the next available game. Once the game starts, the player will be informed if it is their turn and if so they will be able to move the board by clicking on the piece they want to move followed by its destination. The win/loss status will be posted when the game is over as well as if applicable if the player forfeited. Once the game is over, each player’s record is updated.

# Project Specs
	When a user goes on the website a login page is shown. A user must sign up for an account, confirm their email via a confirmation email that is sent to them, then they can access the contents of the program. Sign up and authentication is handled by Firebase. For each account created an entry is created in the SQL Database which contains records for each user. 
	On the homepage the record of the player will be shown and a link to join a game is included. When the link is clicked the user joins the player lobby and a loading screen will appear when the user is waiting to join the game. When the user joins a game the chess board will appear and a message stating whose turn is will appear as well. 
	If it is the user’s turn, and they click on a piece they owe and the move can possibly move somewhere, its possible final destination will show but in green. Clicking anywhere else will erase it back to the dark blackish color. After a user sees the green areas, if the user, clicks on a green space, the piece will move. Depending on the type of move, the user may still be able to move again and repeat the process or it’s the opposing player's turn and whatever the user clicks will have no effect. The header alerts will be updated as well.
	When one of the users wins, the corresponding messages “You won” or “You lost” will appear as well as “You won by forfeit” if applicable. Forfeits are determined if a user exits a game before the game ends. 

# How to Run the Project
To build and run the project locally, in game.js replace  the line: conn = new WebSocket("ws://104.131.23.151/matches") with  conn = new WebSocket("ws://localhost:4566/matches"). In the main project directory, checkers,run mvn package. Once the project is compiled, ./run --gui from the command line (in the same directory) to run the project. To run the project over the server, go to http://104.131.23.151.

To run the JUnit tests, from the main directory, run mvn test. 
