package edu.brown.cs.checkers.game;

import org.junit.Test;

import edu.brown.cs.checkers.game.Board;
import edu.brown.cs.checkers.game.Game;
import edu.brown.cs.checkers.game.Piece;
import edu.brown.cs.checkers.game.Player;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

/**
 * Created by Justin on 6/20/2017.
 */
public class GameTest {
	@Test
	public void testBoardSetUp() {
		Board board = new Board();
		Piece[][] boardcords = board.getBoard();
		assertTrue(boardcords[0][0] != null);
		assertTrue(boardcords[0][0].isBlack());
		assertTrue(!boardcords[0][0].isKing());
		assertTrue(boardcords[0][0].getXCoordinate() == 0);
		assertTrue(boardcords[0][0].getYCoordinate() == 0);
		assertTrue(boardcords[2][0] != null);
		assertTrue(boardcords[2][0].isBlack());
		assertTrue(!boardcords[2][0].isKing());
		assertTrue(boardcords[4][0] != null);
		assertTrue(boardcords[4][0].isBlack());
		assertTrue(!boardcords[4][0].isKing());
		assertTrue(boardcords[6][0] != null);
		assertTrue(boardcords[6][0].isBlack());
		assertTrue(!boardcords[6][0].isKing());
		assertTrue(boardcords[1][1] != null);
		assertTrue(boardcords[1][1].isBlack());
		assertTrue(!boardcords[1][1].isKing());
		assertTrue(boardcords[1][1].getXCoordinate() == 1);
		assertTrue(boardcords[1][1].getYCoordinate() == 1);
		assertTrue(boardcords[3][1] != null);
		assertTrue(boardcords[3][1].isBlack());
		assertTrue(!boardcords[3][1].isKing());
		assertTrue(boardcords[5][1] != null);
		assertTrue(boardcords[5][1].isBlack());
		assertTrue(!boardcords[5][1].isKing());
		assertTrue(boardcords[7][1] != null);
		assertTrue(boardcords[7][1].isBlack());
		assertTrue(!boardcords[7][1].isKing());
		assertTrue(boardcords[0][2] != null);
		assertTrue(boardcords[0][2].isBlack());
		assertTrue(!boardcords[0][2].isKing());
		assertTrue(boardcords[2][2] != null);
		assertTrue(boardcords[2][2].isBlack());
		assertTrue(!boardcords[2][2].isKing());
		assertTrue(boardcords[4][2] != null);
		assertTrue(boardcords[4][2].isBlack());
		assertTrue(!boardcords[4][2].isKing());
		assertTrue(boardcords[6][2] != null);
		assertTrue(boardcords[6][2].isBlack());
		assertTrue(!boardcords[6][2].isKing());
		assertTrue(boardcords[1][5] != null);
		assertTrue(!boardcords[1][5].isBlack());
		assertTrue(!boardcords[1][5].isKing());
		assertTrue(boardcords[1][5].getXCoordinate() == 1);
		assertTrue(boardcords[1][5].getYCoordinate() == 5);
		assertTrue(boardcords[1][5] != null);
		assertTrue(!boardcords[3][5].isBlack());
		assertTrue(!boardcords[3][5].isKing());
		assertTrue(boardcords[3][5] != null);
		assertTrue(!boardcords[3][5].isBlack());
		assertTrue(!boardcords[3][5].isKing());
		assertTrue(boardcords[5][5] != null);
		assertTrue(!boardcords[5][5].isBlack());
		assertTrue(!boardcords[5][5].isKing());
		assertTrue(boardcords[7][5] != null);
		assertTrue(!boardcords[7][5].isBlack());
		assertTrue(!boardcords[7][5].isKing());
		assertTrue(boardcords[0][6] != null);
		assertTrue(!boardcords[0][6].isBlack());
		assertTrue(!boardcords[0][6].isKing());
		assertTrue(boardcords[2][6].getXCoordinate() == 2);
		assertTrue(boardcords[2][6].getYCoordinate() == 6);
		assertTrue(boardcords[2][6] != null);
		assertTrue(!boardcords[2][6].isBlack());
		assertTrue(!boardcords[2][6].isKing());
		assertTrue(boardcords[4][6] != null);
		assertTrue(!boardcords[4][6].isBlack());
		assertTrue(!boardcords[4][6].isKing());
		assertTrue(boardcords[6][6] != null);
		assertTrue(!boardcords[6][6].isBlack());
		assertTrue(!boardcords[6][6].isKing());
		assertTrue(boardcords[1][7] != null);
		assertTrue(!boardcords[1][7].isBlack());
		assertTrue(!boardcords[1][7].isKing());
		assertTrue(boardcords[3][7] != null);
		assertTrue(!boardcords[3][7].isBlack());
		assertTrue(!boardcords[3][7].isKing());
		assertTrue(boardcords[5][7] != null);
		assertTrue(!boardcords[5][7].isBlack());
		assertTrue(!boardcords[5][7].isKing());
		assertTrue(boardcords[7][7] != null);
		assertTrue(!boardcords[7][7].isBlack());
		assertTrue(!boardcords[7][7].isKing());
		assertTrue(boardcords[1][0] == null);
		assertTrue(boardcords[3][0] == null);
		assertTrue(boardcords[5][0] == null);
		assertTrue(boardcords[7][0] == null);
		assertTrue(boardcords[0][1] == null);
		assertTrue(boardcords[2][1] == null);
		assertTrue(boardcords[4][1] == null);
		assertTrue(boardcords[6][1] == null);
		assertTrue(boardcords[1][2] == null);
		assertTrue(boardcords[3][2] == null);
		assertTrue(boardcords[5][2] == null);
		assertTrue(boardcords[7][2] == null);
		for (int i = 3; i < 5; i++) {
			for (int j = 0; j < 8; j++) {
				assertTrue(boardcords[j][i] == null);
			}
		}
		assertTrue(boardcords[0][5] == null);
		assertTrue(boardcords[2][5] == null);
		assertTrue(boardcords[4][5] == null);
		assertTrue(boardcords[6][5] == null);
		assertTrue(boardcords[1][6] == null);
		assertTrue(boardcords[3][6] == null);
		assertTrue(boardcords[5][6] == null);
		assertTrue(boardcords[7][6] == null);
		assertTrue(boardcords[0][7] == null);
		assertTrue(boardcords[2][7] == null);
		assertTrue(boardcords[4][7] == null);
		assertTrue(boardcords[6][7] == null);
	}

