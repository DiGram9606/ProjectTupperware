package src.util;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Point2D;

public class Derivative {
    private static final double EPSILON = 1e-10;
    private static final double H_DERIVATIVE = 1e-8;
    
    public static class DerivativeResult {
        public Function<Double, Double> derivativeFunction;
        public List<Point2D> nonDifferentiablePoints;
        public String derivativeExpression;
        public String summary;
        
        public DerivativeResult() {
            this.nonDifferentiablePoints = new ArrayList<>();
        }
    }
    
    public static class LimitResult {
        public double leftLimit;
        public double rightLimit;
        public boolean limitExists;
        public boolean isDifferentiable;
        public String reason;
        
        public LimitResult(double left, double right, boolean exists, boolean diff, String reason) {
            this.leftLimit = left;
            this.rightLimit = right;
            this.limitExists = exists;
            this.isDifferentiable = diff;
            this.reason = reason;
        }
    }
    
    // Calculate numerical derivative using central difference
    public static double numericalDerivative(Function<Double, Double> f, double x) {
        try {
            double h = H_DERIVATIVE;
            double fxPlusH = f.apply(x + h);
            double fxMinusH = f.apply(x - h);
            
            if (!Double.isFinite(fxPlusH) || !Double.isFinite(fxMinusH)) {
                // Try forward difference
                double fx = f.apply(x);
                if (Double.isFinite(fx) && Double.isFinite(fxPlusH)) {
                    return (fxPlusH - fx) / h;
                }
                // Try backward difference
                if (Double.isFinite(fx) && Double.isFinite(fxMinusH)) {
                    return (fx - fxMinusH) / h;
                }
                return Double.NaN;
            }
            
            return (fxPlusH - fxMinusH) / (2 * h);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    // Check if function is differentiable at a point using limit definition
    public static LimitResult checkDifferentiability(Function<Double, Double> f, double x) {
        try {
            double h = H_DERIVATIVE;
            
            // Calculate left-hand derivative limit
            double leftLimit = calculateLeftDerivativeLimit(f, x, h);
            
            // Calculate right-hand derivative limit
            double rightLimit = calculateRightDerivativeLimit(f, x, h);
            
            boolean limitExists = Double.isFinite(leftLimit) && Double.isFinite(rightLimit);
            boolean isDifferentiable = limitExists && Math.abs(leftLimit - rightLimit) < EPSILON;
            
            String reason = "";
            if (!limitExists) {
                reason = "Derivative limits do not exist";
            } else if (!isDifferentiable) {
                reason = String.format("Left limit (%.6f) ≠ Right limit (%.6f)", leftLimit, rightLimit);
            } else {
                reason = "Function is differentiable";
            }
            
            return new LimitResult(leftLimit, rightLimit, limitExists, isDifferentiable, reason);
        } catch (Exception e) {
            return new LimitResult(Double.NaN, Double.NaN, false, false, "Error in calculation: " + e.getMessage());
        }
    }
    
    private static double calculateLeftDerivativeLimit(Function<Double, Double> f, double x, double h) {
        try {
            double fx = f.apply(x);
            if (!Double.isFinite(fx)) return Double.NaN;
            
            // Calculate limit as h approaches 0 from the left
            double limit = Double.NaN;
            for (int i = 1; i <= 10; i++) {
                double testH = h / Math.pow(10, i);
                double fxMinusH = f.apply(x - testH);
                if (Double.isFinite(fxMinusH)) {
                    limit = (fx - fxMinusH) / testH;
                }
            }
            return limit;
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    private static double calculateRightDerivativeLimit(Function<Double, Double> f, double x, double h) {
        try {
            double fx = f.apply(x);
            if (!Double.isFinite(fx)) return Double.NaN;
            
            // Calculate limit as h approaches 0 from the right
            double limit = Double.NaN;
            for (int i = 1; i <= 10; i++) {
                double testH = h / Math.pow(10, i);
                double fxPlusH = f.apply(x + testH);
                if (Double.isFinite(fxPlusH)) {
                    limit = (fxPlusH - fx) / testH;
                }
            }
            return limit;
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    // Create derivative function with comprehensive analysis
    public static DerivativeResult createDerivativeFunction(PlotFunction originalFunction, double xMin, double xMax) {
        DerivativeResult result = new DerivativeResult();
        Function<Double, Double> originalFunc = originalFunction.getFunction();
        String originalExpression = originalFunction.getLabel();
        
        // Create numerical derivative function
        result.derivativeFunction = x -> numericalDerivative(originalFunc, x);
        
        // Scan for non-differentiable points
        double step = (xMax - xMin) / 1000.0;
        for (double x = xMin; x <= xMax; x += step) {
            LimitResult limitCheck = checkDifferentiability(originalFunc, x);
            if (!limitCheck.isDifferentiable && limitCheck.limitExists) {
                double y = originalFunc.apply(x);
                if (Double.isFinite(y)) {
                    result.nonDifferentiablePoints.add(new Point2D.Double(x, y));
                }
            }
        }
        
        // Generate derivative expression (symbolic if possible, numerical otherwise)
        result.derivativeExpression = generateDerivativeExpression(originalExpression);
        
        // Create summary
        result.summary = createDerivativeSummary(originalExpression, result);
        
        return result;
    }
    
    private static String generateDerivativeExpression(String original) {
        try {
            // Simple symbolic differentiation for common cases
            original = original.toLowerCase().trim();
            
            if (original.equals("x")) return "1";
            if (original.matches("\\d+")) return "0";
            if (original.equals("sin(x)")) return "cos(x)";
            if (original.equals("cos(x)")) return "-sin(x)";
            if (original.equals("tan(x)")) return "sec²(x)";
            if (original.equals("exp(x)")) return "exp(x)";
            if (original.equals("log(x)")) return "1/x";
            if (original.equals("sqrt(x)")) return "1/(2√x)";
            
            // Pattern matching for simple polynomials
            if (original.matches("x\\^\\d+")) {
                String[] parts = original.split("\\^");
                if (parts.length == 2) {
                    int power = Integer.parseInt(parts[1]);
                    if (power == 1) return "1";
                    if (power == 2) return "2x";
                    return power + "x^" + (power - 1);
                }
            }
            
            // Pattern matching for coefficients
            if (original.matches("\\d+\\*?x")) {
                String coeff = original.replaceAll("[*x]", "");
                return coeff;
            }
            
            if (original.matches("\\d+\\*?x\\^\\d+")) {
                String[] parts = original.split("\\*?x\\^");
                if (parts.length == 2) {
                    int coeff = Integer.parseInt(parts[0]);
                    int power = Integer.parseInt(parts[1]);
                    int newCoeff = coeff * power;
                    if (power - 1 == 0) return String.valueOf(newCoeff);
                    if (power - 1 == 1) return newCoeff + "x";
                    return newCoeff + "x^" + (power - 1);
                }
            }
            
            return "d/dx[" + original + "]";
        } catch (Exception e) {
            return "d/dx[" + original + "]";
        }
    }
    
    private static String createDerivativeSummary(String original, DerivativeResult result) {
        StringBuilder summary = new StringBuilder();
        summary.append("Derivative Analysis:\n\n");
        summary.append("Original Function: ").append(original).append("\n");
        summary.append("Derivative: ").append(result.derivativeExpression).append("\n\n");
        
        if (result.nonDifferentiablePoints.isEmpty()) {
            summary.append("✓ Function is differentiable everywhere in the domain\n");
        } else {
            summary.append("⚠ Non-differentiable points found:\n");
            for (Point2D point : result.nonDifferentiablePoints) {
                summary.append(String.format("  • x = %.4f (sharp corner or cusp)\n", point.getX()));
            }
        }
        
        summary.append("\nNote: Derivative calculated using numerical methods");
        return summary.toString();
    }
    
    // Legacy symbolic differentiation (kept for compatibility)
    public static String differentiate(String expr) {
        try {
            expr = expr.replaceAll("\\s+", "").toLowerCase();
            
            if (expr.startsWith("(") && expr.endsWith(")")) {
                return differentiate(expr.substring(1, expr.length() - 1));
            }
            
            // Handle addition
            int plus = findTopLevelOperator(expr, '+');
            if (plus > 0) {
                return differentiate(expr.substring(0, plus)) + " + " + differentiate(expr.substring(plus + 1));
            }
            
            // Handle subtraction
            int minus = findTopLevelOperator(expr, '-');
            if (minus > 0) {
                return differentiate(expr.substring(0, minus)) + " - " + differentiate(expr.substring(minus + 1));
            }
            
            // Handle multiplication (product rule)
            int mult = findTopLevelOperator(expr, '*');
            if (mult > 0) {
                String u = expr.substring(0, mult);
                String v = expr.substring(mult + 1);
                return "(" + differentiate(u) + ")*(" + v + ")+(" + u + ")*(" + differentiate(v) + ")";
            }
            
            // Handle division (quotient rule)
            int div = findTopLevelOperator(expr, '/');
            if (div > 0) {
                String u = expr.substring(0, div);
                String v = expr.substring(div + 1);
                return "((" + differentiate(u) + ")*(" + v + ")-(" + u + ")*(" + differentiate(v) + "))/((" + v + ")^2)";
            }
            
            // Handle exponentiation (power rule and chain rule)
            int pow = findTopLevelOperator(expr, '^');
            if (pow > 0) {
                String base = expr.substring(0, pow);
                String exp = expr.substring(pow + 1);
                if (base.equals("x")) {
                    return exp + "*x^(" + exp + "-1)";
                }
                return exp + "*(" + base + ")^(" + exp + "-1)*(" + differentiate(base) + ")";
            }
            
            // Handle basic cases
            if (expr.equals("x")) return "1";
            if (expr.matches("-?\\d+(\\.\\d+)?")) return "0";
            
            // Handle trigonometric functions
            if (expr.startsWith("sin(") && expr.endsWith(")")) {
                String arg = expr.substring(4, expr.length() - 1);
                return "cos(" + arg + ")*(" + differentiate(arg) + ")";
            }
            if (expr.startsWith("cos(") && expr.endsWith(")")) {
                String arg = expr.substring(4, expr.length() - 1);
                return "-sin(" + arg + ")*(" + differentiate(arg) + ")";
            }
            if (expr.startsWith("tan(") && expr.endsWith(")")) {
                String arg = expr.substring(4, expr.length() - 1);
                return "1/(cos(" + arg + ")^2)*(" + differentiate(arg) + ")";
            }
            
            // Handle other functions
            if (expr.startsWith("exp(") && expr.endsWith(")")) {
                String arg = expr.substring(4, expr.length() - 1);
                return "exp(" + arg + ")*(" + differentiate(arg) + ")";
            }
            if (expr.startsWith("log(") && expr.endsWith(")")) {
                String arg = expr.substring(4, expr.length() - 1);
                return "1/(" + arg + ")*(" + differentiate(arg) + ")";
            }
            if (expr.startsWith("sqrt(") && expr.endsWith(")")) {
                String arg = expr.substring(5, expr.length() - 1);
                return "0.5/sqrt(" + arg + ")*(" + differentiate(arg) + ")";
            }
            
            throw new UnsupportedOperationException("Cannot differentiate: " + expr);
        } catch (Exception e) {
            return "Error in differentiation";
        }
    }
    
    private static int findTopLevelOperator(String expr, char op) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == op && depth == 0) return i;
        }
        return -1;
    }
}
