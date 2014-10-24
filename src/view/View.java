package view;

import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;

import common.Buffer;
import common.ComponentBase;
import common.IBuffer;
import common.IGrid;

public class View extends ComponentBase {

	// 1e-12f;
	private final float STABLE_THRESHOLD = 0f;
	
	// set true to instrument stats (NOTE: some of these will change execution timing)
	private final boolean STATISTIC_MODE = true; 
	
	private Publisher pub = Publisher.getInstance();
	
	//ArrayBlockingQueue<IGrid> q;
	private EarthDisplay display = null;

	// flag used to keep us from requesting more than once before getting response
	private boolean displayRequestPending = false; 

	// set to true when initial conditions are overcome
	private boolean steadyState = false; 
	
	// Profiling fields
	private float statInterval = 1.0f;
		
	// Steady state assumed when when average equator temperature stabilizes
	private float lastEquatorAverage = 0.0f;
	private float presentationInterval;
	
	// used to throttle presentation rate
	private long lastDisplayTime = 0;
	private long lastStatTime = 0;
	private long maxUsedMem = 0;
	private long startWallTime;
	private long presentationCnt = 1;

	public View(int gs, int timeStep, float presentationInterval) {
		
		//this.q = q;
		this.presentationInterval = presentationInterval;
		this.display = new EarthDisplay();
		display.display(gs, timeStep);
		display.update((IGrid) null);
	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void runAutomaticActions() throws InterruptedException {
		// Don't do anything if enough time hasn't passed for us to display
		// another datapoint

		long curTime = System.nanoTime();
		if ((curTime - lastDisplayTime) * 1e-9 < presentationInterval) {
			return;
		}

		// Check to see if there is anything in the data queue to process
		IGrid data = null;
		data = Buffer.getBuffer().get();

		// NOTE: we need to be careful that we don't do something silly like
		// spam
		// a bazillion messages while paused. (don't think this is a problem
		// now but we should ensure it is not later.)
		// TODO: this will be a problem now and need to be addressed.
		if (data != null) {
			
			if (STATISTIC_MODE) {

				// NOTE: we need to do something here to manage the animation
				// update rate...
				if (!steadyState && steadyStateReached(data)) {
					steadyState = true;
					System.out.printf("stable reached: %d\n",
							data.getCurrentTime());
				}

				// Sample memory usage periodically
				if ((curTime - lastStatTime) * 1e-9 > statInterval) {
					float wallTimePerPresentation = (float) (System.nanoTime() - startWallTime)
							/ presentationCnt;
					System.out.printf("walltime/present (msec): %f\n",
							wallTimePerPresentation / 1e6);
					Runtime runtime = Runtime.getRuntime();
					System.gc();
					maxUsedMem = Math.max(maxUsedMem, runtime.totalMemory()
							- runtime.freeMemory());
					System.out.printf("usedMem: %.1f\n", maxUsedMem / 1e6);
					lastStatTime = curTime;

					IBuffer b = Buffer.getBuffer();
					System.out.printf("Buffer fill status: %d/%d\n",
							b.size() + 1, b.size() + b.getRemainingCapacity());

					startWallTime = System.nanoTime();
					presentationCnt = 0;

				}
				presentationCnt++;
			}
			
			present(data);
			lastDisplayTime = curTime;
			displayRequestPending = false;
		} else {
			if (!displayRequestPending) {
				pub.send(new NeedDisplayDataMessage());
				displayRequestPending = true;
			}
		}
	}

	private void present(IGrid data) {

		display.update(data);
		pub.send(new DisplayMessage());
	}

	public void close() {
		// destructor when done with class
		display.close();
	}

	public boolean steadyStateReached(IGrid grid) {
		float equatorAverage = 0.0f;
		int eqIdx = grid.getGridHeight() / 2;
		for (int i = 0; i < grid.getGridWidth(); i++) {
			equatorAverage += grid.getTemperature(i, eqIdx);
		}
		equatorAverage /= grid.getGridWidth();

		boolean stable = false;

		if (Math.abs(equatorAverage - lastEquatorAverage) <= STABLE_THRESHOLD) {
			stable = true;
		}
		lastEquatorAverage = equatorAverage;
		return stable;

	}
}
