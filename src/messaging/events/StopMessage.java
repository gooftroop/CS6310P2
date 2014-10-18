package messaging.events;

import common.IEngine;

import messaging.Message;

public class StopMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.stop();
	}
}