package messaging;

public interface MessageListener {
	
	public void onMessage(Message msg);
	
	public void generate();
	
	public void close();
	
	public void start();
	
	public void stop();
	
	public void pause();
	
	public void resume();
	
}