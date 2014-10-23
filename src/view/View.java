package view;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import common.ComponentBase;
import common.IGrid;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;


public class View extends ComponentBase {
	private final float STABLE_THRESHOLD = 0f;//1e-12f;
	private final boolean STATISTIC_MODE = true; // set true to instrument stats (NOTE: some of these will change execution timing)
	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<IGrid> q;
	EarthDisplay earth = null;
	long lastDisplayTime = 0; // used to throttle presentation rate
	float presentationInterval;
	boolean displayRequestPending = false; // flag used to keep us from requesting more than once before getting response

	// Profiling fields
	float statInterval = 1.0f;
	long lastStatTime = 0;
	boolean steadyState = false; // set to true when initial conditions are overcome
	float lastEquatorAverage = 0.0f; // Steady state assumed when when average equator temperature stabilizes
	long maxUsedMem = 0;
	long startWallTime;
	long startCpuTime;
	long presentationCnt = 1;
	
	public View(ArrayBlockingQueue<IGrid> q, int gs, int timeStep, float presentationInterval) {
		this.q = q;
		this.presentationInterval = presentationInterval;
		this.earth = new EarthDisplay();
		earth.display(gs, timeStep);
		earth.update((IGrid)null);
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runAutomaticActions() throws InterruptedException {
		// Don't do anything if enough time hasn't passed for us to display
		// another datapoint
//		System.out.printf("runing autoactions\n");
		long curTime = System.nanoTime();
		if ((curTime - lastDisplayTime)*1e-9 < presentationInterval) {
//			System.out.printf("autoactions bail\n");
			return;
		}
		// Check to see if there is anything in the data queue to process
		IGrid data = null;
		data = q.poll(10, TimeUnit.MILLISECONDS);
		
		//NOTE: we need to be careful that we don't do something silly like spam
		//      a bazillion messages while paused. (don't think this is a problem
		//      now but we should ensure it is not later.)
		//TODO: this will be a problem now and need to be addressed.
		if(data != null) {
			if(STATISTIC_MODE) {
				//NOTE: we need to do something here to manage the animation update rate...
				if(!steadyState && steadyStateReached(data)) {
					steadyState = true;
					System.out.printf("stable reached: %d\n", data.getCurrentTime());
				}
	
				// Sample memory usage periodically
				if((curTime - lastStatTime)*1e-9 > statInterval) {
		        	float wallTimePerPresentation = (float)(System.nanoTime() - startWallTime) / presentationCnt;
		        	System.out.printf("walltime/present (msec): %f\n", wallTimePerPresentation/1e6);
					Runtime runtime = Runtime.getRuntime();
					System.gc();
		            maxUsedMem = Math.max(maxUsedMem, runtime.totalMemory() - runtime.freeMemory());
		            System.out.printf("usedMem: %.1f\n", maxUsedMem/1e6);
		            lastStatTime = curTime;
		            
		            System.out.printf("Buffer fill status: %d/%d\n", q.size()+1,q.size()+q.remainingCapacity());

		            startWallTime = System.nanoTime();
		            presentationCnt = 0;
//		        	float cpuTimePerPresentation;

				}
	            presentationCnt++;
			}
			present(data);
			lastDisplayTime = curTime;
			displayRequestPending = false;
		}
		else {
			if (!displayRequestPending) {
				pub.send(new NeedDisplayDataMessage());
				displayRequestPending = true;
			}
		}
//		System.out.printf("runing autoactions done!\n");
		
	}
	
	private void present(IGrid data) {
//		System.out.printf("presented data\n");
		earth.update(data);
		pub.send(new DisplayMessage());
	}

	public void close() {
		// destructor when done with class
		earth.close();
	}
	
	public boolean steadyStateReached(IGrid grid) {
		float equatorAverage = 0.0f;
		int eqIdx = grid.getGridHeight()/2;
		for(int i = 0; i < grid.getGridWidth(); i++) {
			equatorAverage += grid.getTemperature(i, eqIdx);
		}
		equatorAverage /= grid.getGridWidth();
		
		boolean stable = false;
//		System.out.printf("diff: %f\n", Math.abs(equatorAverage-lastEquatorAverage));
		if(Math.abs(equatorAverage-lastEquatorAverage) <= STABLE_THRESHOLD) {
			stable = true;
		}
		lastEquatorAverage = equatorAverage;
		return stable;
		
	}
}
