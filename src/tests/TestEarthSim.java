package tests;

// Test simulation method in EarthSim
import simulation.Earth;
import simulation.util.GridCell;

public class TestEarthSim {
	public static void main(String [] args){
		Earth earth = new Earth();
		earth.configure(6, 10);
		earth.initializePlate();
		GridCell prime = Earth.getGrid();
		//earth.run();
		printGrid(prime);
	}
	
	private static void printGrid(GridCell iniposition){
		GridCell curr = iniposition;
		int height = Earth.getHeight();
		int width  = Earth.getWidth();
		for (int x = 1; x < height - 1; x++) {
			for (int y = 0; y < width; y++) {
				GridCell rowgrid = curr.getRight();
				System.out.println(rowgrid.getTemp());
			}
			curr = curr.getBottom();
		}
	}
}
