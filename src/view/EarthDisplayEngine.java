package view;

import common.AbstractEngine;
import common.Buffer;
import common.IGrid;

public class EarthDisplayEngine extends AbstractEngine {
	
	private final EarthDisplay earthDisplay;
	
	public EarthDisplayEngine() {
		
		earthDisplay = new EarthDisplay();
	}

	@Override
	public synchronized void generate() {
		try {
			IGrid grid = Buffer.getBuffer().get();
			earthDisplay.update(grid);
		} catch (InterruptedException e) {
			// We couldn't get anything. Wait for next round to try again
		}
	}

	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing value");
		
		earthDisplay.display(gs, timeStep);
	}

	@Override
	public void close() {
		earthDisplay.close();
	}
}