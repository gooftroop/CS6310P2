package view.widgets;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimulationStatus extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4874764682275993951L;
	
	private JTextField sunPosStats, currTimeStatus, gsStatus, timeStepStatus;
	private JLabel lblSunPos, lblCurrTime, lblGs, lblTimeStep;
	
	private static final int HEIGHT = 4;
	private static final int WIDTH = 2;
	private static final int HGAP = 1;
	private static final int VGAP = 1;
	
	public SimulationStatus() {
		
		this.setLayout(new GridLayout(HEIGHT,  WIDTH, HGAP, VGAP));
		
		sunPosStats 	= new JTextField("0");
		currTimeStatus 	= new JTextField("0");
		gsStatus 		= new JTextField("0");
		timeStepStatus 	= new JTextField("0");
		
		lblSunPos 	= new JLabel("Rotational Position:");
		lblCurrTime = new JLabel("Time:");
		lblGs 		= new JLabel("Grid Spacing:");
		lblTimeStep = new JLabel("Simulation Time Step:");
		
		this.add(lblSunPos);
		this.add(sunPosStats);
		
		this.add(lblCurrTime);
		this.add(currTimeStatus);
		
		this.add(lblGs);
		this.add(gsStatus);
		
		this.add(lblTimeStep);
		this.add(timeStepStatus);
	}
	
	public void update(int sunPosition, int currentTime, int gs, int timeStep) {
		
		this.sunPosStats.setText(Integer.toString(sunPosition));
		this.currTimeStatus.setText(Integer.toString(currentTime));
		this.gsStatus.setText(Integer.toString(gs));
		this.timeStepStatus.setText(Integer.toString(timeStep));
		
		this.validate();
	}
}
