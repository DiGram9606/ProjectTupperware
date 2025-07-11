package src.util;

import java.util.function.Function;

public class Evaluator {
    public static double evaluate(String expression, double x) {
        try {
            Function<Double, Double> func = EquationParser.parse(expression);
            return func.apply(x);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
