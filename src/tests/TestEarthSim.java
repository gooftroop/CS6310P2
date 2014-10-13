package tests;

// Test simulation method in EarthSim
import simulation.Earth;
import simulation.util.GridCell;

public class TestEarthSim {
	public static void main(String [] args){
		Earth earth = new Earth();
		earth.configure(6, 10);
		earth.initializePlate();
		GridCell prime = earth.getGrid();
		earth.run();
	}

}
