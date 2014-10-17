package common;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class SimulationHandler implements IHandler {
	
	private final Class<? extends MessageListener> type;
	
	public SimulationHandler(Class<? extends MessageListener> type) {
		this.type = type;
	}

	@Override
	public void trigger(Class<? extends MessageListener> src) {
		if (this.type.equals(src))
			Publisher.getInstance().send(new ConsumeMessage());
	}
	
	@Override
	public void start() {
		Publisher.getInstance().send(new ProduceMessage());
	}
}