package messaging.events;

import common.IEngine;

public class ResumeMessage extends SuspendBase {

	@Override
	public void process(IEngine l) {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}
}