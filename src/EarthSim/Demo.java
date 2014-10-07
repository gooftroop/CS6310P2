package EarthSim;

import java.awt.EventQueue;

import EarthSim.gui.EarthSim;
import EarthSim.util.AbstractDemo;

public class Demo extends AbstractDemo {

	public static void main(String[] args) {
		
		Demo.configureOpts();
		
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new EarthSim();
            }
		});
	}
	
	protected static void configureOpts() {
		options.put("-s", "");
		options.put("-p", "");
		options.put("-r", "");
		options.put("-t", "");
		options.put("-b", "");
	}
}