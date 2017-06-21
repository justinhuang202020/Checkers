package checkers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Justin on 6/7/2017.
 */
public class Game {
    private Board board;
    private int turnCounter;
    private boolean startGame;
    List<Player> playerList;
    private static int maxPlayers = 2;
    private boolean gameWon;
    private boolean redWon;
    private boolean blackWon;
    private boolean turnEnded;


    public Game() {
        turnEnded = false;
        turnCounter = 0;
        gameWon = false;
        startGame = true;
        redWon =true;
        blackWon = true;
        playerList = new ArrayList<>();
        board = new Board();

    }
    private boolean canStartGame() {
        if (startGame) {
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
       if (turnEnded) {

           if (turnCounter % 2 == 0) {
               playerList.get(0).turnStarted();
               turnEnded = false;
           } else {
               playerList.get(1).turnStarted();
               turnEnded = false;
           }
           turnCounter++;
       }
    }
    private void checkWinStatus() {
        for (int i = 0; i<playerList.size(); i ++) {
            if (board.getBlackPiecesList().size() == 0) {
                gameWon =true;
                blackWon = true;
            }
            else if (board.getRedPiecesList().size() ==0) {
                gameWon = true;
                blackWon = true;
            }
        }
    }
    public boolean gameWon() {
        if (gameWon) {
            return true;
        }
        return false;
    }
}
