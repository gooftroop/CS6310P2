package tests;

// Test simulation method in EarthSim
import simulation.Earth;
import simulation.util.GridCell;

public class TestEarthSim {
//	public static void main(String [] args){
//		Earth earth = new Earth();
//		earth.configure(90, 10);
//		GridCell primeRef = Earth.getGrid();
//		earth.initializePlate();
//		//earth.run();
//		printGrid(primeRef);
//	}
	
	private static void printGrid(){
		
		GridCell curr = iniposition;
		int height = Earth.getHeight();
		int width  = Earth.getWidth();
		System.out.println(height);
		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				System.out.println("test");
				GridCell rowgrid = curr.getLeft();
				System.out.println(rowgrid.getTemp());
			}
			curr = curr.getTop();
		}
	}
}
