package concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import common.IEngine;
import messaging.Message;
import messaging.MessageListener;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

public class ThreadManager extends ThreadPoolExecutor implements MessageListener {
	
	private static final Object LSUSPEND = new Object();
	
	private final static int POOL_SIZE = 2;
	private final static long TIMEOUT = 0L;
	private final Queue<IEngine> queued;
	
	private static ThreadManager instance = null;
	
	public static ThreadManager getManager() {
		if (instance == null) instance = new ThreadManager();
		
		return instance;
	}

	private ThreadManager() {
		
		super(POOL_SIZE, POOL_SIZE, TIMEOUT, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		queued = new LinkedList<IEngine>();
	}

	public synchronized void add(IEngine r) {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		if (queued.contains(r)) return;
		
		this.queued.add(r);
	}
	
	public void stop() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		// make sure nothing is paused
		this.resume();
		
		for (IEngine r : queued) {
			// Reach down and interrupt the threads
			r.stop();
		}
	}
	
	public void close() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		this.stop();
		this.shutdown();
		
		while(this.isTerminating()) {
			// block
		}
		
		if (!this.isShutdown()) this.shutdownNow();
		
	}

	public void start() {
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		for (Runnable r : this.queued) this.execute(r);
	}
	
	public void pause() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		for (IEngine r : queued) {
			try {
				r.pause(LSUSPEND);
			} catch (InterruptedException e) {
				// log and then stop
				System.err.println("An error occurred while pausing thread: " + e);
				this.stop();
			}
		}
	}
	
	public void resume() {
		
		if (this.isShutdown() || this.isTerminating() || this.isTerminated()) return;
		
		synchronized (LSUSPEND) {
			LSUSPEND.notifyAll();
		}
	}

	@Override
	public void onMessage(Message msg) {
		// Don't queue - execute immediately
		if (msg instanceof StopMessage) {
			this.stop();
		} else if (msg instanceof PauseMessage) {
			this.pause();
		} else if (msg instanceof ResumeMessage) {
			this.resume();
		} else if (msg instanceof StartMessage) {
			this.start();
		} 
	}

	@Override
	public void generate() {
		// nothing to do
		return;
	}
}