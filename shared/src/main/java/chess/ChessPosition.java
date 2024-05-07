package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row - 1; // Subtract 1 to convert from 1-based to 0-based index
        this.col = col - 1; // Subtract 1 to convert from 1-based to 0-based index
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row; // Add 1 to convert back to 1-based index
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col; // Add 1 to convert back to 1-based index
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition other = (ChessPosition) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }
}
