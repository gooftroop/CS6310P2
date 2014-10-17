package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import messaging.Publisher;
import messaging.events.UpdatedMessage;
import simulation.util.EarthCell;
import simulation.util.GridCell;
import common.AbstractEngine;
import common.Buffer;
import common.Grid;
import common.IGrid;

public final class Earth extends AbstractEngine {

	public static final double CIRCUMFERENCE 	= 4.003014 * Math.pow(10, 7);
	public static final double SURFACE_AREA 	= 5.10072 * Math.pow(10, 14);

	public static final int MAX_TEMP 			= 288;
	public static final int MIN_TEMP			= 0;

	private static final int DEFAULT_DEGREES 	= 15;
	private static final int DEFAULT_SPEED 		= 1; // minutes
	private static final int MAX_DEGREES 		= 180;
	private static final int MAX_SPEED 			= 1440;
	private static final int SUN_START_POS 		= 0;

	private static final int[] increments = {6, 9, 10, 12, 15, 18, 20, 30, 36, 45, 60, 90, 180};

	private int currentStep, width, height, sunPosition, p;
	private float avgArea;
	
	private GridCell prime 	= null;
	private int speed 		= DEFAULT_SPEED;
	private int gs 			= DEFAULT_DEGREES;

	public Earth(boolean simThreaded) {
		super(simThreaded);
	}

	public GridCell getGrid() {
		return prime;
	}

//	public static int getWidth() {
//		return new Integer(width);
//	}
//
//	public static int getHeight() {
//		return new Integer(height);
//	}
//
//	public static int getSunPosition() {
//		return new Integer(sunPosition);
//	}

	@Override
	public void configure(int gs, int timeStep) {

		if (gs <= 0 || gs > MAX_DEGREES)
			throw new IllegalArgumentException("Invalid grid spacing");

		if (speed <= 0 || speed > MAX_SPEED)
			throw new IllegalArgumentException("Invalid speed setting");

		speed = timeStep;

		// The following could be done better - if we have time, we should do so
		if (MAX_DEGREES % gs != 0) {
			for (int i = 1; i < increments.length; i++)
				if (increments[i] > gs) this.gs = increments[i - 1];
		} else this.gs = gs;
		
		this.initializePlate();
	}

	public void initializePlate() {

		int x = 0, y = 0;

		// do a reset
		sunPosition = SUN_START_POS;
		currentStep = 0;

		width = (2 * MAX_DEGREES / this.gs);	// rows
		height = (MAX_DEGREES / this.gs);		// cols
		
		avgArea = (float) (Earth.SURFACE_AREA / (width * height));

		if (prime != null) prime.setTemp(MAX_TEMP);
		else prime = new GridCell(MAX_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs, avgArea);
		prime.setTop(null);
		
		p = this.gs / 360;

		// Set initial average temperature
		GridCell.setAvgtemp(MAX_TEMP);
		
		// South Pole
		GridCell next = null, curr = prime;
		for (x = 1; x < width; x++) {

			this.createRowCell(curr, next, null, x, y);
			curr = curr.getLeft();
		}

		// Stitch the grid row together
		prime.setRight(curr);
		curr.setLeft(prime);

		// Create each grid row, with the exception of the south pole
		GridCell bottom = prime, left = null;
		for (y = 1; y < height - 1; y++) {

			this.createNextRow(bottom, curr, y);       // curr should be changed, but actually have not.
			curr   = bottom.getTop();
			this.createRow(curr, next, bottom.getLeft(), left, y);  // left should be changed, but actually have not.
			bottom = bottom.getTop();

		}

		this.createNextRow(bottom, curr, y);
		curr   = bottom.getTop();

		// North Pole
		this.createRow(curr, next, bottom.getLeft(), left, y);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void generate() {

		Queue<EarthCell> bfs = new LinkedList<EarthCell>();
		Queue<EarthCell> calcd = new LinkedList<EarthCell>();

		currentStep++;

		int t = speed * currentStep;
		int rotationalAngle = (t % MAX_SPEED) * 360 / MAX_SPEED;
		sunPosition = rotationalAngle == 0 ? rotationalAngle : (width * (rotationalAngle / 360) + (width / 2) % width);

		IGrid grid = new Grid(sunPosition, t, width, height);

		int time = 0;
		float totaltemp;
		float avgtemp;
		while(time < 10) {

			bfs.add(prime);
			prime.visited(true);
			totaltemp = 0;
			
			while(!bfs.isEmpty()) {

				EarthCell point = bfs.remove();
				calcd.add(point);

				EarthCell child = null;
				float childtemp = 0;
				Iterator<EarthCell> itr = point.getChildren(false);
				while(itr.hasNext()) {
					child = itr.next();
					child.visited(true);
					childtemp = child.calculateTemp(sunPosition);
					totaltemp = totaltemp + childtemp;
					grid.setTemperature(child.getX(), child.getY(), childtemp);
					bfs.add(child);
				}
			}
			
			avgtemp = totaltemp / width * height;
			GridCell.setAvgtemp(avgtemp);
			
			System.out.println(calcd.size());
			EarthCell c = calcd.poll();
			while (c != null) {
				c.visited(false);
				c.swapTemp();
				c = calcd.poll();
			}
			
		}
		
		try {
			Buffer.getBuffer().add(grid);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		// This tells the handler that this is ready to be triggered again if it has the initiative
		Publisher.getInstance().send(new UpdatedMessage(this));
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
			l.setTemp(MAX_TEMP);
			l.setGridProps(x, y, this.getLatitude(y), this.getLongitude(x), this.gs, avgArea);
		} else {
			next = new GridCell(null, bottom, null, curr, MAX_TEMP, x, y, this.getLatitude(y), this.getLongitude(x), this.gs, avgArea);
			curr.setLeft(next);
			if (bottom != null) {
				bottom.setTop(next);
			}
		}
	}

	private void createNextRow(GridCell bottom, GridCell curr, int y) {

		if (bottom.getTop() != null) {
			curr = bottom.getTop();
			curr.setTemp(MAX_TEMP);
			curr.setGridProps(0, y, this.getLatitude(y), this.getLongitude(0), p, avgArea);
		} else {
			curr = new GridCell(null, bottom, null, null, MAX_TEMP, 0, y, this.getLatitude(y), this.getLongitude(0), this.gs, avgArea);
			bottom.setTop(curr);
		}
	}

	private int getLatitude(int y) {
		return (y - (height / 2)) * this.gs;
	}

	private int getLongitude(int x) {
		return x < (width / 2) ? -(x + 1) * this.gs : (360) - (x + 1) * this.gs;
	}
	
	
//	// The following code is only for testing.
//	public static void main(String [] args){
//		Earth earth = new Earth();
//		earth.configure(45, 10);
//		earth.initializePlate();
//		System.out.println("Just after initializaiton:");
//		printGrid();
//		//earth.run();
//		earth.generate();
//	}
//	
//	private static void printGrid(){
//		GridCell curr = prime;
//		int height = Earth.getHeight();
//		int width  = Earth.getWidth();
//		//System.out.println(height);
//		//System.out.println(width);
//		for (int x = 0; x < height; x++) {
//			GridCell rowgrid = curr.getLeft();
//			for (int y = 0; y < width; y++) {
//				System.out.printf("%.2f,",rowgrid.getTemp());
//				rowgrid = rowgrid.getLeft();
//			}
//			System.out.println();
//			curr = curr.getTop();
//		}
//	}
}
