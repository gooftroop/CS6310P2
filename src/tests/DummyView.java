package tests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import view.EarthDisplay;
import common.ComponentBase;
import common.IGrid;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;

public class DummyView extends ComponentBase {
	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<IGrid> q;
	EarthDisplay earth = null;
	long lastDisplayTime = 0; // used to throttle presentation rate
	float presentationInterval;
	boolean displayRequestPending = false; // flag used to keep us from requesting more than once before getting response
	
	public DummyView(ArrayBlockingQueue<IGrid> q, int gs, int timeStep, float presentationInterval) {
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
			//NOTE: we need to do something here to manage the animation update rate...
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
		System.out.printf("presented data\n");
		earth.update(data);
		pub.send(new DisplayMessage());
	}

	public void close() {
		// destructor when done with class
		earth.close();
	}
}
