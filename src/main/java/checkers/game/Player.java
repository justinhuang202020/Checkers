package checkers.game;

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


    public Player() {
        jumpMove = false;
        isTurn = false;
        startGame = false;
        jumpPiece = null;

    }
    public void setBlack() {
        isBlack = true;
    }
    public void setRed() {
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
    protected void turnEnded() {
        isTurn = false;
    }
//    protected void getMovablePieces(Board board) {
//
//    }

    protected void move(Piece currPiece, int xStart, int yStart, int xEnd, int yEnd, Board board) {
        if (jumpMove && currPiece != jumpPiece) {
            throw new IllegalArgumentException("cannot move piece");
        }
        if (board.validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
            board.move(xStart, yStart, xEnd, yEnd, currPiece);
        }


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
