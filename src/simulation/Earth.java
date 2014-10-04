package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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
		
		int x = 0, y = 0;
		
		this.root = new GridCell(INITIAL_TEMP, x, y);
		
		
	}
	
	private GridCell createPlate(int x, int y) {
		
		if (x > this.width || y > this.height) return null;
		
		// how to deal with stuff existing?
		
		GridCell curr = new GridCell(this.INITIAL_TEMP, x, y);
		
		GridCell n = this.createPlate(x, y + 1);
		curr.setBottom(n);
		n.setTop(curr);
		
		n = this.createPlate(x, y);
		curr.setRight(n);
		n.setLeft(curr);
		
		n = this.createPlate(x, y);
		curr.setLeft(n);
		n.setRight(curr);
		
		return curr;
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
