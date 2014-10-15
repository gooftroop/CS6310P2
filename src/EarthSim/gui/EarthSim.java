package EarthSim.gui;

import javax.swing.JFrame;

import common.ComponentBase;

public class EarthSim extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8339374045042987430L;
	private final ComponentBase engine;
	
	public EarthSim(ComponentBase engine) {
		
		if (engine == null)
			throw new IllegalArgumentException("Invalid engine provided");
		
		this.engine = engine;
	}

}