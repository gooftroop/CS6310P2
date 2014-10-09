package simulation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import simulation.Earth;

public final class GridCell implements EarthCell<GridCell> {
	
	public static final float AVG = 4;
	
	public int x, y, lat, longi;
	
	private boolean visited;
	private float currTemp, newTemp;
	
	private GridCell top = null, bottom = null, left = null, right = null;
	
	// Cell properties: surface area, perimeter
	private float surfarea, pm;
	
	public GridCell(float temp, int x, int y, int lat, int longi) {
		
		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");
		
		this.setGridProps(x, y, lat, longi);
		
		this.setTemp(temp);
		this.visited = false;
	}
	
	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y, int lat, int longi) {
		
		this(temp, x, y, lat, longi);
		
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
	public void setGridProps(int x, int y, int lat, int longi) {
		
		this.setX(x);
		this.setY(y);
		this.setLatitude(lat);
		this.setLongitude(longi);
		
		// calc lengths, area, etc. 
	}
	
	@Override
	public void setLatitude(int lat) {
		this.lat = lat;
	}
	
	@Override
	public void setLongitude(int longi) {
		this.longi = longi;
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
	public float calculateTemp(int time) {
		return 0; // new temp
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
		return this.lat;
	}

	@Override
	public int getLongitude() {
		return this.longi;
	}

	private float calSurfaceArea() {
		//double lv = Earth.CIRCUMFERENCE * ;
		double lv = Earth.CIRCUMFERENCE;
		double lb = Math.cos(Math.toRadians(lat));
		double lt = Math.cos(Math.toRadians(lat + gs));
		
		this.pm = (float) (lt + lb + 2 * lv);
		
		this.surfarea =  1 / 2 * (lt + lb) * h;
	}
}