package src.util;

import java.util.function.Function;

public class AreaCalculator {

    // Performs trapezoidal integration and returns area
    public static double computeDefiniteIntegral(Function<Double, Double> function, double a, double b, int n) {
        if (a > b) {
            double temp = a;
            a = b;
            b = temp;
        }

        double h = (b - a) / n;
        double area = 0.5 * (safeApply(function, a) + safeApply(function, b));

        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            area += safeApply(function, x);
        }

        return area * h;
    }

    private static double safeApply(Function<Double, Double> func, double x) {
        try {
            double y = func.apply(x);
            return Double.isFinite(y) ? y : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
