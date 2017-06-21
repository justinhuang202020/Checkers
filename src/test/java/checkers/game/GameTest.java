package checkers.game;

import checkers.game.Board;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Created by Justin on 6/20/2017.
 */
public class GameTest {
    @Test
    public void testBoardSetUp() {
        Board board = new Board();
        Piece[][] boardcords = board.getBoard();
        assertTrue(boardcords[1][0]!=null);
        assertTrue(boardcords[1][0].isBlack());
        assertTrue(!boardcords[1][0].isKing());
        assertTrue(boardcords[1][0].getXCoordinate() == 1);
        assertTrue(boardcords[1][0].getYCoordinate() == 0);
        assertTrue(boardcords[3][0]!=null);
        assertTrue(boardcords[3][0].isBlack());
        assertTrue(!boardcords[3][0].isKing());
        assertTrue(boardcords[5][0]!=null);
        assertTrue(boardcords[5][0].isBlack());
        assertTrue(!boardcords[5][0].isKing());
        assertTrue(boardcords[7][0]!=null);
        assertTrue(boardcords[7][0].isBlack());
        assertTrue(!boardcords[7][0].isKing());
        assertTrue(boardcords[0][1]!=null);
        assertTrue(boardcords[0][1].isBlack());
        assertTrue(!boardcords[0][1].isKing());
        assertTrue(boardcords[0][1].getXCoordinate() == 0);
        assertTrue(boardcords[0][1].getYCoordinate() == 1);
        assertTrue(boardcords[2][1]!=null);
        assertTrue(boardcords[2][1].isBlack());
        assertTrue(!boardcords[2][1].isKing());
        assertTrue(boardcords[4][1]!=null);
        assertTrue(boardcords[4][1].isBlack());
        assertTrue(!boardcords[4][1].isKing());
        assertTrue(boardcords[6][1]!=null);
        assertTrue(boardcords[6][1].isBlack());
        assertTrue(!boardcords[6][1].isKing());
        assertTrue(boardcords[1][2]!=null);
        assertTrue(boardcords[1][2].isBlack());
        assertTrue(!boardcords[1][2].isKing());
        assertTrue(boardcords[3][2]!=null);
        assertTrue(boardcords[3][2].isBlack());
        assertTrue(!boardcords[3][2].isKing());
        assertTrue(boardcords[5][2]!=null);
        assertTrue(boardcords[5][2].isBlack());
        assertTrue(!boardcords[5][2].isKing());
        assertTrue(boardcords[7][2]!=null);
        assertTrue(boardcords[7][2].isBlack());
        assertTrue(!boardcords[7][2].isKing());
        assertTrue(boardcords[0][5]!=null);
        assertTrue(!boardcords[0][5].isBlack());
        assertTrue(!boardcords[0][5].isKing());
        assertTrue(boardcords[0][5].getXCoordinate() == 0);
        assertTrue(boardcords[0][5].getYCoordinate() == 5);
        assertTrue(boardcords[0][5]!=null);
        assertTrue(!boardcords[0][5].isBlack());
        assertTrue(!boardcords[0][5].isKing());
        assertTrue(boardcords[2][5]!=null);
        assertTrue(!boardcords[2][5].isBlack());
        assertTrue(!boardcords[2][5].isKing());
        assertTrue(boardcords[4][5]!=null);
        assertTrue(!boardcords[4][5].isBlack());
        assertTrue(!boardcords[4][5].isKing());
        assertTrue(boardcords[6][5]!=null);
        assertTrue(!boardcords[6][5].isBlack());
        assertTrue(!boardcords[6][5].isKing());
        assertTrue(boardcords[1][6]!=null);
        assertTrue(!boardcords[1][6].isBlack());
        assertTrue(!boardcords[1][6].isKing());
        assertTrue(boardcords[1][6].getXCoordinate() == 1);
        assertTrue(boardcords[1][6].getYCoordinate() == 6);
        assertTrue(boardcords[3][6]!=null);
        assertTrue(!boardcords[3][6].isBlack());
        assertTrue(!boardcords[3][6].isKing());
        assertTrue(boardcords[5][6]!=null);
        assertTrue(!boardcords[5][6].isBlack());
        assertTrue(!boardcords[5][6].isKing());
        assertTrue(boardcords[7][6]!=null);
        assertTrue(!boardcords[7][6].isBlack());
        assertTrue(!boardcords[7][6].isKing());
        assertTrue(boardcords[0][7]!=null);
        assertTrue(!boardcords[0][7].isBlack());
        assertTrue(!boardcords[0][7].isKing());
        assertTrue(boardcords[2][7]!=null);
        assertTrue(!boardcords[2][7].isBlack());
        assertTrue(!boardcords[2][7].isKing());
        assertTrue(boardcords[4][7]!=null);
        assertTrue(!boardcords[4][7].isBlack());
        assertTrue(!boardcords[4][7].isKing());
        assertTrue(boardcords[6][7]!=null);
        assertTrue(!boardcords[6][7].isBlack());
        assertTrue(!boardcords[6][7].isKing());
        assertTrue(boardcords[0][0] ==null);
        assertTrue(boardcords[2][0] ==null);
        assertTrue(boardcords[4][0] ==null);
        assertTrue(boardcords[6][0] ==null);
        assertTrue(boardcords[1][1] ==null);
        assertTrue(boardcords[3][1] ==null);
        assertTrue(boardcords[5][1] ==null);
        assertTrue(boardcords[7][1] ==null);
        assertTrue(boardcords[0][2] ==null);
        assertTrue(boardcords[2][2] ==null);
        assertTrue(boardcords[4][2] ==null);
        assertTrue(boardcords[6][2] ==null);
        for (int i = 3; i <5; i ++) {
            for (int j = 0; j<8; j++) {
                assertTrue(boardcords[j][i]==null);
            }
        }
        assertTrue(boardcords[1][5]==null);
        assertTrue(boardcords[3][5]==null);
        assertTrue(boardcords[5][5]==null);
        assertTrue(boardcords[7][5]==null);
        assertTrue(boardcords[0][6]==null);
        assertTrue(boardcords[2][6]==null);
        assertTrue(boardcords[4][6]==null);
        assertTrue(boardcords[6][6]==null);
        assertTrue(boardcords[1][7]==null);
        assertTrue(boardcords[3][7]==null);
        assertTrue(boardcords[5][7]==null);
        assertTrue(boardcords[7][7]==null);
    }
    @Test
    public void testInitialMovesBlack() {
        Board board = new Board();
        assertTrue(board.possibleMovesBlack(1, 0, board.search(1, 0)).size()==0);
        assertTrue(board.possibleMovesBlack(3, 0, board.search(3, 0)).size()==0);
        assertTrue(board.possibleMovesBlack(1, 2, board.search(1, 2)).get("normal").get(2) ==3);
        assertTrue(board.possibleMovesBlack(1, 2, board.search(1, 2)).get("normal").get(0) ==3);
        assertTrue(board.possibleMovesBlack(1, 2, board.search(1, 2)).get("normal").get(2) ==3);
        assertTrue(board.possibleMovesBlack(1, 2, board.search(1, 2)).get("normal").get(0) ==3);
        assertTrue(board.possibleMovesBlack(7, 2, board.search(7, 2)).get("normal").get(6) ==3);
    }
    @Test public void testInitialMovesRed() {
        Board board = new Board();
        assertTrue(board.possibleMovesRed(0, 7, board.search(0, 7)).size()==0);
        assertTrue(board.possibleMovesRed(1, 6, board.search(1, 6)).size()==0);
        assertTrue(board.possibleMovesRed(0, 5, board.search(0, 5)).get("normal").get(1) ==4);
        assertTrue(board.possibleMovesRed(0, 5, board.search(0, 5)).get("normal").size() == 1);
        assertTrue(board.possibleMovesRed(4, 5, board.search(4, 5)).get("normal").get(5) ==4);
        assertTrue(board.possibleMovesRed(4, 5, board.search(4, 5)).get("normal").get(3) ==4);
        assertTrue(board.possibleMovesRed(4, 5, board.search(4, 5)).get("normal").size() == 2);
    }
    @Test public void mockGame() {
        Player Justin = new Player();
        Player Jason = new Player();
        Game game = new Game();
        game.addPlayer(Justin);
        game.addPlayer(Jason);
        System.out.println(Justin.isBlack());
        System.out.println(Jason.isBlack());
    }
}
