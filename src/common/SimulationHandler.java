package common;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class SimulationHandler implements IHandler {
	
	private final Class<? extends MessageListener> type;
	private final boolean isThreaded;
	
	public SimulationHandler(Class<? extends MessageListener> type, boolean isThreaded) {
		this.type = type;
		this.isThreaded = isThreaded;
	}

	@Override
	public void trigger(Class<? extends MessageListener> src) {
		if (this.type.equals(src)) {
			Publisher.getInstance().send(new ConsumeMessage());
			// Now tell the Simulation to do another calculation if it's threaded. TODO this is a hack for testing. clean up.
			if (isThreaded)
				Publisher.getInstance().send(new ProduceMessage());
		}
	}
	
	@Override
	public void start() {
		Publisher.getInstance().send(new ProduceMessage());
	}
}