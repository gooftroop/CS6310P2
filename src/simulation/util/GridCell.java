package simulation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// for testing
//import java.lang.Integer;

import simulation.Earth;

public final class GridCell implements EarthCell<GridCell> {

	// gs: grid spacing
	public int x, y, latitude, longitude, gs;
	
	// average temperature
	private static float avgtemp;

	private boolean visited;
	private float currTemp, newTemp, avgArea;

	private GridCell top = null, bottom = null, left = null, right = null;

	// Cell properties: surface area, perimeter
	private float lv, lb, lt, surfarea, pm;

	public GridCell(float temp, int x, int y, int latitude, int longitude, int gs, float avgArea) {

		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");

		this.setGridProps(x, y, latitude, longitude, gs, avgArea);

		this.setTemp(temp);
		this.visited = false;
		
		//System.out.println(Integer.toString(x)+ "," + Integer.toString(y));
	}

	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y, int latitude, int longitude, int gs, float avgArea) {
		
		this(temp, x, y, latitude, longitude, gs, avgArea);

		this.setTop(top);
		this.setBottom(bottom);
		this.setLeft(left);
		this.setRight(right);
	}

	@Override
	public void setTop(GridCell top) {

		if (top == null) return;
		this.top = top;
	}

	@Override
	public GridCell getTop() {
		return this.top;
	}

	@Override
	public void setBottom(GridCell bottom) {

		if (bottom == null) return;
		this.bottom = bottom;
	}

	@Override
	public GridCell getBottom() {
		return this.bottom;
	}

	@Override
	public void setRight(GridCell right) {

		if (right == null) return;
		this.right = right;
	}

	@Override
	public GridCell getRight() {
		return this.right;
	}

	@Override
	public void setLeft(GridCell left) {

		if (left == null) return;
		this.left = left;
	}

	@Override
	public GridCell getLeft() {
		return this.left;
	}

	@Override
	public float getTemp() {
		return this.currTemp;
	}

	@Override
	public void setTemp(float temp) {

		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		this.currTemp = temp;
	}

	@Override
	public void setGridProps(int x, int y, int latitude, int longitude, int gs, float avgArea) {

		this.setX(x);
		this.setY(y);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setGridSpacing(gs);
		this.setAverageArea(avgArea);

		// calc lengths, area, etc.
		this.calSurfaceArea(latitude, gs);
	}

	@Override
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	@Override
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this. y = y;
	}
	
	@Override
	public void setAverageArea(float avgArea) {
		this.avgArea = avgArea;
	}
	
	@Override
	public float getAverageArea() {
		return this.avgArea;
	}

	@Override
	public float calculateTemp(int sunPosition) {
		//return this.currTemp + calTsun(sunPosition) + calTcool() + calTneighbors(); // new temp
		this.newTemp = this.currTemp + calTsun(sunPosition) + calTcool() + calTneighbors();
		//System.out.println(this.currTemp);
		return this.newTemp; // new temp
	}

	@Override
	public void swapTemp() {
		this.currTemp = this.newTemp;
		//System.out.println(this.newTemp);
		this.newTemp = 0;
	}

	@Override
	public void visited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public Iterator<GridCell> getChildren(boolean visited) {
		List<GridCell> ret = new ArrayList<GridCell>();

		if (this.top != null 	&& this.top.visited == visited) 	ret.add(this.top);
		if (this.bottom != null && this.bottom.visited == visited) 	ret.add(this.bottom);
		if (this.left != null 	&& this.left.visited == visited) 	ret.add(this.left);
		if (this.right != null 	&& this.right.visited == visited) 	ret.add(this.right);

		return ret.iterator();
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getLatitude() {
		return this.latitude;
	}

	@Override
	public int getLongitude() {
		return this.longitude;
	}

	@Override
	public void setGridSpacing(int gs) {
		this.gs = gs;
	}

	@Override
	public int getGridSpacing() {
		return this.gs;
	}

	public static void setAvgtemp(float avg){
		avgtemp = avg;
	}
	
	public static float getAvgtemp(){
		return avgtemp;
	}
	
	private void calSurfaceArea(int latitude, int gs) {
		
		double p  = 1.0 * gs / 360;
		this.lv   = (float) (Earth.CIRCUMFERENCE * p);
		this.lb   = (float) (Math.cos(Math.toRadians(latitude)) * this.lv);
		this.lt   = (float) (Math.cos(Math.toRadians(latitude + gs)) * this.lv);
		double h  = Math.sqrt(Math.pow(this.lv,2) - 1/4 * Math.pow((this.lb - this.lt), 2));

		this.pm = (float) (this.lt + this.lb + 2 * this.lv);
		this.surfarea =  (float) (1.0/2 * (this.lt + this.lb) * h);
	}

	private float calTsun(int sunPosition) {
		int   sunLongitude      = getSunLocationOnEarth(sunPosition);
		float attenuation_lat   = (float) Math.cos(Math.toRadians(this.latitude));
		float attenuation_longi = (float) (( (Math.abs(sunLongitude - this.longitude) % 360 ) < 90 ) ? Math.cos(Math.toRadians(sunLongitude - this.longitude)) : 0);

		return 278 * attenuation_lat * attenuation_longi;
	}

	// A help function for get the Sun's corresponding location on longitude.
	private int getSunLocationOnEarth(int sunPosition) {
		// Grid column under the Sun at sunPosition
		int cols = 360 / this.gs;
		int j    = sunPosition;
		return j < (cols / 2) ? -(j + 1) * this.gs : (360) - (j + 1) * this.gs;
	}

	private float calTcool() {
		float beta = (float) (this.surfarea / avgArea);  // actual grid area / average cell area
		float tempfactor = this.currTemp / avgtemp;

		return -1 * beta * tempfactor * this.currTemp;
	}

	private float calTneighbors() {
		float top_temp = 0, bottom_temp = 0;
		
		if (this.top != null) 	top_temp = this.lt / this.pm * this.top.getTemp();
		if (this.bottom != null) 	bottom_temp = this.lb / this.pm * this.bottom.getTemp();
		
		return  top_temp + bottom_temp + this.lv / this.pm * (this.left.getTemp() + this.right.getTemp());
	}

}
