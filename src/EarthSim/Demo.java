package EarthSim;

import java.awt.EventQueue;

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
		options.put("-d", "");
		options.put("-l", "");
		options.put("-r", "");
		options.put("-t", "");
		options.put("-b", "");
	}
}