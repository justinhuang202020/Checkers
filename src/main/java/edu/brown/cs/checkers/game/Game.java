package edu.brown.cs.checkers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Created by Justin on 6/7/2017.
 */
public class Game {
    private Board board;
    private int turnCounter;
    private boolean startGame;
    private List<Player> playerList;
    private static int maxPlayers = 2;
    private boolean gameWon;
    private boolean redWon;
    private boolean blackWon;
    private boolean turnEnded;
    private Player currentTurn;
    private UUID gameID;


    public Game(UUID id) {
        turnEnded = false;
        gameID = id;
        turnCounter = 0;
        gameWon = false;
        currentTurn = null;
        startGame = false;
        redWon =true;
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
            board.updateMoves();
            playerList.get(0).setOpponent(playerList.get(1));
            playerList.get(1).setOpponent(playerList.get(0));
        }
    }
    public void randomizeOrder() {
        Collections.shuffle(playerList);
    }
    public void assignColor() {
        playerList.get(0).setBlack();
        playerList.get(1).setRed();
    }
    public void nextTurn() {
        assert(startGame);
        if (!currentTurn.isTurn()) {
        	if (currentTurn == playerList.get(0)) {
    		   currentTurn = playerList.get(1);
        	}
        	else {
        		currentTurn = playerList.get(0);
        	}
        	currentTurn.turnStarted();
    	   
       }
    }
    private void checkWinStatus() {
       
    	if (board.getBlackPiecesList().size() == 0) {
    		gameWon =true;
    		blackWon = true;
    	}
    	else if (board.getRedPiecesList().size() ==0) {
    		gameWon = true;
    		blackWon = true;
    	}
        
    }
    protected void updateMoves() {
    	board.updateMoves();
    	this.checkWinStatus();
    	if (gameWon) {
    		if (blackWon) {
    			System.out.println("black won!");
    		}
    		else {
    			System.out.println("red won!");
    		}
    		for (int i = 0; i < playerList.size(); i ++) {
    			playerList.get(i).gameEnded();;
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
    	return redWon();
    }
}
