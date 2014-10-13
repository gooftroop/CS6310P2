package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import messaging.Message;
import simulation.util.EarthCell;
import simulation.util.GridCell;

import common.Grid;
import common.IGrid;

public final class Earth extends EarthEngine {
	
	public static final double CIRCUMFERENCE 	= 4.003014 * Math.pow(10, 7);
	public static final double SURFACE_AREA 	= 5.10072 * Math.pow(10, 14);
	
	private static final float INITIAL_TEMP 	= 288;
	
	private static final int DEFAULT_DEGREES 	= 15;
	private static final int DEFAULT_SPEED 		= 1; // minutes
	private static final int MAX_DEGREES 		= 180;
	private static final int MAX_SPEED 			= 1440;
	private static final int SUN_START_POS 		= 0;
	
	private static final int[] increments = {6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180};
	
	private static int currentStep, width, height, sunPosition, p;
	
	private static GridCell prime 	= null;
	private static int speed 		= DEFAULT_SPEED;
	private int gs 					= DEFAULT_DEGREES;
	
	public static GridCell getGrid() {
		return prime;
	}
	
	public static int getWidth() {
		return new Integer(width);
	}
	
	public static int getHeight() {
		return new Integer(height);
	}
	
	public static int getSunPosition() {
		return new Integer(sunPosition);
	}
	
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
		
		width = (2 * MAX_DEGREES / this.gs);	// rows
		height = (MAX_DEGREES / this.gs);		// cols
				
		if (prime != null) prime.setTemp(INITIAL_TEMP);
		else prime = new GridCell(INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs);
		prime.setTop(null);
		
		p = this.gs / 360;
		
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void generate() {
		
		Queue<EarthCell> bfs = new LinkedList<EarthCell>();
		Queue<EarthCell> calcd = new LinkedList<EarthCell>();
		
		currentStep++;
		
		int t = speed * currentStep;
		int rotationalAngle = (t % MAX_SPEED) * 360 / MAX_SPEED;
		sunPosition = (width * (rotationalAngle / 360) + (width / 2) % width);
		
		IGrid grid = new Grid(sunPosition, width, height);
		
		while(true) {
			
			bfs.add(prime);
			prime.visited(true);
			
			while(!bfs.isEmpty()) {
				
				EarthCell point = bfs.remove();
				calcd.add(point);
				
				// TODO This needs testing. Should work though.
				EarthCell c = calcd.peek();
				if (c != null) {
					Iterator<EarthCell> itr = c.getChildren(false);
					if (!itr.hasNext()) {
						c.visited(false);
						c.swapTemp();
						calcd.poll();
					}
				}
				// done TODO
				
				EarthCell child = null;
				Iterator<EarthCell> itr = point.getChildren(false);
				while(itr.hasNext()) {
					child = itr.next();
					child.visited(true);
					grid.setTemperature(child.getX(), child.getY(), child.calculateTemp());
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
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x), this.gs);
		} else {
			next = new GridCell(null, bottom, null, curr, INITIAL_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs);
			curr.setLeft(next);
		}
	}
	
	private void createNextRow(GridCell bottom, GridCell curr, int y) {
		
		if (bottom.getTop() != null) { 
			curr = bottom.getTop();
			curr.setTemp(INITIAL_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0), p);
		} else {
			curr = new GridCell(null, bottom, null, null, INITIAL_TEMP, 0, y, this.getLatitude(y), this.getLongitude(0), this.gs);
			bottom.setTop(curr);
		}
	}
	
	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}
	
	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
