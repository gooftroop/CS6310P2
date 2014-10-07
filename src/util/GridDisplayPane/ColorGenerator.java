package util.GridDisplayPane;

import java.awt.*;

/**
 * Abstract class for different heat visualization algorithms.
 */
public interface ColorGenerator {

    /**
     * Method to calculate RGB color from temperature value
     *
     * @param temperature value
     * @return color object
     */
    public Color calculateColor(double temperature);
}
