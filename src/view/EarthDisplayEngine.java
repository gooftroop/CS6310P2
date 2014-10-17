package view;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.UpdatedMessage;
import common.AbstractEngine;
import common.Buffer;
import common.IGrid;

public class EarthDisplayEngine extends AbstractEngine {
	
	private final EarthDisplay earthDisplay;
	private IGrid grid = null;
	
	private int gs, timeStep;
	
	public EarthDisplayEngine() {
		this(false);
	}
	
	public EarthDisplayEngine(final boolean isThreaded) {
		super(isThreaded);
		earthDisplay = new EarthDisplay();
	}
	
	@Override
	public synchronized void onMessage(Message msg) {

		if (msg instanceof DisplayMessage) {
			if (grid != null) {
				earthDisplay.update(grid);
			}
			
			// This tells the handler that this is ready to be triggered again if it has the initiative
			Publisher.getInstance().send(new UpdatedMessage(this));
		} else
			super.onMessage(msg);
	}

	@Override
	public synchronized void generate() {
		
		try {
			grid = Buffer.getBuffer().get();
		} catch (InterruptedException e) {
			// We couldn't get anything. Wait for next round to try again
			// This won't cause the top level GUI to block, but appear as if
			// the program is hanging, which is the appropriate user feedback
			// we want to convey -- for some reason, the Earth is not producing
			// a grid.
			grid = null;
		}
	}

	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing value");
		
		this.gs = gs;
		this.timeStep = timeStep;
	}

	@Override
	public void close() {
		earthDisplay.close();
	}

	@Override
	public void reset() {
		grid = null;
		earthDisplay.display(this.gs, this.timeStep);
	}
}