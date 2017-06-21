package checkers.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;

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
    private Map<Piece,Map<String, Map<Integer, Integer>>> allPossibleMovesRed;
    private Map<Piece,Map<String, Map<Integer, Integer>>> allPossibleMovesBlack;
    private List redPiecesList;
    private List blackPiecesList;
    private boolean redMustJump = false;
    private boolean blackMustJump = false;
    public Board() {
        redPiecesList = new ArrayList<>();
        blackPiecesList = new ArrayList<>();
        allPossibleMovesRed = new HashMap<>();
        allPossibleMovesBlack = new HashMap<>();
        board = new Piece[COORDS][COORDS];
        this.setUp();
    }
    private void setUp() {
        for (int i = BLACKSTART; i <=BLACKEND; i++) {
            int start;
            if (i%2 == 0) {
                start = 1;
            }
            else {
                start = 0;
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
                start = 1;
            }
            else {
                start = 0;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = new Piece("red", j, i);
                board[j][i] = currPiece;
                redPiecesList.add(currPiece);
            }
        }
    }
    protected void  allBlackMoves() {
        redMustJump = false;
        for (int i = 0; i<COORDS; i ++) {
            int start;
            if (i%2 == 0) {
                start = 1;
            }
            else {
                start = 0;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = board[j][i];
                if (!currPiece.isBlack()) {
                    Map<String, Map<Integer, Integer>> result = possibleMovesBlack(j, i, currPiece);
                    if (result.containsKey("jump")) {
                        blackMustJump = true;
                    }
                    if (result!= null) {
                        allPossibleMovesBlack.put(currPiece,possibleMovesRed(j, i, currPiece));
                    }
                }

            }
        }
        if (blackMustJump) {
            Iterator<Map.Entry<Piece, Map<String, Map<Integer, Integer>>>> it = allPossibleMovesBlack.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Piece, Map<String, Map<Integer, Integer>>> curr = it.next();
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

    }
//    protected Map<String, Map<Integer, Integer>>getPieceMove(int x, int y, Piece currPiece) {
//    }
    protected Map<String, Map<Integer, Integer>>  possibleMovesBlack(int x, int y, Piece currPiece) {
        Map<Integer, Integer> map = new HashMap<>();
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("coordinates is outside the board");
        }
        if (!currPiece.isBlack()) {
            throw new IllegalArgumentException("Piece is not black");
        }
        Map<String, Map<Integer, Integer>>moves= new HashMap<>();
        Map<Integer, Integer>  jumpMove = new HashMap<>();
        Map<Integer, Integer> normalMove = new HashMap<>();
        if (blackCanJumpLeft(x, y)) {
            System.out.println("jump left");
            blackMustJump = true;
            jumpMove.put(x+1, y+1);
        }
        else if (inBounds(x+1, y+1) && blackMustJump == false && search(x+1, y+1) ==null) {
            System.out.println("normal move left");
            normalMove.put(x+1, y+1);
        }
        if (blackCanJumpRight(x, y)) {
            blackMustJump = true;
            jumpMove.put(x-1, y+1);
        }
        else if (inBounds(x-1, y+1) && blackMustJump == false && search(x-1, y+1) ==null) {
            normalMove.put(x-1, y+1);
        }
        if (currPiece.isKing()) {
            if (redCanJumpBackLeft(x, y)) {
                blackMustJump = true;
                jumpMove.put(x+1, y-1);
            }
            else if (inBounds(x+1, y-1) && blackMustJump == false && search(x+1, y-1) ==null) {
                normalMove.put(x+1, y-1);
            }
            if (redCanJumpBackRight(x, y)) {
                blackMustJump = true;
                jumpMove.put(x-1, y-1);
            }
            else if (inBounds(x-1, y-1) && blackMustJump == false && search(x+1, y-1) ==null) {
                normalMove.put(x-1, y-1);
            }
        }
        if (jumpMove.size() == 0&& normalMove.size()!=0) {
            blackMustJump = false;
            moves.put("normal", normalMove);
        }
        else if (jumpMove.size() != 0 && normalMove.size() != 0) {
            blackMustJump = true;
            moves.put("jump", jumpMove);
        }
        return moves;

    }
    protected Map<String, Map<Integer, Integer>> possibleMovesRed( int x, int y, Piece currPiece) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("coordinates is outside the board");
        }

        if (currPiece.isBlack()) {
            throw new IllegalArgumentException("Piece is not red");
        }
        Map<String, Map<Integer, Integer>>moves= new HashMap<>();

        Map<Integer, Integer>  jumpMove = new HashMap<>();
        Map<Integer, Integer> normalMove = new HashMap<>();
        if (redCanJumpLeft(x, y)) {
            redMustJump = true;

            jumpMove.put(x-1, y-1);
        }
        else if (inBounds(x-1, y-1) &&  redMustJump == false && search(x-1, y-1) ==null) {
            normalMove.put(x-1, y-1);
            System.out.println("red move left");
        }
        System.out.println("search " + search(x+1, y-1));
        if (redCanJumpRight(x, y)) {
            redMustJump = true;
            jumpMove.put(x+1, y-1);
        }
        else if (inBounds(x+1, y-1) &&  redMustJump == false && search(x+1, y-1) ==null) {
            System.out.println("red move right");
            normalMove.put(x+1, y-1);

        }

        if (currPiece.isKing()) {
            if (redCanJumpBackLeft(x, y)) {
                redMustJump = true;
                jumpMove.put(x-1, y+1);
            }
            else if (inBounds(x-1, y+1) && redMustJump == false && search(x-1, y+1) ==null) {
                normalMove.put(x-1, y+1);
            }
             if (redCanJumpBackRight(x, y)) {
                 redMustJump = false;
               jumpMove.put(x+1, y+1);
            }
            else if (inBounds(x-1, y+1) && redMustJump == false && search(x+1, y+1) ==null) {
                normalMove.put(x+1, y+1);
             }
        }
        if (jumpMove.size() == 0 && normalMove.size() != 0) {
            redMustJump = false;
            moves.put("normal", normalMove);
        }
        else if (jumpMove.size() !=0 && normalMove.size() != 0) {
            redMustJump = true;
            moves.put("jump", jumpMove);
        }
        System.out.println(jumpMove);
        System.out.println(normalMove);
        return moves;
    }
    protected void  allRedMoves() {
        redMustJump = false;
        for (int i = 0; i<COORDS; i ++) {
            int start;
            if (i%2 == 0) {
                start = 1;
            }
            else {
                start = 0;
            }
            for (int j = start; j<COORDS; j+=2) {
                Piece currPiece = board[j][i];
                if (!currPiece.isBlack()) {
                    Map<String, Map<Integer, Integer>> result = possibleMovesRed(j, i, currPiece);
                    if (result.containsKey("jump")) {
                        redMustJump = true;
                    }
                    if (result!= null) {
                        allPossibleMovesRed.put(currPiece,possibleMovesRed(j, i, currPiece));
                    }
                }

            }
        }
        if (redMustJump) {
            Iterator<Map.Entry<Piece, Map<String, Map<Integer, Integer>>>> it = allPossibleMovesRed.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Piece, Map<String, Map<Integer, Integer>>> curr = it.next();
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
            if (checkNeighborIsRed(x+1, y+1)) {
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
            if (checkNeighborIsRed(x+1, y-1)) {
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
            if (checkNeighborIsRed(x-1, y+1)) {
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
        if (inBounds(x-1, y+1)) {
            if (checkNeighborIsRed(x-1, y-1)) {
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
            if (!checkNeighborIsRed(x-1, y-1)) {
                if (!inBounds(x - 2, y - 2)) {
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
            if (!checkNeighborIsRed(x-1, y+1)) {
                if (!inBounds(x - 2, y + 2)) {
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
            if (!checkNeighborIsRed(x+1, y-1)) {
                if (!inBounds(x + 2, y - 2)) {
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
            if (!checkNeighborIsRed(x+1, y+1)) {
                if (!inBounds(x + 2, y +2)) {
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
        Map<Piece, Map<String, Map<Integer, Integer>>> curr = null;
        if (currPiece.isBlack()) {
            curr = allPossibleMovesBlack;
        }
        else  {
            curr = allPossibleMovesRed;
        }
        if (!curr.containsKey(currPiece)) {
            return false;
        }
        Iterator<Map.Entry<String, Map<Integer, Integer>>> it = curr.get(currPiece).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<Integer, Integer>> next = it.next();
            if (next.getValue().containsKey(xEnd)) {
                if (next.getValue().get(xEnd) == yEnd) {
                    return true;
                }
            }

        }
        return false;

    }
    protected Piece[][] getBoard() {
        return board;
    }
    public Map<Piece,Map<String, Map<Integer, Integer>>>getMovablePiecesRed() {
        return ImmutableMap.copyOf(allPossibleMovesRed);
    }
    public Map<Piece,Map<String, Map<Integer, Integer>>>getMovablePiecesBlack() {
        return ImmutableMap.copyOf(allPossibleMovesBlack);
    }
    protected void move(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
        Map<Piece, Map<String, Map<Integer, Integer>>> check = null;
        if (currPiece.isBlack()) {
            check = allPossibleMovesBlack;
        }
        else {
            check = allPossibleMovesRed;
        }
        if (!validMove(xStart, yStart, xEnd, yEnd, currPiece)) {
            throw  new IllegalArgumentException("move is not valid");
        }

        if(allPossibleMovesBlack.containsKey("jump")) {
            normalMove(xStart, yStart, xEnd, yEnd, currPiece);
        }
        else {
            jumpMove(xStart, yStart, xEnd, yEnd, currPiece);
        }

        normalMove(xStart, yStart, xEnd, yEnd, currPiece);
    }
    private void normalMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {

        board[xEnd][yEnd] = currPiece;
        currPiece.setCoordinates(xEnd, yEnd);
        board[xStart][xEnd] = null;
        currPiece.kingMe();
    }
    private void jumpMove(int xStart, int yStart, int xEnd, int yEnd, Piece currPiece) {
        board[xEnd][yEnd] = currPiece;
        currPiece.setCoordinates(xEnd, yEnd);
        int jumpPieceXCoordiante = (xStart + xEnd) /2;
        int jumpPieceYCoordiante = (yStart + yEnd) /2;
        board[xStart][xEnd] = null;
        if (currPiece.isBlack()) {
            redPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
        }
        else {
            blackPiecesList.remove(search(jumpPieceXCoordiante, jumpPieceYCoordiante));
        }
        currPiece.kingMe();
        board[jumpPieceXCoordiante][jumpPieceYCoordiante] = null;
    }
    public List<Piece> getRedPiecesList() {
        return ImmutableList.copyOf(redPiecesList);
    }
    public List<Piece> getBlackPiecesList() {
        return ImmutableList.copyOf(blackPiecesList);
    }
    protected void kingMe(Piece piece) {
        if (piece.isBlack()) {
            if (piece.getXCoordinate()== 7) {
                piece.kingMe();
            }
        }
        else {
            if (piece.getXCoordinate() ==0) {
                piece.kingMe();
            }
        }
    }
}
