package tests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import common.ComponentBase;
import messaging.Message;
import messaging.Publisher;
import messaging.events.DisplayMessage;
import messaging.events.NeedDisplayDataMessage;

public class DummyView extends ComponentBase {
	private Publisher pub = Publisher.getInstance();
	ArrayBlockingQueue<Integer> q;
	
	public DummyView(ArrayBlockingQueue<Integer> q) {
		this.q = q;
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runAutomaticActions() throws InterruptedException {
		// Check to see if there is anything in the data queue to process
		Integer data = null;
		data = q.poll(10, TimeUnit.MILLISECONDS);
		
		//NOTE: we need to be careful that we don't do something silly like spam
		//      a bazillion messages while paused. (don't think this is a problem
		//      now but we should ensure it is not later.)
		if(data != null) {
			//NOTE: we need to do something here to manage the animation update rate...
			present(data);
		}
		else {
			pub.send(new NeedDisplayDataMessage());
		}
	}
	
	private void present(Integer data) {
		System.out.printf("presented data\n");
		pub.send(new DisplayMessage());
	}

	public void close() {
		// destructor when done with class
	}
}
