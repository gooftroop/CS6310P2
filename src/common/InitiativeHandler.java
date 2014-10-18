package common;

import messaging.Message;
import messaging.MessageListener;
import messaging.events.StartMessage;
import messaging.events.UpdatedMessage;

public class InitiativeHandler implements IEngine {
	
	private final IHandler handler;

	public InitiativeHandler(IHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onMessage(Message msg) {
		msg.process(this);
	}

	@Override
	public void generate() {
		this.handler.trigger();
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
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(int gs, int timeStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processQueue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause(Object lock) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
}
