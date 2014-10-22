package messaging.events;

import common.IEngine;

public class StartMessage extends AbstractMessage {

	@Override
	public void processEngine(IEngine l) {
		l.start();
	}
}