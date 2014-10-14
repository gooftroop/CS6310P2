// SimulationRun.java
package EarthSim;

public class SimulationRun {

	private boolean ownSimThread,ownPresThread;
	private Initiative initiative;
	private long bufferSize;

	private GUI ui;

	public SimulationRun(boolean ownSimThread,boolean ownPresThread,Initiative initiative,long bufferSize, GUI ui){
		this.ownSimThread = ownSimThread;
		this.ownPresThread = ownPresThread;
		this.initiative = initiative;
		this.bufferSize = bufferSize;
		this.ui = ui;
	}

	public void reset(){
		debug("reset called");
	}

	public void start(){
		debug("start called");
	}

	public void pause(){
		debug("pause called");
	}

	public void resume(){
		debug("resume called");
	}

	public void stop(){
		debug("stop called");
	}

	private void debug(String s){
		System.out.println(s);
	}

}
