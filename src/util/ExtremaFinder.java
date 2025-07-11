package src.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExtremaFinder {
    private static final double TOLERANCE = 1e-6;
    private static final double DERIVATIVE_H = 1e-8;
    
    public static class ExtremaPoint {
        public Point2D point;
        public String type; // "Global Maximum", "Global Minimum", "Local Maximum", "Local Minimum"
        public String functionName;
        
        public ExtremaPoint(Point2D point, String type, String functionName) {
            this.point = point;
            this.type = type;
            this.functionName = functionName;
        }
        
        @Override
        public String toString() {
            return String.format("%s at (%.4f, %.4f) for %s", 
                type, point.getX(), point.getY(), functionName);
        }
    }
    
    public static class ExtremaResult {
        public List<ExtremaPoint> allExtrema;
        public ExtremaPoint globalMaximum;
        public ExtremaPoint globalMinimum;
        public String summary;
        
        public ExtremaResult() {
            this.allExtrema = new ArrayList<>();
        }
    }
    
    // Calculate numerical derivative using central difference
    public static double derivative(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }
    
    // Calculate second derivative for concavity test
    public static double secondDerivative(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
    }
    
    // Find all critical points in the interval
    public static List<Double> findCriticalPoints(Function<Double, Double> f, double xMin, double xMax, double step) {
        List<Double> criticalPoints = new ArrayList<>();
        
        for (double x = xMin; x < xMax; x += step) {
            double d1 = derivative(f, x, DERIVATIVE_H);
            double d2 = derivative(f, x + step, DERIVATIVE_H);
            
            // Check for sign change in derivative (indicates critical point)
            if (d1 * d2 < 0) {
                // Use bisection method to find exact critical point
                double criticalX = bisectionForCriticalPoint(f, x, x + step);
                if (criticalX != Double.NaN) {
                    criticalPoints.add(criticalX);
                }
            }
            
            // Check for zero derivative
            if (Math.abs(d1) < TOLERANCE) {
                criticalPoints.add(x);
            }
        }
        
        return criticalPoints;
    }
    
    // Bisection method to find exact critical point
    private static double bisectionForCriticalPoint(Function<Double, Double> f, double a, double b) {
        double left = a, right = b;
        
        for (int i = 0; i < 100; i++) {
            double mid = (left + right) / 2;
            double derivative = derivative(f, mid, DERIVATIVE_H);
            
            if (Math.abs(derivative) < TOLERANCE) {
                return mid;
            }
            
            double leftDerivative = derivative(f, left, DERIVATIVE_H);
            if (leftDerivative * derivative < 0) {
                right = mid;
            } else {
                left = mid;
            }
            
            if (Math.abs(right - left) < TOLERANCE) {
                return (left + right) / 2;
            }
        }
        
        return Double.NaN;
    }
    
    // Main method to find all extrema in the given interval
    public static ExtremaResult findExtrema(PlotFunction plotFunction, double xMin, double xMax, double step) {
        ExtremaResult result = new ExtremaResult();
        Function<Double, Double> f = plotFunction.getFunction();
        String functionName = plotFunction.getLabel();
        
        // Find critical points
        List<Double> criticalPoints = findCriticalPoints(f, xMin, xMax, step);
        
        // Add endpoints to check
        List<Double> candidatePoints = new ArrayList<>(criticalPoints);
        candidatePoints.add(xMin);
        candidatePoints.add(xMax);
        
        // Evaluate function at all candidate points
        List<Point2D> candidateValues = new ArrayList<>();
        for (Double x : candidatePoints) {
            if (x >= xMin && x <= xMax) {
                double y = f.apply(x);
                if (Double.isFinite(y)) {
                    candidateValues.add(new Point2D.Double(x, y));
                }
            }
        }
        
        if (candidateValues.isEmpty()) {
            result.summary = "No valid points found in the interval";
            return result;
        }
        
        // Find global extrema
        Point2D globalMax = candidateValues.get(0);
        Point2D globalMin = candidateValues.get(0);
        
        for (Point2D point : candidateValues) {
            if (point.getY() > globalMax.getY()) {
                globalMax = point;
            }
            if (point.getY() < globalMin.getY()) {
                globalMin = point;
            }
        }
        
        // Classify extrema
        for (Double x : criticalPoints) {
            if (x > xMin && x < xMax) { // Interior points only
                double y = f.apply(x);
                if (Double.isFinite(y)) {
                    Point2D point = new Point2D.Double(x, y);
                    String type = classifyExtrema(f, x);
                    
                    // Check if it's also a global extremum
                    if (Math.abs(y - globalMax.getY()) < TOLERANCE) {
                        type = "Global Maximum";
                    } else if (Math.abs(y - globalMin.getY()) < TOLERANCE) {
                        type = "Global Minimum";
                    }
                    
                    result.allExtrema.add(new ExtremaPoint(point, type, functionName));
                }
            }
        }
        
        // Add global extrema if they occur at endpoints
        if (globalMax.getX() == xMin || globalMax.getX() == xMax) {
            result.allExtrema.add(new ExtremaPoint(globalMax, "Global Maximum", functionName));
        }
        if (globalMin.getX() == xMin || globalMin.getX() == xMax) {
            result.allExtrema.add(new ExtremaPoint(globalMin, "Global Minimum", functionName));
        }
        
        result.globalMaximum = new ExtremaPoint(globalMax, "Global Maximum", functionName);
        result.globalMinimum = new ExtremaPoint(globalMin, "Global Minimum", functionName);
        
        // Create summary
        result.summary = createSummary(result, xMin, xMax);
        
        return result;
    }
    
    // Classify extrema using second derivative test
    private static String classifyExtrema(Function<Double, Double> f, double x) {
        double secondDeriv = secondDerivative(f, x, DERIVATIVE_H);
        
        if (secondDeriv > TOLERANCE) {
            return "Local Minimum";
        } else if (secondDeriv < -TOLERANCE) {
            return "Local Maximum";
        } else {
            return "Inflection Point";
        }
    }
    
    // Create summary string
    private static String createSummary(ExtremaResult result, double xMin, double xMax) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Extrema Analysis for interval [%.3f, %.3f]:\n\n", xMin, xMax));
        
        if (result.globalMaximum != null) {
            sb.append(String.format("Global Maximum: %.4f at x = %.4f\n", 
                result.globalMaximum.point.getY(), result.globalMaximum.point.getX()));
        }
        
        if (result.globalMinimum != null) {
            sb.append(String.format("Global Minimum: %.4f at x = %.4f\n", 
                result.globalMinimum.point.getY(), result.globalMinimum.point.getX()));
        }
        
        sb.append(String.format("\nTotal extrema found: %d\n", result.allExtrema.size()));
        
        if (!result.allExtrema.isEmpty()) {
            sb.append("\nDetailed breakdown:\n");
            for (ExtremaPoint point : result.allExtrema) {
                sb.append(String.format("- %s\n", point.toString()));
            }
        }
        
        return sb.toString();
    }
    
    // Find extrema for multiple functions
    public static List<ExtremaResult> findExtremaForAllFunctions(List<PlotFunction> functions, 
                                                                double xMin, double xMax, double step) {
        List<ExtremaResult> results = new ArrayList<>();
        
        for (PlotFunction pf : functions) {
            ExtremaResult result = findExtrema(pf, xMin, xMax, step);
            results.add(result);
        }
        
        return results;
    }
    
    // Legacy method for backward compatibility
    public static List<Double> findExtrema(String expr, double xMin, double xMax, double step, double tolerance) {
        List<Double> extrema = new ArrayList<>();
        double h = 1e-5;
        Function<Double, Double> f = EquationParser.parse(expr);
        
        for (double x = xMin; x < xMax; x += step) {
            double d1 = derivative(f, x, h);
            double d2 = derivative(f, x + step, h);
            
            if (d1 * d2 < 0) {
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
