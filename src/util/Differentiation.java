package src.util;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.Function;

public class Differentiation {
    public static class DerivativeResult {
        public final Function<Double, Double> derivative;
        public final Map<Point2D, String> criticalPoints;

        public DerivativeResult(Function<Double, Double> derivative, Map<Point2D, String> criticalPoints) {
            this.derivative = derivative;
            this.criticalPoints = criticalPoints;
        }
    }

    public static DerivativeResult computeDerivative(Function<Double, Double> func, double xMin, double xMax) {
        double h = 1e-5;
        Function<Double, Double> derivative = x -> {
            try {
                return (func.apply(x + h) - func.apply(x - h)) / (2 * h);
            } catch (Exception e) {
                return Double.NaN;
            }
        };

        Map<Point2D, String> criticalPoints = new HashMap<>();
        double prevVal = derivative.apply(xMin);
        double prevSign = Math.signum(prevVal);
        double xStep = (xMax - xMin) / 1000;

        for (double x = xMin + xStep; x <= xMax; x += xStep) {
            double currVal = derivative.apply(x);
            double currSign = Math.signum(currVal);

            if (Math.abs(currVal) < 1e-3) {
                double y = func.apply(x);
                if (Double.isFinite(y)) {
                    criticalPoints.put(new Point2D.Double(x, y), "Possible Inflection/Extremum");
                }
            } else if (prevSign != 0 && currSign != 0 && prevSign != currSign) {
                double y = func.apply(x);
                if (Double.isFinite(y)) {
                    criticalPoints.put(new Point2D.Double(x, y), "Local Extremum");
                }
            }

            prevSign = currSign;
        }

        return new DerivativeResult(derivative, criticalPoints);
    }
}
