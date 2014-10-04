package simulation;

import java.util.Iterator;

public interface Cell<T> {
	
	public void setTop(T top);
	
	public T getTop();
	
	public void setBottom(T bottom);
	
	public T getBottom();
	
	public void setRight(T right);
	
	public T getRight();
	
	public void setLeft(T left);
	
	public T getLeft();

	public double getTemp();
	
	public void setTemp(double temp);
	
	public float calculateTemp();
	
	public void swapTemp();
	
	public void visited(boolean visited);
	
	public Iterator<T> getChildren(boolean unvisited);
	
}
