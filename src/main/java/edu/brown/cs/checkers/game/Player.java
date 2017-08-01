package edu.brown.cs.checkers.game;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Justin on 6/13/2017.
 */
public class Player {
    private boolean isBlack;
    private boolean isTurn;
    private boolean startGame;
    private Piece clicked;
    private boolean jumpMove;
    private Piece jumpPiece;
    private Player  opponent;
    private String name;

    public Player(String userName) {
        jumpMove = false;
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
        isTurn = true;
    }
    protected void setGameHasStarted() {
        startGame = false;
    }
    protected  boolean hasGameStarted() {
        return startGame;
    }
    protected void gameEnded() {
    	isTurn = false;
    }
    protected void turnEnded() {
    	jumpPiece = null;
        isTurn = false;
        jumpMove = false;
        opponent.turnStarted();
    }
//    protected void getMovablePieces(Board board) {
//
//    }

    protected void move(Piece currPiece, int xStart, int yStart, int xEnd, int yEnd, Board board) {
    	System.out.println("start");
    	if (!isTurn) {
    		throw new IllegalArgumentException ("is not your turn");
    	}
        if (jumpMove && currPiece != jumpPiece) {
            throw new IllegalArgumentException("cannot move piece");
        }
        System.out.println("before");
        if (board.validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
        	System.out.println("valid");
            if (board.move(xStart, yStart, xEnd, yEnd, currPiece)) {
            	jumpPiece = currPiece;
            	jumpMove = true;
            }
            else {
            	this.turnEnded();
            }
        }
        else {
        	throw new IllegalArgumentException("move is invalid");
        }
     

    }
    protected void setOpponent(Player player) {
    	opponent = player;
    }
    protected boolean jumpAgain() {return jumpMove;};
//    protected boolean containMovablePiece (int x, int y,  Board board) {
//        if (isBlack) {
//
//        }
//        if (isBlack) {
//
//        }
//    }
}
