package simulation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import simulation.Earth;

public final class GridCell implements EarthCell<GridCell> {
	
	public static final float AVG = 4;
	
	// gs: grid spacing
	public int x, y, latitude, longitude, gs;
	
	// avg: average grid cell area 
	public float avg;
	
	private boolean visited;
	private float currTemp, newTemp;
	
	private GridCell top = null, bottom = null, left = null, right = null;
	
	// Cell properties: surface area, perimeter
	private float lv, lb, lt, surfarea, pm;
	private float avgtemp;

	public GridCell(float temp, int x, int y, int latitude, int longitude, int gs, float avg) {
		
		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");
		
		this.setGridProps(x, y, latitude, longitude, gs, avg);
		
		this.setTemp(temp);
		this.visited = false;
	}
	
	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y, int latitude, int longitude, int gs, float avg) {
		
		this(temp, x, y, latitude, longitude, gs, avg);
		
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
	public void setGridProps(int x, int y, int latitude, int longitude, int gs, float avg) {
		
		this.setX(x);
		this.setY(y);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setGridSpacing(gs);
		this.setAverageGridArea(avg);
		
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
	public float calculateTemp(int sunPosition) {
		return this.currTemp + calTsun(sunPosition) + calTcool() + calTneighbors(); // new temp
	}
	
	@Override
	public void swapTemp() {
		this.currTemp = this.newTemp;
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
	public float calculateTemp() {
		// Unused
		return 0;
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
	
	@Override
	public void setAverageGridArea(float avg) {
		this.avg = avg;
	}
	
	@Override
	public float getAverageGridArea() {
		return this.avg;
	}

	private void calSurfaceArea(int latitude, int gs) {
		double p  = gs / 360;
		this.lv   = (float) (Earth.CIRCUMFERENCE * p);
		this.lb   = (float) (Math.cos(Math.toRadians(latitude)) * this.lv);
		this.lt   = (float) (Math.cos(Math.toRadians(latitude + gs)) * this.lv);
		double h  = Math.sqrt(Math.pow(this.lv,2) - 1/4 * Math.pow((this.lb - this.lt), 2));
		
		this.pm = (float) (this.lt + this.lb + 2 * this.lv);
		this.surfarea =  (float) (1/2 * (this.lt + this.lb) * h);
	}
	
	private float calTsun(int sunPosition) {
		float attenuation_lat   = (float) Math.cos(Math.toRadians(this.latitude));
		float attenuation_longi = (float) (( (Math.abs(sunPosition - this.longitude) % 360 ) < 90 ) ? Math.cos(Math.toRadians(sunPosition - this.longitude)) : 0);
		
		return 278 * attenuation_lat * attenuation_longi;
	}
	
	private float calTcool() {
		float beta = this.surfarea / this.avg;
		// HERE IS THE AVERAGE TEMP USED
		float tempfactor = this.currTemp / this.avgtemp;
		
		return -1 * beta * tempfactor * this.currTemp;
	}
	
	private float calTneighbors() {
		return this.lt / this.pm * this.top.getTemp() + this.lb / this.pm * this.bottom.getTemp() + this.lv / this.pm * (this.left.getTemp() + this.right.getTemp());
	}
}