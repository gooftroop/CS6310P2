package simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class GridCell implements Cell<GridCell> {
	
	public static final float AVG = 4;
	
	public final int x, y;
	
	private boolean visited;
	private float currTemp, newTemp;
	
	private GridCell top = null, bottom = null, left = null, right = null;
	
	public GridCell(float temp, int x, int y) {
		
		if (temp > Double.MAX_VALUE) throw new IllegalArgumentException("Invalid temp provided");
		if (x > Integer.MAX_VALUE || x < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'x' provided");
		if (y > Integer.MAX_VALUE || y < Integer.MIN_VALUE) throw new IllegalArgumentException("Invalid 'y' provided");
		
		this.x = x;
		this.y = y;
		
		this.setTemp(temp);
		this.visited = false;
	}
	
	public GridCell(GridCell top, GridCell bottom, GridCell left, GridCell right, float temp, int x, int y) {
		
		this(temp, x, y);
		
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
		this.currTemp = temp;
	}
	
	@Override
	public float calculateTemp() {
		this.newTemp = (this.top.getTemp() + this.bottom.getTemp() + this.right.getTemp() + this.left.getTemp()) / AVG;
		return this.newTemp - this.currTemp;
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
}