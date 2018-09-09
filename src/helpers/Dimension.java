package helpers;

/**
 * @author Joep Geuskens 
 * This class is basically a container for the amount of
 *         rows and columns in the MMatrix.
 */
public class Dimension {
	private int rows = 0;
	public int cols = 0;

	public Dimension(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}

	public int rows() {
		return rows;
	}

	public int cols() {
		return cols;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Dimension)
			return ((Dimension) other).cols() == cols && ((Dimension) other).rows() == rows;
		return false;
	}

	@Override
	public String toString() {
		return "(" + rows + ", " + cols + ")";
	}
}