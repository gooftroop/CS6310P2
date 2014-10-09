package util;


public class Grid implements IGrid {
	
	// Used to transport the temps in the buffer
	private final int sunPosition, width, height;
	
	public Grid(int sunPosition, int width, int height) {
		
		this.sunPosition = sunPosition;
		this.width = width;
		this. height = height;
	}

	@Override
	public float getTemperature(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSunPosition() {
		return this.sunPosition;
	}

	@Override
	public int getGridWidth() {
		return this.width;
	}

	@Override
	public int getGridHeight() {
		return this.height;
	}
}
