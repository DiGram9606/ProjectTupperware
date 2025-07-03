package src.util;

import java.awt.Color;
import java.util.function.Function;

public class PlotFunction {
    private final String label;
    private final Function<Double, Double> function;
    private final Color color;

    public PlotFunction(String label, Function<Double, Double> function) {
        this.label = label;
        this.function = function;
        this.color = new Color((int)(Math.random() * 0xFFFFFF));
    }

    public String getLabel() {
        return label;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }

    public Color getColor() {
        return color;
    }
}
