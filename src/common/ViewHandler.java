package common;

import messaging.Message;
import messaging.Publisher;
import messaging.events.UpdatedMessage;

public class ViewHandler implements IHandler {

	private final Message msg;
	
	public ViewHandler(Message msg) {
		this.msg = msg;
	}

	@Override
	public void trigger() {
		msg.process(null);
	}
	
	@Override
	public void start() {
		Publisher.getInstance().send(new UpdatedMessage());
	}
}
