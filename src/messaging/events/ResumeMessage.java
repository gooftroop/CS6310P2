package messaging.events;

import common.IEngine;

public class ResumeMessage extends AbstractMessage {

	@Override
	public void processEngine(IEngine l) {
		l.resume();
	}
}