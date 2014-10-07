package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import simulation.util.GridCell;

public final class Earth {
	
	// These two *were* found in an abstract class, but moved up here in case 
	// we don't need that abstract class
	public final float INITIAL_TEMP = 288;
	
	private GridCell root = null;
	
	private int height = 0, width = 0;
	
	public Earth() {
		/* Empty */
	}
	
	public Earth(int height, int width) {
		
		this.setup(height, width);
	}
	
	public void setup(int width, int height) {
		
		if (height < 0 || height >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid height dimension");
		
		if (width < 0 || width >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid width dimension");
		
		this.height = height;
		this.width = width;
		
		this.initializePlate();
	}

	public void initializePlate() {
		
		int y = 0, x = 0;
		
		if (this.root != null) this.root.setTemp(INITIAL_TEMP);
		else this.root = new GridCell(INITIAL_TEMP, x, y);
		
		GridCell top = null, left = null, bottom = null, curr = root;
		for (; y < this.height; y++) {
			
			x = 0;
			
			if (curr.getRight() != null) curr.getRight().setTemp(INITIAL_TEMP);
			else  {
				curr.setRight(new GridCell(INITIAL_TEMP, x, y));
				curr.getRight().setLeft(curr);
			}
			
			left = curr.getLeft();
			top = curr.getRight();
			
			for (x = 1; x < this.width; x++) {
				
				if (curr.getBottom() != null) { 
					curr.getBottom().setTemp(INITIAL_TEMP);
					if (x == this.width - 1) curr.setRight(null);
				} else  {
					bottom = new GridCell(INITIAL_TEMP, x, y); 
					curr.setBottom(bottom);
					bottom.setTop(curr);
					
					if (left != null) {
						left = left.getBottom();
						left.setRight(bottom);
					}
					
					bottom.setLeft(left);
				}
				
				curr = curr.getBottom();
			}
			
			curr.setBottom(null);
			curr = top;
		}
	}
	
	public void run() {
		
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();
		
		while(true) {
			
			bfs.add(this.root);
			this.root.visited(true);
			
			while(!bfs.isEmpty()) {
				
				GridCell point = bfs.remove();
				calcd.add(point);
				
				// TODO This needs testing. Should work though.
				GridCell c = calcd.peek();
				if (c != null) {
					Iterator<GridCell> itr = c.getChildren(false);
					if (!itr.hasNext()) {
						c.visited(false);
						c.swapTemp();
						calcd.poll();
					}
				}
				// done TODO
				
				GridCell child = null;
				Iterator<GridCell> itr = point.getChildren(false);
				while(itr.hasNext()) {
					child = itr.next();
					child.visited(true);
					child.calculateTemp();
					bfs.add(child);
				}
				
				// this.update(point.getTemp(), point.x, point.y); // We can use a ResultsHandler to update the grid to send...
			}
		}
	}
}
