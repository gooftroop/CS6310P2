package messaging;

public interface MessageListener {
	
	public void onMessage(Message msg);
	
	public void generate();
	
	public void close();
	
}