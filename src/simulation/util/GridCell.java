package simulation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class GridCell implements EarthCell<GridCell> {
	
	public static final float AVG = 4;
	
	public int x, y, latitude, longitude, p;
	
	private boolean visited;
	private float currTemp, newTemp;
	
	private GridCell top = null, bottom = null, left = null, right = null;
	
	public GridCell(float temp, int x, int y, int latitude, int longitude, int p) {
		
		if (temp > Float.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");
		
		this.setGridProps(x, y, latitude, longitude, p);
		
		this.setTemp(temp);
		this.visited = false;
	}
	
	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y, int latitude, int longitude, int p) {
		
		this(temp, x, y, latitude, longitude, p);
		
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
	public void setGridProps(int x, int y, int latitude, int longitude, int p) {
		
		this.setPorportion(p);
		this.setX(x);
		this.setY(y);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		
		// calc lengths, area, etc. 
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
		return this.latitude;
	}

	@Override
	public int getLongitude() {
		return this.longitude;
	}

	@Override
	public void setPorportion(int p) {
		this.p = p;
	}

	@Override
	public int getPorportion() {
		return this.p;
	}
}