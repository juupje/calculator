package helpers;

import main.Operator;

/**
 * @author Joep Geuskens 
 * This class is basically a container for the amount 
 */
public class Dimension {
	public int rows = 0, cols = 0;
	
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
	
	public Dimension copy() {
		return new Dimension(rows, cols);
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