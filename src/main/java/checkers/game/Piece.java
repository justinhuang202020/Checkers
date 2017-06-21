package checkers.game;

/**
 * Created by Justin on 6/7/2017.
 */
public class Piece {
    private boolean isKing = false;
    private String pieceColor;
    private int xPosition;
    private int yPosition;


    public Piece(String color, int initialXCoordinate, int initialYCoordinate) {
        System.out.println(color);
        if (!color.equals("red") && !color.equals("black")) {
            throw new IllegalArgumentException();
        }
        pieceColor = color;
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
}
