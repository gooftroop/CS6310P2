package messaging.events;

import common.IEngine;

public class PauseMessage extends AbstractMessage {

	@Override
	public void processEngine(IEngine l) {
		l.pause();
	}
}
