package EarthSim.view;

import util.GridDisplayPane.ColormapVisualizer;

import java.util.*;

public class TestStandAlone {

	public static void main(String[] args) {
		Boolean SHOW_RUN_STATS = false;
		Boolean ANIMATE = false;
		String COLORMAP = "bone"; // jet/hot/cool/autumn/bone (check
									// ColorMap.java for complete list)

		// Standard required setup
		EarthDisplay view = new EarthDisplay(SHOW_RUN_STATS);
		view.setVisualizer(new ColormapVisualizer(COLORMAP)); // we can fix this
																// ourselves or
																// allow
																// modification
																// via control
		view.setVisible(true);

		// Call updateGrid as changes are made. In test mode I'll just call in
		// a loop with sample data, updating the offset which will result in a
		// circular looping of coloration
		
		// A single invocation would just be something like the line below 
		// except you'd presumably be pulling the EarthData equivalent off of
		// some queue connected to the simulator
		if( !ANIMATE ) {
			view.updateGrid(new TestEarthData(20, 40, false, 0, 0));
		}
		else {
			long updatePeriod_ms = 250;
			new Timer().schedule(new UpdateTask(view), 0, updatePeriod_ms);
		}
	}
}

class UpdateTask extends TimerTask {
	EarthDisplay view;
	int rowOffset;
	int colOffset;

	UpdateTask(EarthDisplay view) {
		this.view = view;
		rowOffset = colOffset = 0;
	}

	public void run() {
		view.updateGrid(new TestEarthData(20, 40, false, rowOffset, colOffset));
		rowOffset++;
		colOffset++;
	}
}
