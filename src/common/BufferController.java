package common;

public class BufferController implements ICallback {
	
	// we don't want to starve the buffer in either direction,
	// nor do we want to make either either agent wait for long.
	
	/*
	 * 1. buffer has something in it, so we can tell the display to consume
	 * 2. there's nothing in the buffer, so we can tell the simulation to produce
	 * 
	 * Invoke is a callback, so it gets called whenever something is added to the 
	 * buffer, or when it's removed
	 */

	@Override
	public void invoke() {
		
		IBuffer b = Buffer.getBuffer();
		int r = b.getRemainingCapacity();
		
		// the buffer is empty - this means that invoke was called after a grid was removed
		// and we need to fill it up to prevent starvation. Ideally the only way this could
		// happen is if the buffer size was 1
		if (r == 0)
			// TODO send produce message
			return;
		
		// if (r < b.getCapacity())
			// TODO send produce message
		
		// if (r > 0)
			// TODO send consume message
		
	}
}
