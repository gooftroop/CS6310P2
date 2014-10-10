package common;

public interface IBuffer {
	
	public void add(IGrid grid);

	public IGrid get() throws InterruptedException;
	
	public int size();
	
	public int getCapacity();
	
	public int getRemainingCapacity();

}
