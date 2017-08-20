package edu.brown.cs.checkers.game;

import java.util.Objects;

/**
 * Created by Justin on 6/13/2017.
 * Handles each player in a checkers game. Moving power is given to the player
 * as the move is legal
 */
public class Player {
	private boolean isBlack;
	private boolean isTurn;
	private boolean startGame;
	private boolean jumpMove;
	private Piece jumpPiece;
	private Player opponent;
	private String name;
	private boolean forfeit;
	private boolean won;
	private boolean loss;
	private boolean gameEnded;

	public Player(String userName) {
		gameEnded = false;
		jumpMove = false;
		forfeit = false;
		isTurn = false;
		startGame = false;
		jumpPiece = null;
		opponent = null;
		name = userName;
		won = false;
		loss = false;

	}

	public String userName() {
		return name;
	}
	/**
	 *sets the player to black
	 */
	protected void setBlack() {
		isBlack = true;
	}
/**
 *sets the player to red
 */
	protected void setRed() {
		isBlack = false;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public boolean isTurn() {
		return isTurn;
	}
	protected void setWinner() {
		won = true;
	}
	protected void setLoser() {
		loss = true;
	}
	public boolean isWinner() {
		return won;
	}
	public boolean isLoser() {
		return loss;
	}
	protected void turnStarted() {
		isTurn = true;
	}

	protected void setGameHasStarted() {
		startGame = true;
	}

	protected boolean hasGameStarted() {
		return startGame;
	}
	public boolean hasGameEnded() {
		return gameEnded;
	}
	protected void gameEnded() {
		gameEnded = true;
		isTurn = false;
	}
/**
 * resets the variables and tells the opponent player that it is there turn 
 */
	protected void turnEnded() {
		jumpPiece = null;
		isTurn = false;
		jumpMove = false;
		opponent.turnStarted();
	}
/**
 *Player moves their piece if it is legal
 * @param currPiece
 * @param xStart
 * @param yStart
 * @param xEnd
 * @param yEnd
 * @param board
 */
	protected void move(Piece currPiece, int xStart, int yStart, int xEnd, int yEnd, Board board) {
		if (!isTurn) {
			throw new IllegalArgumentException("is not your turn");
		}
		// if the move is a second jump move, make sure the current piece is the exact same piece as the last move 
		if (jumpMove && currPiece != jumpPiece) {
			throw new IllegalArgumentException("cannot move piece");
		}
		if (board.validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
			//if board.move() returns true which means that there is still another move
			if (board.move(xStart, yStart, xEnd, yEnd, currPiece)) {
				//assigns the currPiece to jumpPiece so it can be referenced next time the player moves in case of a jump move
				jumpPiece = currPiece;
				jumpMove = true;
			} else {
				this.turnEnded();
			}
		} else {
			throw new IllegalArgumentException("move is invalid");
		}

	}
/**
 * sets the opponent 
 * @param player
 */
	protected void setOpponent(Player player) {
		opponent = player;
	}

	protected Player opponent() {
		return opponent;
	}

	protected boolean jumpAgain() {
		return jumpMove;
	};
	public boolean hasForfeit() {
		return forfeit;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
 	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}
		Player otherPlayer = (Player) o;
		if (otherPlayer.userName().equals(name)) {
			return true;
		}
		return false;
	}
	protected void forfeit() {
		forfeit = true;
	}
}
