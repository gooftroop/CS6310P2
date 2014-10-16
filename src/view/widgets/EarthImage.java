package view.widgets;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class EarthImage extends JPanel {
	
	private static final String EARTH_IMAGE = "../resource/earth.jpg";

	/**
	 * 
	 */
	private static final long serialVersionUID = 6319433780080785942L;
	
	private BufferedImage earth;
	
	public EarthImage() {
		try {
			earth = ImageIO.read(new File(EARTH_IMAGE));
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(earth,  0,  0,  null);
	}
}
