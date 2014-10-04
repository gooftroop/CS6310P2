package simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public final class Earth {
	
	private GridCell root = null;
	
	private double leftTemp = 0, rightTemp = 0, topTemp = 0, bottomTemp = 0;
	
	private int height = 0, width = 0;
	
	public Earth() {
		/* Empty */
	}
	
	public Earth(int height, int width, double topTemp, double bottomTemp, double leftTemp, double rightTemp) {
		
		this.setup(height, width, topTemp, bottomTemp, leftTemp, rightTemp);
	}
	
	public void setup(int width, int height, double topTemp, double bottomTemp, double leftTemp, double rightTemp) {
		
		if (height < 0 || height >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid height dimension");
		
		if (width < 0 || width >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid width dimension");
		
		if (leftTemp < MIN_TEMP || leftTemp > MAX_TEMP)
			throw new IllegalArgumentException("Invalid left temperature value");
		
		if (rightTemp < MIN_TEMP || rightTemp > MAX_TEMP)
			throw new IllegalArgumentException("Invalid left temperature value");
		
		if (topTemp < MIN_TEMP || topTemp > MAX_TEMP)
			throw new IllegalArgumentException("Invalid left temperature value");
		
		if (bottomTemp < MIN_TEMP || bottomTemp > MAX_TEMP)
			throw new IllegalArgumentException("Invalid left temperature value");
		
		this.height = height + 2;
		this.width = width + 2;
		
		this.leftTemp 	= leftTemp;
		this.rightTemp 	= rightTemp;
		this.topTemp 	= topTemp;
		this.bottomTemp = bottomTemp;
		
		this.initializePlate();
	}

	public void initializePlate() {
		
		
		
//		LatticePoint left = null, curr = null, right = null, top = null;
//		
//		double temp = 0;
//		boolean edge = true;
//		
//		for (int h = 0; h < this.height; h++) {
//			for (int w = 0; w < this.width; w++) {
//				
//				edge = (w == 0 || h == 0 || w == this.width - 1 || h == this.height - 1) ? true : false;
//				
//				if (h == 0) temp = this.topTemp;
//				else if (w == 0) temp = this.leftTemp;
//				else if (h == this.height - 1) temp = this.bottomTemp;
//				else if (w == this.width - 1) temp = this.rightTemp;
//				else temp = 0;
//				
//				curr = new LatticePoint(top, null, left, null, temp, edge, w - 1, h - 1);
//				if (top != null) top.setBottom(curr);
//				if (left != null) left.setRight(curr);
//				
//				left = curr;
//				if (top != null) top = top.getRight();
//			}
//			
//			top = curr;
//			if (++h >= this.height) break;
//			
//			for (int w = this.width - 1; w >= 0; w--) {
//				
//				edge = (w == 0 || h == 0 || w == this.width - 1 || h == this.height - 1) ? true : false;
//				
//				if (h == 0) temp = this.topTemp;
//				else if (w == 0) temp = this.leftTemp;
//				else if (h == this.height - 1) temp = this.bottomTemp;
//				else if (w == this.width - 1) temp = this.rightTemp;
//				else temp = 0;
//				
//				curr = new LatticePoint(top, null, null, right, temp, edge, w - 1, h - 1);
//				if (top != null) top.setBottom(curr);
//				if (right != null) right.setLeft(curr);
//				
//				if (curr.x == 0 && curr.y == 0) this.root = curr;
//				
//				right = curr;
//				if (top != null) top = top.getLeft();
//			}
//			
//			top = curr;
//		}
	}
	
	public void run() {
		
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		
		float deviation = 0f;
		
		do {
			
			maxDeviation = deviation = 0f;
			
			bfs.add(this.root);
			this.root.visited(true);
			
			if ((deviation = this.root.calculateTemp()) > maxDeviation) maxDeviation = deviation;
			
			while(!bfs.isEmpty()) {
				
				GridCell point = bfs.remove();
				GridCell child = null;
				Iterator<GridCell> itr = point.getChildren(false);
				while(itr.hasNext()) {
					child = itr.next();
					child.visited(true);
					
					if ((deviation = child.calculateTemp()) > maxDeviation) maxDeviation = deviation;
					bfs.add(child);
				}
				
				this.update(point.getTemp(), point.x, point.y);
			}

			clearNodes();
		
		} while (maxDeviation >= MAX_DEVIATION && currIterations++ <= MAX_ITERATIONS);
		
		rh.stop();
		rh.setNumIterations(currIterations);
		rh.report();
	}
	
	private void clearNodes() {
		
		Queue<GridCell> bfs = new LinkedList<GridCell>();
		
		bfs.add(this.root);
		this.root.visited(false);
		this.root.swapTemp();
		
		while(!bfs.isEmpty()) {
			GridCell point = bfs.remove();
			GridCell child = null;
			
			Iterator<GridCell> itr = point.getChildren(true);
			while(itr.hasNext()) {
				child = itr.next();
				child.visited(false);
				child.swapTemp();
				bfs.add(child);
			}
		}
	}
}