	@Test
	public void testInitialMovesBlack() {
		Board board = new Board();
		assertTrue(board.possibleMovesBlack(0, 0, board.search(0, 0)).size() == 0);
		assertTrue(board.possibleMovesBlack(2, 0, board.search(2, 0)).size() == 0);
		assertTrue(board.possibleMovesBlack(0, 2, board.search(0, 2)).get("normal").get(1).contains(3));
		assertTrue(board.possibleMovesBlack(6, 2, board.search(6, 2)).get("normal").get(5).contains(3));
		assertTrue(board.possibleMovesBlack(6, 2, board.search(6, 2)).get("normal").get(7).contains(3));
	}

	@Test
	public void testInitialMovesRed() {
		Board board = new Board();
		assertTrue(board.possibleMovesRed(7, 7, board.search(7, 7)).size() == 0);
		assertTrue(board.possibleMovesRed(0, 6, board.search(0, 6)).size() == 0);
		assertTrue(board.possibleMovesRed(1, 5, board.search(1, 5)).get("normal").get(0).contains(4));
		assertTrue(board.possibleMovesRed(1, 5, board.search(1, 5)).get("normal").get(2).contains(4));
		assertTrue(board.possibleMovesRed(1, 5, board.search(1, 5)).get("normal").size() == 2);
		assertTrue(board.possibleMovesRed(7, 5, board.search(7, 5)).get("normal").get(6).contains(4));
		assertTrue(board.possibleMovesRed(7, 5, board.search(7, 5)).get("normal").size() == 1);
	}

