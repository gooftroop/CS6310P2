package messaging.events;

import messaging.Message;

import common.IEngine;

public class CloseMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.close();
	}
}
