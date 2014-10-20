package messaging.events;

import messaging.Message;
import common.IEngine;

public class PauseMessage implements Message {

	@Override
	public void process(IEngine l) {
		l.pause();
	}
}
