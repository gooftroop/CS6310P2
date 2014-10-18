package messaging.events;

import common.IEngine;
import messaging.Message;

public class PauseMessage implements Message {

	@Override
	public void process(IEngine l) {
		try {
			l.pause(new Object());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