	@Test
	public void mockGame() {
		Player Justin = new Player("jhuang11");
		Player Jason = new Player("JJ");
		Game game = new Game(UUID.randomUUID());
		game.addPlayer(Justin);
		assertTrue(!game.gameStarted());
		game.addPlayer(Jason);
		assertTrue(game.gameStarted());
		Player player1 = game.getPlayerList().get(0);
		Player player2 = game.getPlayerList().get(1);
		Board board = game.getBoard();
		player1.move(board.search(0, 2), 0, 2, 1, 3, board);
		assertTrue(!player1.isTurn());
		assertTrue(player2.isTurn());
		assertTrue(board.search(0, 2) == null);
		Piece currPiece = board.search(1, 3);
		assertTrue(currPiece.getXCoordinate() == 1);
		assertTrue(currPiece.getYCoordinate() == 3);
		game.updateMoves();
		player2.move(board.search(1, 5), 1, 5, 2, 4, board);
		assertTrue(player1.isTurn());
		assertTrue(!player2.isTurn());
		assertTrue(board.search(1, 5) == null);
		currPiece = board.search(2, 4);
		assertTrue(currPiece.getXCoordinate() == 2);
		assertTrue(currPiece.getYCoordinate() == 4);
		game.updateMoves();
		player1.move(board.search(2, 2), 2, 2, 3, 3, board);
		assertTrue(!player1.isTurn());
		assertTrue(player2.isTurn());
		assertTrue(board.search(2, 2) == null);
		currPiece = board.search(3, 3);
		assertTrue(currPiece.getXCoordinate() == 3);
		assertTrue(currPiece.getYCoordinate() == 3);
		game.updateMoves();
		currPiece = board.search(2, 4);
		assertTrue(board.getMovablePiecesRed().containsKey(board.search(2, 4)));
		assertTrue(board.getMovablePiecesRed().get(board.search(2, 4)).containsKey("jump"));
		assertTrue(board.getMovablePiecesRed().get(board.search(2, 4)).get("jump").get(0).contains(2));
		player2.move(currPiece, 2, 4, 0, 2, board);
		assertTrue(player1.isTurn());
		assertTrue(!player2.isTurn());
		assertTrue(board.search(2, 4) == null);
		assertTrue(board.search(1, 3) == null);
		assertTrue(currPiece.getXCoordinate() == 0);
		assertTrue(currPiece.getYCoordinate() == 2);
		assertTrue(board.getBlackPiecesList().size() == 11);
		game.updateMoves();
		currPiece = board.search(1, 1);
		player1.move(currPiece, 1, 1, 2, 2, board);
		assertTrue(!player1.isTurn());
		assertTrue(player2.isTurn());
		assertTrue(board.search(1, 1) == null);
		assertTrue(board.search(2, 2) != null);
		assertTrue(currPiece.getXCoordinate() == 2);
		assertTrue(currPiece.getYCoordinate() == 2);
		game.updateMoves();
		currPiece = board.search(3, 5);
		player2.move(currPiece, 3, 5, 2, 4, board);
		assertTrue(player1.isTurn());
		assertTrue(!player2.isTurn());
		assertTrue(board.search(3, 5) == null);
		assertTrue(board.search(2, 4) != null);
		assertTrue(currPiece.getXCoordinate() == 2);
		assertTrue(currPiece.getYCoordinate() == 4);
		game.updateMoves();
		currPiece = board.search(3, 3);
		player1.move(currPiece, 3, 3, 1, 5, board);
		assertTrue(!player1.isTurn());
		assertTrue(player2.isTurn());
		assertTrue(board.search(3, 3) == null);
		assertTrue(board.search(2, 4) == null);
		assertTrue(board.search(1, 5) != null);
		assertTrue(currPiece.getXCoordinate() == 1);
		assertTrue(currPiece.getYCoordinate() == 5);
		assertTrue(board.getRedPiecesList().size() == 11);
		game.updateMoves();
		currPiece = board.search(0, 6);
		player2.move(currPiece, 0, 6, 2, 4, board);
		assertTrue(board.getBlackPiecesList().size() == 10);
		currPiece = board.search(2, 0);
		player1.move(currPiece, 2, 0, 1, 1, board);
		game.updateMoves();
		currPiece = board.search(0, 2);
		player2.move(currPiece, 0, 2, 2, 0, board);
		assertTrue(board.getBlackPiecesList().size() == 9);
		assertTrue(currPiece.isKing());
		game.updateMoves();
		currPiece = board.search(4, 2);
		player1.move(currPiece, 4, 2, 5, 3, board);
		game.updateMoves();
		currPiece = board.search(2, 0);
		player2.move(currPiece, 2, 0, 4, 2, board);
		game.updateMoves();
		player2.move(currPiece, 4, 2, 6, 4, board);
		assertTrue(board.getBlackPiecesList().size() == 7);
		game.updateMoves();
		currPiece = board.search(0, 0);
		player1.move(currPiece, 0, 0, 1, 1, board);
		game.updateMoves();
		currPiece = board.search(4, 6);
		player2.move(currPiece, 4, 6, 3, 5, board);
		game.updateMoves();
		currPiece = board.search(5, 1);
		player1.move(currPiece, 5, 1, 4, 2, board);
		game.updateMoves();
		currPiece = board.search(6, 4);
		player2.move(currPiece, 6, 4, 5, 3, board);
		game.updateMoves();
		currPiece = board.search(4, 2);
		player1.move(currPiece, 4, 2, 6, 4, board);
		game.updateMoves();
		player1.move(currPiece, 6, 4, 4, 6, board);
		assertTrue(board.getRedPiecesList().size() == 9);
		game.updateMoves();
		currPiece = board.search(3, 7);
		player2.move(currPiece, 3, 7, 5, 5, board);
		game.updateMoves();
		currPiece = board.search(1, 1);
		player1.move(currPiece, 1, 1, 0, 2, board);
		game.updateMoves();
		currPiece = board.search(5, 5);
		player2.move(currPiece, 5, 5, 4, 4, board);
		game.updateMoves();
		currPiece = board.search(4, 0);
		player1.move(currPiece, 4, 0, 3, 1, board);
		game.updateMoves();
		currPiece = board.search(4, 4);

		player2.move(currPiece, 4, 4, 3, 3, board);
		game.updateMoves();
		currPiece = board.search(2, 2);
		player1.move(currPiece, 2, 2, 4, 4, board);
		game.updateMoves();
		currPiece = board.search(3, 5);
		player2.move(currPiece, 3, 5, 5, 3, board);
		game.updateMoves();
		currPiece = board.search(6, 2);
		player1.move(currPiece, 6, 2, 4, 4, board);
		game.updateMoves();
		currPiece = board.search(2, 6);
		player2.move(currPiece, 2, 6, 1, 5, board);
		game.updateMoves();
		currPiece = board.search(3, 1);
		player1.move(currPiece, 3, 1, 2, 2, board);
		game.updateMoves();
		currPiece = board.search(1, 7);
		player2.move(currPiece, 1, 7, 0, 6, board);
		game.updateMoves();
		currPiece = board.search(4, 4);
		player1.move(currPiece, 4, 4, 3, 5, board);
		game.updateMoves();
		currPiece = board.search(5, 7);
		player2.move(currPiece, 5, 7, 4, 6, board);
		game.updateMoves();
		currPiece = board.search(3, 5);
		player1.move(currPiece, 3, 5, 5, 7, board);
		assertTrue(currPiece.isKing());
		assertTrue(board.getBlackPiecesList().size() == 5);
		assertTrue(board.getRedPiecesList().size() == 6);
		currPiece = board.search(2, 4);
		player2.move(currPiece, 2, 4, 1, 3, board);
		game.updateMoves();
		currPiece = board.search(2, 2);
		player1.move(currPiece, 2, 2, 0, 4, board);
		game.updateMoves();
		player1.move(currPiece, 0, 4, 2, 6, board);
		game.updateMoves();
		currPiece = board.search(7, 5);
		player2.move(currPiece, 7, 5, 6, 4, board);
		game.updateMoves();
		currPiece = board.search(5, 7);
		player1.move(currPiece, 5, 7, 7, 5, board);
		assertTrue(board.getBlackPiecesList().size() == 5);
		assertTrue(board.getRedPiecesList().size() == 3);
		game.updateMoves();
		player1.move(currPiece, 7, 5, 5, 3, board);
		game.updateMoves();
		currPiece = board.search(7, 7);
		player2.move(currPiece, 7, 7, 6, 6, board);
		game.updateMoves();
		currPiece = board.search(5, 3);
		player1.move(currPiece, 5, 3, 4, 4, board);
		game.updateMoves();
		currPiece = board.search(6, 6);
		player2.move(currPiece, 6, 6, 5, 5, board);
		game.updateMoves();
		currPiece = board.search(4, 4);
		player1.move(currPiece, 4, 4, 6, 6, board);
		game.updateMoves();
		Piece lastWhitePiece = board.search(0, 6);
		Piece lastBlackPiece = board.search(6, 6);
		player2.move(lastWhitePiece, 0, 6, 1, 5, board);
		game.updateMoves();
		player1.move(lastBlackPiece, 6, 6, 5, 5, board);
		game.updateMoves();
		player2.move(lastWhitePiece, 1, 5, 2, 4, board);
		game.updateMoves();
		player1.move(lastBlackPiece, 5, 5, 4, 4, board);
		game.updateMoves();
		player2.move(lastWhitePiece, 2, 4, 3, 3, board);
		game.updateMoves();
		player1.move(lastBlackPiece, 4, 4, 2, 2, board);
		game.updateMoves();
		assertTrue(!player1.isTurn());
		assertTrue(!player2.isTurn());
		assertTrue(game.gameWon());
		assertTrue(game.blackWon());

	}
}
