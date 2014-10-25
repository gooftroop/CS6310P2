package common;

public interface IGrid {
	
	public void setTemperature(int x, int y, float temp);
	
	public float getTemperature(int x, int y);
	
	public float getSunPositionDeg();
	
	public int getCurrentTime();
	
	public int getGridWidth();
	
	public int getGridHeight();

}
