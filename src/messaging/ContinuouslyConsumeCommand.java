package messaging;

import common.IEngine;
import messaging.events.ConsumeMessage;
import messaging.events.ProduceMessage;

public class ContinuouslyConsumeCommand implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ProduceMessage());
		Publisher.getInstance().send(new ConsumeMessage());
	}
}