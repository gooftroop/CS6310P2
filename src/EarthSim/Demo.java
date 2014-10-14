package EarthSim;

import java.awt.EventQueue;
import java.util.Hashtable;

import common.ComponentBase;
import EarthSim.gui.EarthSim;

public class Demo {
	
	protected static final Hashtable<String, Object> options = new Hashtable<String, Object>();

	public static void main(String[] args) {
		
		Demo.configureOpts();
		
		boolean s = (Boolean) options.get("-s"), p = (Boolean) options.get("-p"), 
				r = (Boolean) options.get("-r"), t = (Boolean) options.get("-t");
		
		int b = (Integer) options.get("-b");
		final ComponentBase engine = new EarthSimEngine(s, p, r, t, b);
		
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new EarthSim(engine);
            }
		});
	}
	
	protected static void configureOpts() {
		options.put("-s", false);
		options.put("-p", false);
		options.put("-r", false);
		options.put("-t", false);
		options.put("-b", 1);
	}
	
	protected static void parseArgs(String... args) {
		
		String curr = "";
		
		if (args.length == 0) throw new IllegalArgumentException("No arguments provided to Demo");
		
		for (int i = 0; i < args.length; i++) {
			
			curr = args[i];
			
	        switch (curr.charAt(0)) {
	        			
	        	case '-':
		            if (!options.containsKey(curr)) 
		            	throw new IllegalArgumentException("Invalid argument '" + curr + "'");
		            
		            if ("-b".equals(curr)) {
		            	
		        		if (++i >= args.length || args[i] == "")
		        			throw new IllegalArgumentException("Expecting an value for argument " + curr);
		        		
		        		try {
		        			int val = Integer.parseInt(args[i]);
		        			options.put(curr, val);
		        		} catch( NumberFormatException e) {
		        			throw new IllegalArgumentException("Invalid value for argument " + curr);
		        		}
		        		
		            } else options.put(curr, true);

		            break;
		        default:
		        	throw new IllegalArgumentException("Invalid parameter " + curr);
	        }
		}	
	}
}