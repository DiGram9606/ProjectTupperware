package src.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class IntersectionSolver {
private static final double TOLERANCE = 1e-10;
private static final int MAX_ITERATIONS = 1000;

public static class IntersectionPoint {
public Point2D point;
public String function1Name;
public String function2Name;

public IntersectionPoint(Point2D point, String f1, String f2) {
this.point = point;
this.function1Name = f1;
this.function2Name = f2;
}
}

public static Point2D findIntersection(PlotFunction f1, PlotFunction f2, double startX, double endX) {
return bisectionMethod(f1, f2, startX, endX);
}

private static Point2D bisectionMethod(PlotFunction f1, PlotFunction f2, double a, double b) {
try {
double fa = f1.getFunction().apply(a) - f2.getFunction().apply(a);
double fb = f1.getFunction().apply(b) - f2.getFunction().apply(b);

if (fa * fb > 0) {
return null;
}

double c = a;
for (int i = 0; i < MAX_ITERATIONS; i++) {
c = (a + b) / 2;
double fc = f1.getFunction().apply(c) - f2.getFunction().apply(c);

if (Math.abs(fc) < TOLERANCE) {
break;
}

if (fa * fc < 0) {
b = c;
fb = fc;
} else {
a = c;
fa = fc;
}
}

double y = f1.getFunction().apply(c);
return new Point2D.Double(c, y);
} catch (Exception e) {
return null;
}
}

public static List<IntersectionPoint> findAllIntersections(PlotFunction f1, PlotFunction f2, double startX, double endX) {
List<IntersectionPoint> intersections = new ArrayList<>();
double step = 0.1;

for (double x = startX; x < endX - step; x += step) {
try {
double y1_curr = f1.getFunction().apply(x);
double y2_curr = f2.getFunction().apply(x);
double y1_next = f1.getFunction().apply(x + step);
double y2_next = f2.getFunction().apply(x + step);

if ((y1_curr - y2_curr) * (y1_next - y2_next) < 0) {
Point2D intersection = findIntersection(f1, f2, x, x + step);
if (intersection != null) {
intersections.add(new IntersectionPoint(intersection, f1.getLabel(), f2.getLabel()));
}
}
} catch (Exception e) {
}
}

return intersections;
}
}

