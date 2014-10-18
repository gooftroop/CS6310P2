package messaging;

import common.IEngine;
import messaging.events.ConsumeMessage;

public class SingleConsumeCommand implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ConsumeMessage());
	}
}