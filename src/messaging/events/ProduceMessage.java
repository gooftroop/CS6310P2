package messaging.events;

import common.IEngine;

import messaging.Message;

public class ProduceMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.generate();
	}
}