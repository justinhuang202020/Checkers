package edu.brown.cs.checkers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

/**
 * Created by Justin on 6/7/2017. handles the creation of a game and assigning
 * players to games
 */
public class Game {
	private Board board;
	private boolean startGame;
	private List<Player> playerList;
	private static int maxPlayers = 2;
	private boolean gameWon;
	private boolean redWon;
	private boolean blackWon;
	private Player currentTurn;
	private UUID gameID;

	public Game(UUID id) {
		gameID = id;
		gameWon = false;
		currentTurn = null;
		startGame = false;
		redWon = true;
		blackWon = true;
		playerList = new ArrayList<>();
		board = new Board();

	}

	public UUID getGameID() {
		return gameID;
	}

	public boolean maxPlayers() {
		if (playerList.size() == 2) {
			return true;
		}
		return false;
	}

	/**
	 * adds a player to the game and rejects the player if the max amount of
	 * players has been reached
	 * 
	 * @param player
	 */
	protected void addPlayer(Player player) {
		if (playerList.size() >= maxPlayers) {
			throw new IllegalArgumentException("Max players has been reached");
		}

		playerList.add(player);
		if (playerList.size() == maxPlayers) {
			startGame = true;
			this.randomizeOrder();
			this.assignColor();
			currentTurn = playerList.get(0);
			currentTurn.turnStarted();
			//tells the board to check possible moves of each piece
			board.updateMoves();
			playerList.get(0).setOpponent(playerList.get(1));
			playerList.get(1).setOpponent(playerList.get(0));
		}
	}
	/**
	 * randomizes the order of the players
	 */
	public void randomizeOrder() {
		Collections.shuffle(playerList);
	}
	/**
	 *assigns the color of each player
	 */
	public void assignColor() {
		playerList.get(0).setBlack();
		playerList.get(1).setRed();
	}

	/**
	 * checks if any of the pieces on each side are left
	 */
	private void checkWinStatus() {

		if (board.getBlackPiecesList().size() == 0) {
			gameWon = true;
			blackWon = false;
		} else if (board.getRedPiecesList().size() == 0) {
			gameWon = true;
			blackWon = true;
		}

	}

	/**
	 * tells the board to update the possible moves of all of the players
	 */
	protected void updateMoves() {
		board.updateMoves();
		this.checkWinStatus();
		if (gameWon) {
			if (blackWon) {
				System.out.println("black won!");
			} else {
				System.out.println("red won!");
			}
			for (int i = 0; i < playerList.size(); i++) {
				Player currPlayer = playerList.get(i);
				//if the currPlayer is black and black won
				if (currPlayer.isBlack() == blackWon) {
					currPlayer.setWinner();
				}
				else {
					currPlayer.setLoser();
				}
				currPlayer.gameEnded();
			}
		}
	}

	public boolean gameWon() {
		if (gameWon) {
			return true;
		}
		return false;
	}

	public List<Player> getPlayerList() {
		return ImmutableList.copyOf(playerList);
	}

	public Board getBoard() {
		return board;
	}

	public boolean gameStarted() {
		return startGame;
	}

	public boolean blackWon() {
		return blackWon;
	}

	public boolean redWon() {
		return redWon;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gameID);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Game)) {
			return false;
		}
		Game game = (Game) o;
		if (game.getGameID().equals(gameID)) {
			return true;
		}
		return false;
	}
}
