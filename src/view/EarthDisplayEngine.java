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
	public void onMessage(Message msg) {

		if (msg instanceof DisplayMessage) {
			if (grid != null) {
				earthDisplay.update(grid);
				grid = null;
			}
			
			// This tells the handler that this is ready to be triggered again if it has the initiative
			Publisher.getInstance().send(new UpdatedMessage(this));
		} else
			super.onMessage(msg);
	}

	@Override
	public synchronized void generate() {
		
		try {
			//System.out.println("Going to retrieve from he buffer");
			if (grid == null)
				grid = Buffer.getBuffer().get();
			//System.out.println("Got to grid " + grid + " from he buffer");
		} catch (InterruptedException e) {
			// We couldn't get anything. Wait for next round to try again
			// This won't cause the top level GUI to block, but appear as if
			// the program is hanging, which is the appropriate user feedback
			// we want to convey -- for some reason, the Earth is not producing
			// a grid.
			System.out.println(e);
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
	public void start() {
		grid = null;
		earthDisplay.display(this.gs, this.timeStep);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}