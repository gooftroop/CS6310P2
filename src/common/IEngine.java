package common;

import messaging.Message;

public interface IEngine extends Runnable {
	
	public void onMessage(Message msg);
	
	public void generate();
	
	public void performAction();
	
	public void configure(int gs, int timeStep);
	
	public void processQueue();
	
	public void pause(Object lock) throws InterruptedException;
	
	public void close();
	
	public void start();
	
	public void stop();
	
	public void resume();

}