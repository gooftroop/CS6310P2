package view.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import view.util.ColorGenerator;

import common.IGrid;

public class GridDisplay extends JPanel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 5907053878068878363L;

	private IGrid grid;
	
	private final ColorGenerator visualizer;
	
	public GridDisplay(ColorGenerator visualizer, int width, int height) {
		this.visualizer = visualizer;
		
		this.setBackground(new Color(0,0,0,0));
		this.setOpaque(false);
		
		Dimension size = new Dimension(width, height);
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	}

	/**
	 * Informs Swing how to render in terms of subcomponents.
	 *
	 * @param g
	 *            Graphics - Graphs context for drawing
	 * @override paintComponent in JPanel
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int wHeight = this.getSize().height;
		int wWidth = this.getSize().width;
		
		if (grid != null) {

			int height = wHeight / grid.getGridHeight();
			int width = wWidth / grid.getGridWidth();

			for (int y = 0; y < grid.getGridHeight(); y++) {
				for (int x = 0; x < grid.getGridWidth(); x++) {
					
					float t = grid.getTemperature(x, y);
					
					int celly = (y * height);
					int cellx = (x * width);
					
					// paint the "grid edge"
					g.setColor(Color.DARK_GRAY);
					g.drawRect(cellx, celly, width, height);
					
					// "fill" the rectangle with the temp color
					g.setColor(visualizer.calculateColor(t));
					g.fillRect(cellx, celly, width, height);
				}
			}
		}
	}

	public void update(IGrid grid) {

		this.grid = grid;
		repaint();
	}
}
