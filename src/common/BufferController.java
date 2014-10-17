package common;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class BufferController implements IHandler {
	
	// we don't want to starve the buffer in either direction,
	// nor do we want to make either either agent wait for long.
	
	/*
	 * 1. buffer has something in it, so we can tell the display to consume
	 * 2. there's nothing in the buffer, so we can tell the simulation to produce
	 * 
	 * Invoke is a callback, so it gets called whenever something is added to the 
	 * buffer, or when it's removed
	 */
	
	private IBuffer b = null;
	
	public BufferController() {
		b = Buffer.getBuffer();
	}

	@Override
	public void trigger(Class<? extends MessageListener> src) {
		
		// We don't care about src here
		
		if (b == null)
			throw new IllegalStateException("Improperly Configured BufferController");
		
		int r = b.getRemainingCapacity();
		
		// the buffer is empty - this means that invoke was called after a grid was removed
		// and we need to fill it up to prevent starvation. Ideally the only way this could
		// happen is if the buffer size was 1
		if (r == 0) {
			Publisher.getInstance().send(new ProduceMessage());
			return;
		}
		
		if (r < b.getCapacity())
			Publisher.getInstance().send(new ProduceMessage());
		
		if (r > 0)
			Publisher.getInstance().send(new ConsumeMessage());
		
	}
	
	@Override
	public void start() {
		Publisher.getInstance().send(new ProduceMessage());
	}
}
