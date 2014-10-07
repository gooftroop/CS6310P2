package EarthSim.view;

import util.GridDisplayPane.GridDisplayable;

// Provides generated data for testing visualization

public class TestEarthData implements GridDisplayable {
	private int rows;
	private int cols;
	private Boolean invert;
	private int rowOffset; // used to circularly shift pattern along rows
	private int colOffset; // used to circularly shift pattern along cols

	public TestEarthData() {
		this(10);
	}

	public TestEarthData(int rows) {
		this(rows, rows);
	}

	public TestEarthData(int rows, int cols) {
		this(rows, cols, false);
	}

	public TestEarthData(int rows, int cols, Boolean invert) {
		this(rows, cols, invert, 0, 0);
	}

	public TestEarthData(int rows, int cols, Boolean invert, int rowOffset, int colOffset) {
		this.rows = rows;
		this.cols = cols;
		this.invert = invert;
		this.rowOffset = rowOffset;
		this.colOffset = colOffset;
	}

	@Override
	public float getDisplayValue(int row, int col) {
		// Generate a test pattern for display
		// for now we'll just checker board getting fainter as you advance to
		// right (except that first row is completely on and last completely off
		
		// Apply offset values
		row = (row+rowOffset) % getNumRows();
		col = (col+colOffset) % getNumCols();
		
		double initialVal = (row + col) % 2;
		if( row == 0 ) {
			initialVal = 1.0;
		}
		else if (row == getNumRows()-1) {
			initialVal = 0.0;
		}
		if (invert) {
			initialVal = (initialVal + 1) % 2;
		}
		double rightGradientScale = 1.0 - ((double)col / getNumCols());
//		System.out.printf("row/col/val %d/%d/%f\n", row, col, (float)(initialVal*rightGradientScale));
		return (float) (initialVal * rightGradientScale);
	}

	@Override
	public int getNumRows() {
		return rows;
	}

	@Override
	public int getNumCols() {
		return cols;
	}

}
