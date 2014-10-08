package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import concurrent.RunnableSim;
import simulation.util.GridCell;

public final class Earth implements RunnableSim {
	
	public static final double CIRCUMFERENCE 	= 4.003014 * Math.pow(10, 7);
	public static final double SURFACE_AREA 	= 5.10072 * Math.pow(10, 14);
	
	private static final float INITIAL_TEMP 	= 288;
	
	private static final int DEFAULT_DEGREES 	= 15;
	private static final int DEFAULT_SPEED 		= 1; // minutes
	private static final int MAX_DEGREES 		= 180;
	private static final int MAX_SPEED 			= 1440;
	private static final int SUN_START_POS 		= 0;
	
	private static final int[] increments = {6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180};
	
	private static int currentStep, width, height, sunPosition;
	
	private static GridCell prime 	= null;
	private static int speed 		= DEFAULT_SPEED;
	private int gs 					= DEFAULT_DEGREES;
	
	public void configure(int gs, int s) {
		
		if (gs <= 0 || gs > MAX_DEGREES)
			throw new IllegalArgumentException("Invalid grid spacing");
		
		if (speed <= 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("Invalid speed setting");
		
		speed = s;
		
		// The following could be done better - if we have time, we should do so
		if (MAX_DEGREES % gs != 0) {
			for (int i = 1; i < increments.length; i++)
				if (increments[i] > gs) this.gs = increments[i - 1];
		} else this.gs = gs;
	}

	public void initializePlate() {
		
		int x = 0, y = 0;
		
		// do a reset
		sunPosition = SUN_START_POS;
		currentStep = 0;
		
		if (prime != null) prime.setTemp(INITIAL_TEMP);
		else prime = new GridCell(INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x));
		prime.setTop(null);
		 
		width = (2 * MAX_DEGREES / this.gs);	// rows
		height = (MAX_DEGREES / this.gs);		// cols
		
		// South Pole
		GridCell next = null, curr = prime;
		for (x = 0; x < width; x++) {
			
			this.createRowCell(curr, next, null, x, y);
			curr = curr.getLeft();
		}
		
		// Stitch the grid row together
		prime.setRight(curr);
		curr.setLeft(prime);
		
		// Create each grid row, with the exception of the south pole
		GridCell bottom = prime.getLeft(), left = null;
		for (y = 1; y < height - 1; y++) {
			
			this.createNextRow(bottom, curr, y);
			this.createRow(curr, next, bottom, left, y);
			bottom = left;
			
		}
		
		this.createNextRow(bottom, curr, y);
		
		// North Pole
		this.createRow(curr, next, bottom, left, y);
	}
	
	public GridCell getGrid() {
		return prime;
	}
	
	public int getWidth() {
		return new Integer(width);
	}
	
	public int getHeight() {
		return new Integer(height);
	}
	
	public int getSunPosition() {
		return new Integer(sunPosition);
	}
	
	public void run() {
		
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		Queue<GridCell> calcd = new LinkedList<GridCell>();
		
		currentStep++;
		
		int t = speed * currentStep;
		int rotationalAngle = (t % MAX_SPEED) * 360 / MAX_SPEED;
		sunPosition = (width * (rotationalAngle / 360) + (width / 2) % width);
		
		while(true) {
			
			bfs.add(prime);
			prime.visited(true);
			
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
	
private void createRow(GridCell curr, GridCell next, GridCell bottom, GridCell left, int y) {
		
		for (int x = 1; x < width; x++) {
			
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
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x));
		} else {
			next = new GridCell(null, bottom, null, curr, INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x));
			curr.setLeft(next);
		}
	}
	
	private void createNextRow(GridCell bottom, GridCell curr, int y) {
		
		if (bottom.getTop() != null) { 
			curr = bottom.getTop();
			curr.setTemp(INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0));
		} else {
			curr = new GridCell(null, bottom, null, null, INITIAL_TEMP, 0, y, this.getLatitude(y), this.getLongitude(0));
			bottom.setTop(curr);
		}
	}
	
	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}
	
	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
}
