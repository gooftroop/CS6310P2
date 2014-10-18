package messaging.events;

import common.IEngine;

import messaging.Message;
import messaging.Publisher;

public class ContinuouslyConsumeMessage implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ProduceMessage());
		Publisher.getInstance().send(new ConsumeMessage());
	}
}