package common;

public interface IEngine extends Runnable {

	public void performAction();
	
	public void configure(int gs, int timeStep);
	
	public void processQueue();
	
	public void stop();
	
	public void pause(Object lock) throws InterruptedException;

}