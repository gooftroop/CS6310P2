package messaging.events;

import common.IEngine;

import messaging.Message;
import messaging.Publisher;

public class SingleProduceMessage implements Message {

	@Override
	public void process(IEngine l) {
		Publisher.getInstance().send(new ProduceMessage());
	}
}