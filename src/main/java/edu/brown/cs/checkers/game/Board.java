package edu.brown.cs.checkers.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Justin on 6/8/2017. Creates the checkers board and also handles
 * checking possible moves for pieces
 */
public class Board {
	private Piece jumpPiece;
	private static final int COORDS = 8;
	private static final int BLACKSTART = 0;
	private static final int BLACKEND = 2;
	private static final int REDSTART = 5;
	private static final int REDEND = 8;
	private Piece[][] board;
	private Map<Piece, Map<String, Map<Integer, List<Integer>>>> allPossibleMovesRed;
	private Map<Piece, Map<String, Map<Integer, List<Integer>>>> allPossibleMovesBlack;
	private List<Piece> redPiecesList;
	private List<Piece> blackPiecesList;
	private boolean redMustJump = false;
	private boolean blackMustJump = false;

	public Board() {
		jumpPiece = null;
		redPiecesList = new ArrayList<>();
		blackPiecesList = new ArrayList<>();
		allPossibleMovesRed = new ConcurrentHashMap<>();
		allPossibleMovesBlack = new ConcurrentHashMap<>();
		board = new Piece[COORDS][COORDS];
		this.setUp();
	}

	/**
	 * sets up the board and adds the pieces to the board
	 */
	private void setUp() {
		for (int i = BLACKSTART; i <= BLACKEND; i++) {
			// start x coordinate of the current row
			// checker pieces are spaced every 2 x positions
			int start;
			if (i % 2 == 0) {
				start = 0;
			} else {
				start = 1;
			}
			for (int j = start; j < COORDS; j += 2) {
				Piece currPiece = new Piece("black", j, i);
				board[j][i] = currPiece;
				blackPiecesList.add(currPiece);
			}
		}
		for (int i = REDSTART; i < REDEND; i++) {
			int start;
			if (i % 2 == 0) {
				start = 0;
			} else {
				start = 1;
			}
			for (int j = start; j < COORDS; j += 2) {
				Piece currPiece = new Piece("red", j, i);
				board[j][i] = currPiece;
				redPiecesList.add(currPiece);
			}
		}
	}

