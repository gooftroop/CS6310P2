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
		else this.prime = new GridCell(INITIAL_TEMP, x, y, this.getLatitude(), this.getLongitude());
		this.prime.setTop(null);

		/*
		 * ok, so we start at the prime meridian (long. 0) and at the north pole (+90).
		 * we construct the globe by working our way down from +90 to -90, and westward,
		 * from 0 to 180.
		 * 
		 * The cells around each poles will have a bottom, left, and right, but no top (they are triangles)
		 */
		
		// TODO We still need to set lat/long...any other attributes? for each cell
		// TODO This all could probably be condensed, modularized, and optimized. Do this if time allows.
		 
		int spacing = (MAX_DEGREES / this.gs);
		
		// South Pole
		GridCell next = null, curr = this.prime;
		for (x = 0; x < spacing; x++) {
			
			this.createRowCell(curr, next, null, x, y);
			curr = curr.getLeft();
		}
		
		// Stitch the grid row together
		this.prime.setRight(curr);
		curr.setLeft(this.prime);
		
		// Create each grid row, with the exception of the south pole
		GridCell bottom = this.prime.getLeft(), left = null;
		for (y = 1; y < spacing - 1; y++) {
			
			this.createNextRow(bottom, curr, y);
			this.createRow(curr, next, bottom, left, spacing, y);
			bottom = left;
			
		}
		
		this.createNextRow(bottom, curr, y);
		
		// North Pole
		this.createRow(curr, next, bottom, left, spacing, y);
	}
	
	private void createRow(GridCell curr, GridCell next, GridCell bottom, GridCell left, int spacing, int y) {
		
		for (int x = 1; x < spacing; x++) {
			
			this.createRowCell(curr, next, bottom, x, y);
			bottom = bottom.getLeft();
			curr = curr.getLeft();
		}
		
		left = bottom.getTop(); // This should be the first cell we created
		
		// Stitch the grid row together
		curr.setLeft(left);
		left.setRight(curr);
	}
	
	private void createRowCell(GridCell curr, GridCell next, GridCell bottom, int x, int y) {
		
		if (curr.getLeft() != null) { 
			GridCell l = curr.getLeft();
			l.setTemp(INITIAL_TEMP);
			l.setGridProps(x, y, this.getLatitude(), this.getLongitude());
		} else {
			next = new GridCell(null, bottom, null, curr, INITIAL_TEMP, x, y, this.getLatitude(), this.getLongitude());
			curr.setLeft(next);
		}
	}
	
	private void createNextRow(GridCell bottom, GridCell curr, int y) {
		
		if (bottom.getTop() != null) { 
			curr = bottom.getTop();
			curr.setTemp(INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(), this.getLongitude());
		} else {
			curr = new GridCell(null, bottom, null, null, INITIAL_TEMP, 0, y, this.getLatitude(), this.getLongitude());
			bottom.setTop(curr);
		}
	}
	
	private int getLatitude() {
		return 0; // TODO
	}
	
	private int getLongitude() {
		return 0; // TODO
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
