package common;

import messaging.MessageListener;

public interface IHandler {
	
	public void trigger(Class<? extends MessageListener> src);
	
	public void start();

}
