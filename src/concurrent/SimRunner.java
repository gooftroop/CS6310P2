package concurrent;

public class SimRunner implements Runnable {
	
	// TODO receive stop/pause events
	
	private final RunnableSim member;
	
	private boolean isStopped, isRunning, isPaused;
	
	public SimRunner(RunnableSim member) {
		
		if (member == null) throw new IllegalArgumentException("Invalid RunnableSim");
		
		this.member = member;
		this.isStopped = this.isRunning = this.isPaused = false;
		
	}
	
	public RunnableSim getRunnableSim() {
		return this.member;
	}
	
	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	public synchronized void stop() {
		
		if (!this.isRunning) return;
		this.isStopped = true;
	}
	
	public synchronized boolean isStopped() {
		return this.isStopped;
	}
	
	public synchronized void pause() {
		
		if (!this.isRunning || this.isStopped) return;
		this.isPaused = true;
		try {
			this.member.wait();
		} catch (InterruptedException e) {
			this.isPaused = false;
		}
	}
	
	public synchronized void resume() {
		
		if (!this.isRunning || this.isStopped) return;
		this.isPaused = false;
		this.member.notify();
	}
	
	public synchronized boolean isPaused() {
		return this.isPaused;
	}

	@Override
	public synchronized void run() {
		
		this.isRunning = true;
		while(!this.isStopped) {
			// do nothing
		}
		
		this.isRunning = false;
	}
}