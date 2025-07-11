package src.util;

import java.util.function.Function;

public class LimitChecker {

    public static class LimitResult {
        public double leftLimit;
        public double rightLimit;
        public boolean isDifferentiable;

        public LimitResult(double left, double right, boolean diff) {
            this.leftLimit = left;
            this.rightLimit = right;
            this.isDifferentiable = diff;
        }
    }

    /**
     * Approximates left and right limits near x0, and checks if function is differentiable.
     */
    public static LimitResult checkLimit(String expr, double x0, double epsilon, double tolerance) {
        Function<Double, Double> f = EquationParser.parse(expr);
        double left = f.apply(x0 - epsilon);
        double right = f.apply(x0 + epsilon);
        boolean differentiable = Math.abs(left - right) < tolerance;
        return new LimitResult(left, right, differentiable);
    }
}

