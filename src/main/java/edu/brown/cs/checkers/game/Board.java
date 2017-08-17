package edu.brown.cs.checkers.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Justin on 6/8/2017.
 */
public class Board {
    private static final int COORDS = 8;
    private static final int BLACKSTART = 0;
    private static final int BLACKEND = 2;
    private static final int REDSTART = 5;
    private static final int REDEND = 8;
    private Piece[][] board;
    private Map<Piece,Map<String, Map<Integer, List<Integer>>>> allPossibleMovesRed;
    private Map<Piece,Map<String, Map<Integer, List<Integer>>>> allPossibleMovesBlack;
    private List<Piece> redPiecesList;
    private List<Piece> blackPiecesList;
    private boolean redMustJump = false;
    private boolean blackMustJump = false;
    public Board() {
        redPiecesList = new ArrayList<>();
        blackPiecesList = new ArrayList<>();
        allPossibleMovesRed = new ConcurrentHashMap<>();
        allPossibleMovesBlack = new ConcurrentHashMap<>();
        board = new Piece[COORDS][COORDS];
        this.setUp();
    }
    private void setUp() {
        for (int i = BLACKSTART; i <=BLACKEND; i++) {
            int start;
            if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = new Piece("black",j, i);
                board[j][i] = currPiece;
                blackPiecesList.add(currPiece);
            }
        }
        for (int i = REDSTART; i <REDEND; i ++) {
            int start;
            if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = new Piece("red", j, i);
                board[j][i] = currPiece;
                redPiecesList.add(currPiece);
            }
        }
    }
    protected void  allBlackMoves() {
    	
        blackMustJump = false;
        for (int i = 0; i<COORDS; i ++) {
            int start;
            if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = board[j][i];
                if (currPiece!=null) {
	                if (currPiece.isBlack()) {
	                    Map<String, Map<Integer, List<Integer>>> result = possibleMovesBlack(j, i, currPiece);
	                    if (result.containsKey("jump")) {
	                        blackMustJump = true;
	                    }
	                    if (result!= null) {
	                        allPossibleMovesBlack.put(currPiece,possibleMovesBlack(j, i, currPiece));
	                    }
	                }
                }
            }
        }
        if (blackMustJump) {
            Iterator<Entry<Piece, Map<String, Map<Integer, List<Integer>>>>> it = allPossibleMovesBlack.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Piece, Map<String, Map<Integer, List<Integer>>>> curr = it.next();
                boolean containsNormal = curr.getValue().containsKey("normal");
                boolean containsJump = curr.getValue().containsKey("jump");
                if (containsNormal && containsJump) {
                    curr.getValue().remove("normal");
                }
                else if (containsNormal && !containsJump) {
                    allPossibleMovesBlack.remove(curr.getKey());

                }
            }
        }
        blackMustJump = false;
    }
