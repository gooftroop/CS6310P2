package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import simulation.util.GridCell;

public final class Earth {
	
	private final float INITIAL_TEMP = 288;
	
	private final int DEFAULT_DEGREES = 15;
	private final int DEFAULT_SPEED = 1; // minute
	private final int MAX_DEGREES = 180;
	private final int MAX_SPEED = 1440;
	private final int SUN_START_POS = 0;
	
	private final int[] increments = {6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180};
	
	private GridCell prime = null;
	
	private int sun;
	private int speed = DEFAULT_SPEED;
	private int gs = DEFAULT_DEGREES;
	
	public Earth() {
		/* Empty */
	}
	
	public void configure(int gs, int speed) {
		
		if (gs <= 0 || gs > MAX_DEGREES)
			throw new IllegalArgumentException("Invalid grid spacing");
		
		if (speed <= 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("Invalid speed setting");
		
		this.speed = speed;
		
		// The following could be done better - if we have time, we should do so
		if (MAX_DEGREES % gs != 0) {
			for (int i = 1; i < increments.length; i++)
				if (increments[i] > gs) this.gs = increments[i - 1];
		} else this.gs = gs;
	}

	public void initializePlate() {
		
		int x = 0, y = 0;
		
		// do a reset
		sun = SUN_START_POS;
		
		if (this.prime != null) this.prime.setTemp(INITIAL_TEMP);
		else this.prime = new GridCell(INITIAL_TEMP, x, y);
		this.prime.setTop(null);

		/*
		 * ok, so we start at the prime meridian (long. 0) and at the north pole (+90).
		 * we construct the globe by working our way down from +90 to -90, and westward,
		 * from 0 to 180.
		 * 
		 * The cells around each poles will have a bottom, left, and right, but no top (they are triangles)
		 */
		
		// TODO We still need to set lat/long...any other attributes? for each cell
		 
		int spacing = (MAX_DEGREES / this.gs);
		
		// North Pole
		GridCell next = null, curr = this.prime;
		for (x = 0; x < spacing; x++) {
			
			if (curr.getLeft() != null) curr.getLeft().setTemp(INITIAL_TEMP);
			else {
				next = new GridCell(null, null, null, curr, INITIAL_TEMP, x, y);
				curr.setLeft(next);
			}
			curr = curr.getLeft();
		}
		
		// Stitch the grid row together
		this.prime.setRight(curr);
		curr.setLeft(this.prime);
		
		// Create each grid row, with the exception of the south pole
		GridCell top = this.prime.getLeft(), left = null;
		for (y = 1; y < spacing - 1; y++) {
			
			if (top.getBottom() != null) { 
				curr = top.getBottom();
				curr.setTemp(INITIAL_TEMP);
			} else {
				curr = new GridCell(top, null, null, null, INITIAL_TEMP, 0, y);
				top.setBottom(curr);
			}
			
			for (x = 1; x < spacing; x++) {
				
				if (curr.getLeft() != null) curr.getLeft().setTemp(INITIAL_TEMP);
				else {
					next = new GridCell(top, null, null, curr, INITIAL_TEMP, x, y);
					curr.setLeft(next);
				}
				
				top = top.getLeft();
				curr = curr.getLeft();
			}
			
			left = top.getBottom(); // This should be the first cell we created
			
			// Stitch the grid row together
			curr.setLeft(left);
			left.setRight(curr);
			top = left;
			
		}
		
		if (top.getBottom() != null) { 
			curr = top.getBottom();
			curr.setTemp(INITIAL_TEMP);
		} else {
			curr = new GridCell(top, null, null, null, INITIAL_TEMP, 0, y);
			top.setBottom(curr);
		}
		
		// South Pole
		for (x = 1; x < MAX_DEGREES / this.gs; x++) {
			
			if (curr.getLeft() != null) { 
				curr.getLeft().setTemp(INITIAL_TEMP);
			} else {
				next = new GridCell(top, null, null, curr, INITIAL_TEMP, x, y);
				curr.setLeft(next);
			}
			curr = curr.getLeft();
			top = top.getLeft();
		}
		
		left = top.getBottom(); // This should be the first cell we created
		
		// Stitch the grid row together
		curr.setLeft(left);
		left.setRight(curr);
	}
	
	public void run() {
		
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();
		
		while(true) {
			
			bfs.add(this.prime);
			this.prime.visited(true);
			
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
			}
		}
	}
}
