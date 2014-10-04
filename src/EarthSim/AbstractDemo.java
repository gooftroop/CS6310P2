package EarthSim;

import java.util.Hashtable;

public abstract class AbstractDemo implements IView {
	
	protected static final Hashtable<String, String> options = new Hashtable<String, String>();
	
	protected static void configureOpts() {
		options.put("-d", "");
		options.put("-l", "");
		options.put("-r", "");
		options.put("-t", "");
		options.put("-b", "");
	}
	
	protected static void parseArgs(String... args) {
		
		String curr = "", currOpt = "";
		
		if (args.length == 0) throw new IllegalArgumentException("No arguments provided to Demo");
		
		for (int i = 0; i < args.length; i++) {
			
			curr = args[i];
			
	        switch (curr.charAt(0)) {
	        
	        	case '-':
		            if (!"".equals(currOpt)) throw new IllegalArgumentException("No value for argument '" + currOpt + "'");
		            if (args.length == (i + 1))  throw new IllegalArgumentException("Expected value after argument '" + curr + "'");
		            if (!options.containsKey(curr)) throw new IllegalArgumentException("Invalid argument '" + curr + "'");
		            currOpt = curr;
		            break;
		        default:
		        	if ("".equals(currOpt)) throw new IllegalArgumentException("Expected argument for value '" + curr + "'");
		            options.put(currOpt, curr);
		            currOpt = "";
		            break;
	        }
		}	
	}
}
