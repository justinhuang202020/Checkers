package edu.brown.cs.checkers.game;

import java.util.Objects;

/**
 * Created by Justin on 6/13/2017.
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

	public Player(String userName) {
		jumpMove = false;
		forfeit = false;
		isTurn = false;
		startGame = false;
		jumpPiece = null;
		opponent = null;
		name = userName;

	}

	public String userName() {
		return name;
	}

	protected void setBlack() {
		isBlack = true;
	}

	protected void setRed() {
		isBlack = false;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public boolean isTurn() {
		return isTurn;
	}

	protected void turnStarted() {
		System.out.println("turn started");
		isTurn = true;
	}

	protected void setGameHasStarted() {
		startGame = false;
	}

	protected boolean hasGameStarted() {
		return startGame;
	}

	protected void gameEnded() {
		isTurn = false;
	}

	protected void turnEnded() {
		System.out.println("player ended turn");
		jumpPiece = null;
		isTurn = false;
		jumpMove = false;
		opponent.turnStarted();
	}
	// protected void getMovablePieces(Board board) {
	//
	// }

	protected void move(Piece currPiece, int xStart, int yStart, int xEnd, int yEnd, Board board) {
		if (!isTurn) {
			throw new IllegalArgumentException("is not your turn");
		}
		if (jumpMove && currPiece != jumpPiece) {
			throw new IllegalArgumentException("cannot move piece");
		}
		if (board.validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
			if (board.move(xStart, yStart, xEnd, yEnd, currPiece)) {
				System.out.println("line 75 move again");
				jumpPiece = currPiece;
				jumpMove = true;
			} else {
				System.out.println("turn ended line 80");
				this.turnEnded();
			}
		} else {
			throw new IllegalArgumentException("move is invalid");
		}

	}

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
