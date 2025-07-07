package src.util;

import java.util.function.Function;

public class AreaCalculator {
    public static double calculateArea(Function<Double, Double> function, double xMin, double xMax, int steps) {
        double dx = (xMax - xMin) / steps;
        double area = 0.0;

        for (int i = 0; i < steps; i++) {
            double x = xMin + i * dx;
            double y = function.apply(x);
            if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                area += y * dx;
            }
        }

        return area;
    }
}
