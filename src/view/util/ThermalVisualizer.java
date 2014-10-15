package view.util;

import java.awt.Color;

public class ThermalVisualizer implements ColorGenerator {
	
	private ColorMap colorMap;

    public ThermalVisualizer(String cpName) {
    	colorMap = ColorMap.getMap(cpName);
    }

	@Override
	public Color calculateColor(double temperature) {
		// TODO convert temp to a 0 .. 1.0 scale
		double scaled = 0;
		return this.colorMap.getColor(scaled);
	}

}
