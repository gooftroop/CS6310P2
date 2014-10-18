package common;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.StartMessage;
import messaging.events.UpdatedMessage;

public class InitiativeHandler implements MessageListener {
	
	private final IHandler handler;

	public InitiativeHandler(IHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onMessage(Message msg) {
		
		// do i need to queue?
		synchronized (this.handler) {
			if (msg instanceof UpdatedMessage)
				this.handler.trigger(((UpdatedMessage) msg).getSource().getClass());
			if (msg instanceof StartMessage)
				this.handler.start();
		}
	}

	@Override
	public void generate() {
		// nothing to do
		return;
		
	}

	@Override
	public void close() {
		// nothing to do
		return;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
