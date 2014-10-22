package messaging.events;

import messaging.Message;
import messaging.MessageListener;
import common.IEngine;

public abstract class AbstractMessage implements Message {
	
	@Override
	public void process(MessageListener l) {
		return;
	}

	@Override
	public void processEngine(IEngine l) {
		return;
	}
}