	/**
	 * gets all of the possible moves of all of the black pieces by looping
	 * through the list
	 */
	protected void allBlackMoves() {

		blackMustJump = false;
		for (int i = 0; i < blackPiecesList.size(); i++) {

			Piece currPiece = blackPiecesList.get(i);
			if (currPiece.isBlack()) {
				// gets the possible moves of the current piece
				Map<String, Map<Integer, List<Integer>>> result = possibleMovesBlack(currPiece.getXCoordinate(),
						currPiece.getYCoordinate(), currPiece);
				if (result.containsKey("jump")) {
					blackMustJump = true;
				}
				// puts the results in the bigger hashmap
				if (result.size() != 0) {
					allPossibleMovesBlack.put(currPiece, result);
				}
			}
		}
		// if there is one piece that has a jump move, all normal moves must be
		// deleted
		if (blackMustJump) {
			Iterator<Entry<Piece, Map<String, Map<Integer, List<Integer>>>>> it = allPossibleMovesBlack.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<Piece, Map<String, Map<Integer, List<Integer>>>> curr = it.next();
				boolean containsNormal = curr.getValue().containsKey("normal");
				boolean containsJump = curr.getValue().containsKey("jump");
				if (containsNormal && containsJump) {
					curr.getValue().remove("normal");
				} else if (containsNormal && !containsJump) {
					allPossibleMovesBlack.remove(curr.getKey());

				}
			}
		}
		blackMustJump = false;
	}

	/**
	 * checks for all possible moves of one black piece
	 * 
	 * @param x
	 * @param y
	 * @param currPiece
	 * @return
	 */
	protected Map<String, Map<Integer, List<Integer>>> possibleMovesBlack(int x, int y, Piece currPiece) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("coordinates is outside the board");
		}
		if (!currPiece.isBlack()) {
			throw new IllegalArgumentException("Piece is not black");
		}
		Map<String, Map<Integer, List<Integer>>> moves = new ConcurrentHashMap<>();
		Map<Integer, List<Integer>> jumpMove = new ConcurrentHashMap<>();
		Map<Integer, List<Integer>> normalMove = new ConcurrentHashMap<>();
		// list of y coordinates which will be the value of one of the
		// two other maps above. The key of the above two maps is the x cord
		List<Integer> leftYNormal = new ArrayList<>();
		List<Integer> rightYNormal = new ArrayList<>();
		List<Integer> leftYJump = new ArrayList<>();
		List<Integer> rightYJump = new ArrayList<>();
		// if black can jump then black must jump and all future calculations
		// called from
		// allBlackMoves don't need to check for normal moves
		// normal moves calculated before need to be erased at the end of that
		// method
		if (blackCanJumpLeft(x, y)) {
			blackMustJump = true;
			leftYJump.add(y + 2);
			jumpMove.put(x + 2, leftYJump);
			// checks if the space to the left and up is empty if so: add it to
			// the corresponding data structures
			// rest if the if/else if scenarios follow a near-identical pattern

		} else if (inBounds(x + 1, y + 1) && blackMustJump == false && search(x + 1, y + 1) == null) {
			leftYNormal.add(y + 1);
			normalMove.put(x + 1, leftYNormal);
		}
		if (blackCanJumpRight(x, y)) {
			blackMustJump = true;
			rightYJump.add(y + 2);
			jumpMove.put(x - 2, rightYJump);
		} else if (inBounds(x - 1, y + 1) && blackMustJump == false && search(x - 1, y + 1) == null) {
			rightYNormal.add(y + 1);
			normalMove.put(x - 1, rightYNormal);
		}
		// if the current piece is King, check the spaces behind it
		// same if/else if structure
		if (currPiece.isKing()) {
			if (blackCanJumpBackLeft(x, y)) {
				blackMustJump = true;
				leftYJump.add(y - 2);
				jumpMove.put(x + 2, leftYJump);
			} else if (inBounds(x + 1, y - 1) && blackMustJump == false && search(x + 1, y - 1) == null) {
				leftYNormal.add(y - 1);
				normalMove.put(x + 1, leftYNormal);
			}
			if (blackCanJumpBackRight(x, y)) {
				blackMustJump = true;
				rightYJump.add(y - 2);
				jumpMove.put(x - 2, rightYJump);
			} else if (inBounds(x - 1, y - 1) && blackMustJump == false && search(x - 1, y - 1) == null) {
				rightYNormal.add(y - 1);
				normalMove.put(x - 1, rightYNormal);
			}
		}
		// if the normalMove map is not empty and the jumpMove map is empty
		// this piece can move normally
		if (jumpMove.size() == 0 && normalMove.size() != 0) {
			blackMustJump = false;
			moves.put("normal", normalMove);
			// this piece can only move via jump
		} else if (jumpMove.size() != 0) {
			blackMustJump = true;
			moves.put("jump", jumpMove);
		}
		return moves;

	}

	/**
	 * similar to possibleMovesBlack
	 * 
	 * @param x
	 * @param y
	 * @param currPiece
	 * @return
	 */
	protected Map<String, Map<Integer, List<Integer>>> possibleMovesRed(int x, int y, Piece currPiece) {
		if (!inBounds(x, y)) {
			throw new IndexOutOfBoundsException("coordinates is outside the board");
		}

		if (currPiece.isBlack()) {
			throw new IllegalArgumentException("Piece is not red");
		}
		Map<String, Map<Integer, List<Integer>>> moves = new HashMap<>();

		Map<Integer, List<Integer>> jumpMove = new HashMap<>();
		Map<Integer, List<Integer>> normalMove = new HashMap<>();
		List<Integer> rightYJump = new ArrayList<>();
		List<Integer> leftYJump = new ArrayList<>();
		List<Integer> rightYNormal = new ArrayList<>();
		List<Integer> leftYNormal = new ArrayList<>();
		if (redCanJumpLeft(x, y)) {
			// note that y and x offsets are flipped because of the change of
			// perspective
			// depending on what player you are. So one step left is x-1 but for
			// black it's x+1.
			leftYJump.add(y - 2);
			jumpMove.put(x - 2, leftYJump);
		} else if (inBounds(x - 1, y - 1) && !redMustJump && search(x - 1, y - 1) == null) {
			leftYNormal.add(y - 1);
			normalMove.put(x - 1, leftYNormal);
		}
		if (redCanJumpRight(x, y)) {
			redMustJump = true;
			rightYJump.add(y - 2);
			jumpMove.put(x + 2, rightYJump);
		} else if (inBounds(x + 1, y - 1) && !redMustJump && search(x + 1, y - 1) == null) {
			rightYNormal.add(y - 1);
			normalMove.put(x + 1, rightYNormal);

		}
		if (currPiece.isKing()) {
			if (redCanJumpBackLeft(x, y)) {
				redMustJump = true;
				leftYJump.add(y + 2);
				jumpMove.put(x - 2, leftYJump);
			} else if (inBounds(x - 1, y + 1) && !redMustJump && search(x - 1, y + 1) == null) {
				leftYNormal.add(y + 1);
				normalMove.put(x - 1, leftYNormal);
			}
			if (redCanJumpBackRight(x, y)) {
				redMustJump = true;
				rightYJump.add(y + 2);
				jumpMove.put(x + 2, rightYJump);
			} else if (inBounds(x + 1, y + 1) && !redMustJump && search(x + 1, y + 1) == null) {
				rightYNormal.add(y + 1);
				normalMove.put(x + 1, rightYNormal);
			}
		}
		if (jumpMove.size() == 0 && normalMove.size() != 0) {
			moves.put("normal", normalMove);
		} else if (jumpMove.size() != 0) {
			redMustJump = true;
			moves.put("jump", jumpMove);
		}
		return moves;
	}

	/**
	 * same as allBlackMoves but handles the red pieces
	 */
	protected void allRedMoves() {
		redMustJump = false;
		for (int i = 0; i < redPiecesList.size(); i++) {

			Piece currPiece = redPiecesList.get(i);
			if (!currPiece.isBlack()) {
				Map<String, Map<Integer, List<Integer>>> result = possibleMovesRed(currPiece.getXCoordinate(),
						currPiece.getYCoordinate(), currPiece);
				if (result.containsKey("jump")) {
					redMustJump = true;
				}
				if (result.size() != 0) {
					allPossibleMovesRed.put(currPiece, result);
				}
			}
		}
		if (redMustJump) {
			Iterator<Entry<Piece, Map<String, Map<Integer, List<Integer>>>>> it = allPossibleMovesRed.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<Piece, Map<String, Map<Integer, List<Integer>>>> curr = it.next();
				boolean containsNormal = curr.getValue().containsKey("normal");
				boolean containsJump = curr.getValue().containsKey("jump");
				if (containsNormal && containsJump) {
					curr.getValue().remove("normal");
				} else if (containsNormal && !containsJump) {
					allPossibleMovesRed.remove(curr.getKey());

				}
			}
		}
	}

	/**
	 * all of these "....canJump..." follow a similar pattern as expressed in
	 * the comments below
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return
	 */
	private boolean blackCanJumpLeft(int x, int y) {
		// if the middle coordinate is in bounds
		if (inBounds(x + 1, y + 1)) {
			// if the middle coordinate contains the opponent's piece
			if (search(x + 1, y + 1) != null && checkNeighborIsRed(x + 1, y + 1)) {
				// if the destination is in bounds
				if (inBounds(x + 2, y + 2)) {
					// if the destination is empty
					if (search(x + 2, y + 2) == null) {
						// the piece can jump
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean blackCanJumpBackLeft(int x, int y) {
		if (inBounds(x + 1, y - 1)) {
			if (search(x + 1, y - 1) != null && checkNeighborIsRed(x + 1, y - 1)) {
				if (inBounds(x + 2, y - 2)) {
					if (search(x + 2, y - 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean blackCanJumpRight(int x, int y) {
		if (inBounds(x - 1, y + 1)) {
			if (search(x - 1, y + 1) != null && checkNeighborIsRed(x - 1, y + 1)) {
				if (inBounds(x - 2, y + 2)) {
					if (search(x - 2, y + 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean blackCanJumpBackRight(int x, int y) {
		if (inBounds(x - 1, y - 1)) {
			if (search(x - 1, y - 1) != null && checkNeighborIsRed(x - 1, y - 1)) {
				if (inBounds(x - 2, y - 2)) {
					if (search(x - 2, y - 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean redCanJumpLeft(int x, int y) {
		if (inBounds(x - 1, y - 1)) {
			if (search(x - 1, y - 1) != null && !checkNeighborIsRed(x - 1, y - 1)) {
				if (inBounds(x - 2, y - 2)) {
					if (search(x - 2, y - 2) == null) {
						return true;
					}
				}
			}
		}
		return false;

	}

	private boolean redCanJumpBackLeft(int x, int y) {
		if (inBounds(x - 1, y + 1)) {
			if (search(x - 1, y + 1) != null && !checkNeighborIsRed(x - 1, y + 1)) {
				if (inBounds(x - 2, y + 2)) {
					if (search(x - 2, y + 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean redCanJumpRight(int x, int y) {
		if (inBounds(x + 1, y - 1)) {
			if (search(x + 1, y - 1) != null && !checkNeighborIsRed(x + 1, y - 1)) {
				if (inBounds(x + 2, y - 2)) {
					if (search(x + 2, y - 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean redCanJumpBackRight(int x, int y) {
		if (inBounds(x + 1, y + 1)) {
			if (search(x + 1, y + 1) != null && !checkNeighborIsRed(x + 1, y + 1)) {
				if (inBounds(x + 2, y + 2)) {
					if (search(x + 2, y + 2) == null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkNeighborIsRed(int x, int y) {
		Piece neighbor = search(x, y);
		if (neighbor != null) {
			if (!neighbor.isBlack()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks if the coordinates are in bounds
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean inBounds(int x, int y) {
		if (x == 8 || y == 8 || x == -1 || y == -1) {
			return false;

		}
		return true;
	}

	/**
	 * returns a piece based on the coordiantes
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	protected Piece search(int x, int y) {

		return board[x][y];

	}

	/**
	 * checks if a move is valid
	 * 
	 * @param xStart
	 * @param yStart
	 * @param xEnd
	 * @param yEnd
	 * @param currPiece
	 * @return
	 */
	protected boolean validMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
		if (jumpPiece != currPiece && jumpPiece != null) {
			return false;
		}
		Map<Piece, Map<String, Map<Integer, List<Integer>>>> curr = null;
		// move depends on the color of the piece
		if (currPiece.isBlack()) {
			curr = allPossibleMovesBlack;
		} else {
			curr = allPossibleMovesRed;
		}
		// if the piece is not in the map
		if (!curr.containsKey(currPiece)) {
			return false;
		}
		// checks if the piece as well as the coordinates are in the map
		Iterator<Entry<String, Map<Integer, List<Integer>>>> it = curr.get(currPiece).entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<Integer, List<Integer>>> next = it.next();
			if (next.getValue().containsKey(xEnd)) {
				if (next.getValue().get(xEnd).contains(yEnd)) {
					return true;
				}
			}

		}
		return false;

	}

	protected Piece[][] getBoard() {
		return board;
	}

	public Map<Piece, Map<String, Map<Integer, List<Integer>>>> getMovablePiecesRed() {
		return ImmutableMap.copyOf(allPossibleMovesRed);
	}

	public Map<Piece, Map<String, Map<Integer, List<Integer>>>> getMovablePiecesBlack() {
		return ImmutableMap.copyOf(allPossibleMovesBlack);
	}

	/**
	 * moves the piece to its destination
	 * 
	 * @param xStart
	 * @param yStart
	 * @param xEnd
	 * @param yEnd
	 * @param currPiece
	 * @return
	 */
	protected boolean move(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
		Map<Piece, Map<String, Map<Integer, List<Integer>>>> check = null;
		if (jumpPiece!=null && jumpPiece!= currPiece) {
			throw new IllegalArgumentException("move is not valid. Same piece must take all jump options.");
		}
		if (currPiece.isBlack()) {
			check = allPossibleMovesBlack;
		} else {
			check = allPossibleMovesRed;
		}
		if (!validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
			throw new IllegalArgumentException("move is not valid");
		}
		// if the move is a jump move
		if (check.get(currPiece).containsKey("jump")) {
			return jumpMove(xStart, yStart, xEnd, yEnd, currPiece);
		} else if (check.get(currPiece).containsKey("normal")) {
			return normalMove(xStart, yStart, xEnd, yEnd, currPiece);
		}

		return false;
	}

	protected void updateMoves() {
		allRedMoves();
		allBlackMoves();
	}

	private boolean normalMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
		// update piece and board to the new coordinates
		board[xEnd][yEnd] = currPiece;
		currPiece.setCoordinates(xEnd, yEnd);
		board[xStart][yStart] = null;
		// checks if the piece can be kinged
		this.canKing(currPiece);
		return false;
	}

	private boolean jumpMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
		board[xEnd][yEnd] = currPiece;
		boolean becameKing = false;
		boolean isNotKing = false;
		currPiece.setCoordinates(xEnd, yEnd);
		// middle piece coordinate
		int jumpPieceXCoordiante = (xStart + xEnd) / 2;
		int jumpPieceYCoordiante = (yStart + yEnd) / 2;
		board[xStart][yStart] = null;
		// removes the piece from the corresponding list
		if (currPiece.isBlack()) {
			redPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
		} else {
			blackPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
		}
		if (!currPiece.isKing()) {
			isNotKing = true;
		}
		// checks if the piece is king because regardless if the new king can
		// jump the turn ends
		this.canKing(currPiece);
		if (isNotKing && currPiece.isKing()) {
			becameKing = true;
		}
		board[jumpPieceXCoordiante][jumpPieceYCoordiante] = null;
		if (!becameKing) {
			// checks if there's another jump move for the piece
			if (currPiece.isBlack()) {
				if (this.possibleMovesBlack(currPiece.getXCoordinate(), currPiece.getYCoordinate(), currPiece)
						.containsKey("jump")) {
					jumpPiece = currPiece;
					return true;
				}
			} else {
				if (this.possibleMovesRed(currPiece.getXCoordinate(), currPiece.getYCoordinate(), currPiece)
						.containsKey("jump")) {
					jumpPiece = currPiece;
					return true;
				}
			}
		}
		jumpPiece = null;
		return false;
	}

	public List<Piece> getRedPiecesList() {
		return ImmutableList.copyOf(redPiecesList);
	}

	public List<Piece> getBlackPiecesList() {
		return ImmutableList.copyOf(blackPiecesList);
	}

	/**
	 * checks if the piece is king based on if the piece moved to the opposite
	 * end of the board
	 * 
	 * @param piece
	 */
	protected void canKing(Piece piece) {
		// if piece is black, the other end is where y == 7
		if (piece.isBlack()) {
			if (piece.getYCoordinate() == 7) {
				piece.kingMe();
			}
			// if the piece is not black, the other end is where y == 0
		} else {
			if (piece.getYCoordinate() == 0) {
				piece.kingMe();
			}
		}
	}
}
