package edu.brown.cs.checkers.game;

/**
 * checker Piece that knows its own position 
 * Created by Justin on 6/7/2017.
 */
public class Piece {
    private boolean isKing = false;
    private String pieceColor;
    private int xPosition;
    private int yPosition;


    public Piece(String color, int initialXCoordinate, int initialYCoordinate) {
    	//color has to be red or black
        if (!color.equals("red") && !color.equals("black")) {
            throw new IllegalArgumentException();
        }
        pieceColor = color;
        //checks that its intial coordinate is in bounds
        assert(initialXCoordinate<8 && initialXCoordinate>=0);
        assert(initialYCoordinate<8 && initialYCoordinate>=0);
        xPosition = initialXCoordinate;
        yPosition = initialYCoordinate;
    }
    protected void kingMe() {
        isKing = true;
    }
    public boolean isBlack() {
        if (pieceColor == "black") {
            return true;
        }
        return false;
    }
    public boolean isKing() {
        return isKing;
    }
    /**
     *sets the new coordinates of the piece
     * @param xCoordinate
     * @param yCoordinate
     */
    protected void setCoordinates(int xCoordinate, int yCoordinate) {

        assert(xCoordinate<8 && yCoordinate>=0);
        assert(yCoordinate<8 && yCoordinate>=0);
        xPosition = xCoordinate;
        yPosition = yCoordinate;

    }
    public int getXCoordinate() {
        return xPosition;
    }
    public int getYCoordinate() {
        return yPosition;
    }
    @Override
    public String toString() {
    	StringBuilder build = new StringBuilder();
    	build.append(pieceColor);
    	build.append(" checker piece at x coordinate ");
    	build.append(xPosition);
    	build.append(" and at y coordinate ");
    	build.append(yPosition);
    	return build.toString();
    }
}
