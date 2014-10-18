package messaging;

import common.IEngine;
import messaging.events.ProduceMessage;

public class SingleProduceCommand implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ProduceMessage());
	}
}