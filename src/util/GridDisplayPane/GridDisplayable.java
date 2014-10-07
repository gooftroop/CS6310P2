package util.GridDisplayPane;

/**
 * Interface to be displayable as grid
 */
public interface GridDisplayable {

	// Provides a scalar 0-1 value indicating display value 
	// for requested grid location
    public float getDisplayValue(int row, int col);

    // Accessors to determine grid size
    public int getNumRows();
    public int getNumCols();
}
