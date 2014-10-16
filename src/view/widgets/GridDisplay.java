package view.widgets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

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
	
	public GridDisplay(ColorGenerator visualizer) {
		this.visualizer = visualizer;
	}

	/**
	 * Paints one cell of the grid.
	 *
	 * @param aGraphics
	 *            Graphics into which painting will be done
	 * @param x
	 *            row number of the grid cell
	 * @param y
	 *            column number of the grid cell
	 * @param t
	 *            intensity of Color red to be painted; a number from 0.0 to 1.0
	 */
	private void paintSpot(Graphics g, int x, int y, int width, int height, float t) {
		
		// paint the "grid edge"
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
		
		// "fill" the rectangle with the temp color
		g.setColor(visualizer.calculateColor(t));
		g.fillRect(x, y, width, height);
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
		BufferedImage bi = new BufferedImage(wWidth, wHeight, BufferedImage.TYPE_INT_ARGB);
		
		if (grid != null) {

			Graphics render = bi.createGraphics();
			
			int height = wHeight / grid.getGridHeight();
			int width = wWidth / grid.getGridWidth();

			for (int y = 0; y < grid.getGridHeight(); y++) {
				for (int x = 0; x < grid.getGridWidth(); x++) {
					paintSpot(render, x, y, width, height, grid.getTemperature(x, y));
				}
			}
		}
		
		g.drawImage(bi, 0, 0, this);
	}

	public void update(IGrid grid) {
		this.grid = grid;
		repaint();
	}
}
