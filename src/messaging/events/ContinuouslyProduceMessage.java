package messaging.events;

import common.IEngine;

import messaging.Message;
import messaging.Publisher;

public class ContinuouslyProduceMessage implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ConsumeMessage());
		Publisher.getInstance().send(new ProduceMessage());
	}
}