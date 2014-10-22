package view;

import messaging.Message;
import messaging.events.DisplayMessage;

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
		} else
			super.onMessage(msg);
	}

	@Override
	public void generate() {
		
		if (grid == null)
			grid = Buffer.getBuffer().get();
	}

	@Override
	public void configure(int gs, int timeStep) {
		
		if (gs <= 0 || gs >= Integer.MAX_VALUE)
			throw new IllegalArgumentException("Invalid grid spacing value");
		
		this.gs = gs;
		this.timeStep = timeStep;
	}

	@Override
	public void start() {
		grid = null;
		earthDisplay.display(this.gs, this.timeStep);
		earthDisplay.update(grid);
	}
}