//    protected Map<String, Map<Integer, Integer>>getPieceMove(int x, int y, Piece currPiece) {
//    }
    protected Map<String, Map<Integer, List<Integer>>>  possibleMovesBlack(int x, int y, Piece currPiece) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("coordinates is outside the board");
        }
        if (!currPiece.isBlack()) {
            throw new IllegalArgumentException("Piece is not black");
        }
        Map<String, Map<Integer, List<Integer>>>moves= new ConcurrentHashMap<>();
        Map<Integer, List<Integer>>  jumpMove = new ConcurrentHashMap<>();
        Map<Integer, List<Integer>> normalMove = new ConcurrentHashMap<>();
        List<Integer> leftYNormal = new ArrayList<>();
        List<Integer> rightYNormal = new ArrayList<>();
        List<Integer> leftYJump = new ArrayList<>();
        List<Integer> rightYJump = new ArrayList<>();
        if (blackCanJumpLeft(x, y)) {
            blackMustJump = true;
            leftYJump.add(y+2);
            jumpMove.put(x+2, leftYJump);
        }
        else if (inBounds(x+1, y+1) && blackMustJump == false && search(x+1, y+1) ==null) {
        	leftYNormal.add(y+1);
            normalMove.put(x+1, leftYNormal);
        }
        if (blackCanJumpRight(x, y)) {
            blackMustJump = true;
            rightYJump.add(y+2);
            jumpMove.put(x-2, rightYJump);
        }
        else if (inBounds(x-1, y+1) && blackMustJump == false && search(x-1, y+1) ==null) {
        	rightYNormal.add(y+1);
            normalMove.put(x-1, rightYNormal);
        }
        if (currPiece.isKing()) {
            if (blackCanJumpBackLeft(x, y)) {
                blackMustJump = true;
                leftYJump.add(y-2);
                jumpMove.put(x+2, leftYJump);
            }
            else if (inBounds(x+1, y-1) && blackMustJump == false && search(x+1, y-1) ==null) {
            	leftYNormal.add(y-1);
                normalMove.put(x+1, leftYNormal);
            }
            if (blackCanJumpBackRight(x, y)) {
                blackMustJump = true;
                rightYJump.add(y-2);
                jumpMove.put(x-2, rightYJump);
            }
            else if (inBounds(x-1, y-1) && blackMustJump == false && search(x-1, y-1) ==null) {
            	rightYNormal.add(y-1);
                normalMove.put(x-1, rightYNormal);
            }
        }
        if (jumpMove.size() == 0 && normalMove.size()!=0) {
            blackMustJump = false;
            moves.put("normal", normalMove);
        }
        else if (jumpMove.size() != 0) {
            blackMustJump = true;
            moves.put("jump", jumpMove);
        }
        return moves;

    }
    protected Map<String, Map<Integer, List<Integer>>> possibleMovesRed( int x, int y, Piece currPiece) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("coordinates is outside the board");
        }

        if (currPiece.isBlack()) {
            throw new IllegalArgumentException("Piece is not red");
        }
        Map<String, Map<Integer, List<Integer>>>moves = new HashMap<>();

        Map<Integer, List<Integer>> jumpMove = new HashMap<>();
        Map<Integer, List<Integer>> normalMove = new HashMap<>();
        List<Integer> rightYJump = new ArrayList<>();
        List<Integer> leftYJump = new ArrayList<>();
        List<Integer> rightYNormal = new ArrayList<>();
        List<Integer> leftYNormal = new ArrayList<>();
        if (redCanJumpLeft(x, y)) {
        	System.out.println("redCanJumpLeft");
            leftYJump.add(y-2);
            jumpMove.put(x-2, leftYJump);
        }
        else if (inBounds(x-1, y-1) &&  !redMustJump && search(x-1, y-1) ==null) {
        	leftYNormal.add(y-1);
            normalMove.put(x-1, leftYNormal);
        }
        if (redCanJumpRight(x, y)) {
        	System.out.println("redCanJumpRight");
            redMustJump = true;
            rightYJump.add(y-2);
            jumpMove.put(x+2, rightYJump);
        }
        else if (inBounds(x+1, y-1) &&  !redMustJump && search(x+1, y-1) ==null) {
        	rightYNormal.add(y-1);
            normalMove.put(x+1, rightYNormal);

        }
        if (currPiece.isKing()) {
            if (redCanJumpBackLeft(x, y)) {
                redMustJump = true;
                leftYJump.add(y+2);
                jumpMove.put(x-2, leftYJump);
            }
            else if (inBounds(x-1, y+1) && !redMustJump && search(x-1, y+1) ==null) {
            	leftYNormal.add(y+1);
                normalMove.put(x-1, leftYNormal);
            }
             if (redCanJumpBackRight(x, y)) {
                 redMustJump = true;
                 rightYJump.add(y+2);
                 jumpMove.put(x+2, rightYJump);
            }
            else if (inBounds(x+1, y+1) && !redMustJump && search(x+1, y+1) ==null) {
            	rightYNormal.add(y+1);
                normalMove.put(x+1, rightYNormal);
             }
        }
        if (jumpMove.size() == 0 && normalMove.size() != 0) {
            moves.put("normal", normalMove);
        }
        else if (jumpMove.size() !=0) {
            redMustJump = true;
            moves.put("jump", jumpMove);
        }
        return moves;
    }
    protected void  allRedMoves() {
        redMustJump = false;
        for (int i = 0; i<COORDS; i ++) {
            int start;
            if (i%2 == 0) {
                start = 0;
            }
            else {
                start = 1;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = board[j][i];
                if (currPiece!=null) {
	                if (!currPiece.isBlack()) {
	                    Map<String, Map<Integer, List<Integer>>> result = possibleMovesRed(j, i, currPiece);
	                    if (result.containsKey("jump")) {
	                        redMustJump = true;
	                    }
	                    if (result!= null) {
	                        allPossibleMovesRed.put(currPiece,possibleMovesRed(j, i, currPiece));
	                    }
	                }
                }
            }
        }
        if (redMustJump) {
            Iterator<Entry<Piece, Map<String, Map<Integer, List<Integer>>>>> it = allPossibleMovesRed.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Piece, Map<String, Map<Integer, List<Integer>>>> curr = it.next();
                boolean containsNormal = curr.getValue().containsKey("normal");
                boolean containsJump = curr.getValue().containsKey("jump");
                if (containsNormal && containsJump) {
                    curr.getValue().remove("normal");
                }
                else if (containsNormal && !containsJump) {
                    allPossibleMovesRed.remove(curr.getKey());

                }
            }
        }
    }


    private boolean blackCanJumpLeft(int x, int y) {
        if (inBounds(x+1, y+1)) {
            if (search(x+1, y+1) !=null && checkNeighborIsRed(x+1, y+1)) {
                if (inBounds(x + 2, y + 2)) {
                    if (search(x + 2, y + 2) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean blackCanJumpBackLeft(int x, int y) {
        if (inBounds(x+1, y-1)) {
            if (search(x+1, y-1) !=null && checkNeighborIsRed(x+1, y-1)) {
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
        if (inBounds(x-1, y+1)) {
            if (search(x-1, y+1) !=null && checkNeighborIsRed(x-1, y+1)) {
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
        if (inBounds(x-1, y-1)) {
            if (search(x-1, y-1) !=null && checkNeighborIsRed(x-1, y-1)) {
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
        if (inBounds(x-1, y-1)) {
            if (search(x-1, y-1) !=null && !checkNeighborIsRed(x-1, y-1)) {
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
        if (inBounds(x-1, y+1)) {
            if (search(x-1, y+1) !=null && !checkNeighborIsRed(x-1, y+1)) {
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
        if (inBounds(x+1, y-1)) {
            if (search(x+1, y-1) !=null && !checkNeighborIsRed(x+1, y-1)) {
                if (inBounds(x + 2, y - 2)) {
                    if (search(x + 2, y -2) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean redCanJumpBackRight(int x, int y) {
        if (inBounds(x+1, y+1)) {
            if (search(x+1, y+1) !=null && !checkNeighborIsRed(x+1, y+1)) {
                if (inBounds(x + 2, y +2)) {
                    if (search(x + 2, y +2) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private  boolean checkNeighborIsRed(int x, int y) {
        Piece neighbor  = search(x, y);
        if (neighbor != null) {
            if (!neighbor.isBlack()) {
                return true;
            }
        }
        return false;
    }
    private boolean inBounds(int x, int y) {
        if (x==8 || y ==8 || x==-1 || y==-1) {
            return false;

        }
        return true;
    }
    protected Piece search(int x, int y) {

        return board[x][y];

    }
    protected boolean validMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
        Map<Piece, Map<String, Map<Integer, List<Integer>>>> curr = null;
        if (currPiece.isBlack()) {
            curr = allPossibleMovesBlack;
        }
        else  {
            curr = allPossibleMovesRed;
        }
        if (!curr.containsKey(currPiece)) {
            return false;
        }
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
    public Map<Piece,Map<String, Map<Integer, List<Integer>>>>getMovablePiecesRed() {
        return ImmutableMap.copyOf(allPossibleMovesRed);
    }
    public Map<Piece,Map<String, Map<Integer, List<Integer>>>>getMovablePiecesBlack() {
        return ImmutableMap.copyOf(allPossibleMovesBlack);
    }
    protected boolean move(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
        Map<Piece, Map<String, Map<Integer, List<Integer>>>> check = null;
        if (currPiece.isBlack()) {
            check = allPossibleMovesBlack;
        }
        else {
            check = allPossibleMovesRed;
        }
        if (!validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
            throw  new IllegalArgumentException("move is not valid");
        }

        if(check.get(currPiece).containsKey("jump")) {
            return jumpMove(xStart, yStart, xEnd, yEnd, currPiece);
        }
        else if (check.get(currPiece).containsKey("normal")){
            return normalMove(xStart, yStart, xEnd, yEnd, currPiece);
        }

      return false;
    }
    protected void updateMoves() {
    	allRedMoves();
    	allBlackMoves();
    }
    private boolean normalMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {

        board[xEnd][yEnd] = currPiece;
        currPiece.setCoordinates(xEnd, yEnd);
        board[xStart][yStart] = null;
        this.canKing(currPiece);
        return false;
    }
    private boolean jumpMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
    	System.out.println("jumpMove check");
        board[xEnd][yEnd] = currPiece;
        boolean becameKing = false;
        boolean isNotKing = false;
        currPiece.setCoordinates(xEnd, yEnd);
        int jumpPieceXCoordiante = (xStart + xEnd) /2;
        int jumpPieceYCoordiante = (yStart + yEnd) /2;
        board[xStart][yStart] = null;
        if (currPiece.isBlack()) {
            redPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
        }
        else {
            blackPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
        }
        if (!currPiece.isKing()) {
        	isNotKing = true;
        }
        this.canKing(currPiece);
        if (isNotKing && currPiece.isKing()) {
        	becameKing = true;
        }
        board[jumpPieceXCoordiante][jumpPieceYCoordiante] = null;
        if (!becameKing) {
        	if (currPiece.isBlack()) {
        		if (this.possibleMovesBlack(currPiece.getXCoordinate(), currPiece.getYCoordinate(), currPiece).containsKey("jump")) {
        			return true;
        		}
        	}
        	else  {
        		if (this.possibleMovesRed(currPiece.getXCoordinate(), currPiece.getYCoordinate(), currPiece).containsKey("jump")) {
        			return true;
        		}
        	}
        }
        return false;
    }
    public List<Piece> getRedPiecesList() {
        return ImmutableList.copyOf(redPiecesList);
    }
    public List<Piece> getBlackPiecesList() {
        return ImmutableList.copyOf(blackPiecesList);
    }
    protected void canKing(Piece piece) {
        if (piece.isBlack()) {
            if (piece.getYCoordinate() == 7) {
                piece.kingMe();
            }
        }
        else {
            if (piece.getYCoordinate() == 0) {
                piece.kingMe();
            }
        }
    }
}
