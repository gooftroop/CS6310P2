package messaging.events;

import common.IEngine;

public class StopMessage extends AbstractMessage {

	@Override
	public void processEngine(IEngine l) {
		l.stop();
	}
}