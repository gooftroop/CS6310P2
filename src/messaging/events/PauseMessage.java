package messaging.events;

import common.IEngine;

public class PauseMessage extends SuspendBase {

	@Override
	public void process(IEngine l) {
		try {
			l.pause(LOCK);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
