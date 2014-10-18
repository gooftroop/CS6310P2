package messaging.events;

import common.IEngine;

import messaging.Message;

public class StartMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.start();
	}
}