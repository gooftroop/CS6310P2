package EarthSim.view;

import java.awt.BorderLayout;
import java.awt.Container;
//import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;

import util.GridDisplayPane.ColorGenerator;
import util.GridDisplayPane.GridDisplayPane;
import util.GridDisplayPane.GridDisplayable;

// Code for all up display window

@SuppressWarnings("serial")
public class EarthDisplay extends JFrame {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	// private SimulatorForm form;
	private GridDisplayPane gridPane;
	private Boolean showRunStats;
	
	public EarthDisplay() {
		this(false);
	}
	
	public EarthDisplay(Boolean showRunStats) {
		this.showRunStats = showRunStats;

		setTitle("Earth Display");
		setSize(new Dimension(WIDTH, HEIGHT));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
		// pack();
	}

	/**
	 * Method to initialize the UI and draw components
	 */
	private void init() {
		Container pane = getContentPane();
		
		// Buildup top pane with earth data
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(BorderFactory
				.createTitledBorder("Mercator Projection of Earth Temperatures"));
		// TODO: probably want something to extend the base grid display and add
		// overlay indication of current sun position
		gridPane = new GridDisplayPane();
		top.add(gridPane, BorderLayout.CENTER);

		//Build up stats pane
		JPanel statsExternal = new JPanel(new BorderLayout());
		JPanel stats = new JPanel(new GridLayout(0, 2, 10, 3));
		stats.setBorder(BorderFactory.createTitledBorder("Run Stats"));
		//TODO: add runtime stats of interest (time/rotation/performance stats/etc)
		stats.add(new JLabel("Stat1: "));
		stats.add(new JLabel("value"));
		stats.add(new JLabel("Stat2: "));
		stats.add(new JLabel("value"));
		stats.add(new JLabel("Stat3: "));
		stats.add(new JLabel("value"));
		statsExternal.add(stats, BorderLayout.PAGE_END);
		
		// Build up final pane
		pane.add(top, BorderLayout.CENTER);
		if (showRunStats) {
			pane.add(statsExternal, BorderLayout.LINE_START);
		}
	}

	public void updateGrid(GridDisplayable grid) {
		gridPane.updateGrid(grid);
	}

	public void setVisualizer(ColorGenerator visualizer) {
		gridPane.setVisualizer(visualizer);
	}

}
