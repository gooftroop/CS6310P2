package messaging.events;

import messaging.Message;
import common.IEngine;

public class ResumeMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.resume();
	}
}