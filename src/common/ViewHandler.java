package common;

import messaging.MessageListener;
import messaging.Publisher;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class ViewHandler implements IHandler {

	private final Class<? extends MessageListener> type;
	private final boolean isThreaded;
	
	public ViewHandler(Class<? extends MessageListener> type, boolean isThreaded) {
		this.type = type;
		this.isThreaded = isThreaded;
	}

	@Override
	public void trigger(Class<? extends MessageListener> src) {
		if (this.type.equals(src)) {
			Publisher.getInstance().send(new ProduceMessage());
			// Now tell the View to get the next grid
			if (isThreaded)
				Publisher.getInstance().send(new ConsumeMessage());
		}
	}
	
	@Override
	public void start() {
		return;
	}
}
