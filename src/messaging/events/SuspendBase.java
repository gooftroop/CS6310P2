package messaging.events;

import messaging.Message;

public abstract class SuspendBase implements Message {

	protected final Object LOCK = new Object();

}
