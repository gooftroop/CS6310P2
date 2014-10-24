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
		
		if (grid != null) {
			int w = this.getSize().width;
			int h = this.getSize().height;
			
			float cellHeight = (float)h / grid.getGridHeight();
			float cellWidth = (float)w / grid.getGridWidth();
			int cellWidthInt = Math.round((float)Math.ceil(cellWidth));
			int cellHeightInt = Math.round((float)Math.ceil(cellHeight));

			for (int y = 0; y < grid.getGridHeight(); y++) {
				for (int x = 0; x < grid.getGridWidth(); x++) {
					
					float t = grid.getTemperature(x, y);
					
					int celly = Math.round((float)Math.floor(y * cellHeight));
					int cellx = Math.round((float)Math.floor(x * cellWidth));
					
					// "fill" the rectangle with the temp color
					g.setColor(visualizer.calculateColor(t));
					g.fillRect(cellx, celly, cellWidthInt, cellHeightInt);
				}
			}
			// Draw grid lines
			g.setColor(Color.DARK_GRAY);
			for (int y = 1; y < grid.getGridHeight()-1; y++) {
				int celly = Math.round(y * cellHeight);
				g.drawLine(0, celly, w, celly);
			}
			for (int x = 0; x < grid.getGridWidth(); x++) {
				int cellx = Math.round(x * cellWidth);
				g.drawLine(cellx, 0, cellx, h);
			}

		}
	}

	public void update(IGrid grid) {
		this.grid = grid;
		repaint();
	}
}
