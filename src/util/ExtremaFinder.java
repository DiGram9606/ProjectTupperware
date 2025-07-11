package src.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExtremaFinder {

    // Approximates derivative using central difference
    public static double derivative(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    /**
     * Finds extrema (local minima/maxima) by identifying where the derivative crosses 0.
     */
    public static List<Double> findExtrema(String expr, double xMin, double xMax, double step, double tolerance) {
        List<Double> extrema = new ArrayList<>();
        double h = 1e-5;
        Function<Double, Double> f = EquationParser.parse(expr);

        for (double x = xMin; x < xMax; x += step) {
            double d1 = derivative(f, x, h);
            double d2 = derivative(f, x + step, h);

            if (d1 * d2 < 0) {
                // Root of derivative between x and x + step → use bisection
                double a = x, b = x + step;
                for (int i = 0; i < 100; i++) {
                    double mid = (a + b) / 2;
                    double dm = derivative(f, mid, h);
                    if (Math.abs(dm) < tolerance) {
                        extrema.add(mid);
                        break;
                    } else if (derivative(f, a, h) * dm < 0) {
                        b = mid;
                    } else {
                        a = mid;
                    }
                }
            }
        }

        return extrema;
    }
}
