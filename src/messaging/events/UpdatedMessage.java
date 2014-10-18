package messaging.events;

import common.IEngine;

import messaging.Message;

public class UpdatedMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.generate();
	}
}
