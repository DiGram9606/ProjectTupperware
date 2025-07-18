package src.ui;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Function;
import javax.swing.*;
import src.util.PlotFunction;
import src.util.IntersectionSolver;
import src.util.ExtremaFinder;
import src.util.Derivative;

public class GraphPanel extends JPanel {
    private List<PlotFunction> functions;
    private double xMin = -10.0;
    private double xMax = 10.0;
    private double yMin = -10.0;
    private double yMax = 10.0;
    private int lastMouseX;
    private int lastMouseY;
    private String tooltipText = null;
    private Function<Double, Double> highlightedFunc = null;
    private double highlightA = 0;
    private double highlightB = 0;
    private Map<Point2D, String> highlightedPoints = new HashMap<>();
    
    // Intersection points storage
    private List<IntersectionSolver.IntersectionPoint> intersectionPoints = new ArrayList<>();
    
    // Extrema points storage
    private List<ExtremaFinder.ExtremaPoint> extremaPoints = new ArrayList<>();
    
    // Derivative analysis storage
    private Derivative.DerivativeResult derivativeResult = null;
    private List<Point2D> nonDifferentiablePoints = new ArrayList<>();

    public GraphPanel() {
        this.setBackground(Color.BLACK);
        initPanZoomListeners();
        ToolTipManager.sharedInstance().registerComponent(this);
        this.setToolTipText("");
        
        // Enhanced mouse listener for intersection detection
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tooltipText = getPointInfoText(e);
                setToolTipText(tooltipText);
                ToolTipManager.sharedInstance().mouseMoved(e);
            }
        });
        
        // Add mouse motion listener for real-time hover detection
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String hoverText = getPointInfoText(e);
                setToolTipText(hoverText);
                repaint();
            }
        });
    }

    public void setFunctions(List<PlotFunction> funcs) {
        this.functions = funcs;
        calculateIntersections();
        repaint();
    }

    public void setLimits(double xLo, double xHi, double yLo, double yHi) {
        this.xMin = xLo;
        this.xMax = xHi;
        this.yMin = yLo;
        this.yMax = yHi;
        repaint();
    }

    public void highlightAreaUnder(Function<Double, Double> func, double a, double b) {
        this.highlightedFunc = func;
        this.highlightA = a;
        this.highlightB = b;
        repaint();
    }

    public void clearHighlight() {
        this.highlightedFunc = null;
        this.highlightedPoints.clear();
        repaint();
    }

    public void displayCriticalPointsResults(String results) {
        JOptionPane.showMessageDialog(this, results, "Critical Points Analysis", JOptionPane.INFORMATION_MESSAGE);
    }

    public void highlightCriticalPoints(Map<Point2D, String> points) {
        this.highlightedPoints = points;
        repaint();
    }

    // Method to display extrema
    public void highlightExtrema(List<ExtremaFinder.ExtremaPoint> extrema) {
        this.extremaPoints = extrema;
        repaint();
    }

    // Methods for derivative functionality
    public void setDerivativeResult(Derivative.DerivativeResult result) {
        this.derivativeResult = result;
        repaint();
    }

    public void clearDerivative() {
        this.derivativeResult = null;
        this.nonDifferentiablePoints.clear();
        repaint();
    }

    public void highlightNonDifferentiablePoints(List<Point2D> points) {
        this.nonDifferentiablePoints = points;
        repaint();
    }

    // Calculate all intersections between functions
    private void calculateIntersections() {
        intersectionPoints.clear();
        if (functions == null || functions.size() < 2) {
            return;
        }

        for (int i = 0; i < functions.size(); i++) {
            for (int j = i + 1; j < functions.size(); j++) {
                List<IntersectionSolver.IntersectionPoint> intersections = 
                    IntersectionSolver.findAllIntersections(functions.get(i), functions.get(j), xMin, xMax);
                intersectionPoints.addAll(intersections);
            }
        }
    }

    private void initPanZoomListeners() {
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                double factor = Math.pow(1.1, rotation);
                int w = getWidth();
                int h = getHeight();
                double mouseX = e.getX();
                double mouseY = e.getY();
                double worldX = xMin + mouseX * (xMax - xMin) / w;
                double worldY = yMax - mouseY * (yMax - yMin) / h;
                double newWidth = (xMax - xMin) * factor;
                double newHeight = (yMax - yMin) * factor;
                xMin = worldX - (mouseX / w) * newWidth;
                xMax = xMin + newWidth;
                yMax = worldY + (mouseY / h) * newHeight;
                yMin = yMax - newHeight;
                calculateIntersections(); // Recalculate intersections after zoom
                repaint();
            }
        });

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int dx = x - lastMouseX;
                int dy = y - lastMouseY;
                int w = getWidth();
                int h = getHeight();
                double worldDX = -dx * (xMax - xMin) / w;
                double worldDY = dy * (yMax - yMin) / h;
                xMin += worldDX;
                xMax += worldDX;
                yMin += worldDY;
                yMax += worldDY;
                lastMouseX = x;
                lastMouseY = y;
                calculateIntersections(); // Recalculate intersections after pan
                repaint();
            }
        };
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
    }

    // Enhanced method to get point information including intersections with coordinates
    private String getPointInfoText(MouseEvent e) {
        if (functions == null || functions.isEmpty()) return null;

        int w = getWidth();
        int h = getHeight();
        double mouseX = e.getX();
        double mouseY = e.getY();
        double worldX = xMin + mouseX * (xMax - xMin) / w;
        double worldY = yMax - mouseY * (yMax - yMin) / h;

        final int tolerance = 8;

        // Check for non-differentiable points
        for (Point2D point : nonDifferentiablePoints) {
            double screenX = (point.getX() - xMin) * w / (xMax - xMin);
            double screenY = h - (point.getY() - yMin) * h / (yMax - yMin);
            
            double distance = Math.sqrt(Math.pow(screenX - mouseX, 2) + Math.pow(screenY - mouseY, 2));
            
            if (distance <= tolerance) {
                return String.format("<html><b>Non-Differentiable Point</b><br><b>X: %.4f</b><br><b>Y: %.4f</b><br>Function has sharp corner or cusp</html>", 
                    point.getX(), point.getY());
            }
        }

        // First check for intersection points
        for (IntersectionSolver.IntersectionPoint intersection : intersectionPoints) {
            Point2D point = intersection.point;
            double screenX = (point.getX() - xMin) * w / (xMax - xMin);
            double screenY = h - (point.getY() - yMin) * h / (yMax - yMin);
            
            double distance = Math.sqrt(Math.pow(screenX - mouseX, 2) + Math.pow(screenY - mouseY, 2));
            
            if (distance <= tolerance) {
                return String.format("<html><b>Intersection Point</b><br><b>X: %.4f</b><br><b>Y: %.4f</b><br>Between: %s<br>and: %s</html>", 
                    point.getX(), point.getY(), 
                    intersection.function1Name, 
                    intersection.function2Name);
            }
        }

        // Then check for regular function points
        for (PlotFunction pf : functions) {
            double y = pf.getFunction().apply(worldX);
            if (Double.isFinite(y)) {
                double screenY = h - (y - yMin) * h / (yMax - yMin);
                if (Math.abs(screenY - mouseY) <= tolerance) {
                    // Check if this is a derivative function
                    String functionType = pf.getLabel().startsWith("d/dx[") ? "Derivative" : "Function";
                    return String.format("<html><b>%s Point</b><br><b>X: %.4f</b><br><b>Y: %.4f</b><br>Function: %s</html>", 
                        functionType, worldX, y, pf.getLabel());
                }
            }
        }

        return null;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return getPointInfoText(e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        double sX = w / (xMax - xMin);
        double sY = h / (yMax - yMin);

        drawGrid(g2, w, h, sX, sY);
        drawAxes(g2, w, h, sX, sY);
        drawLabels(g2, w, h, sX, sY);

        if (functions != null) {
            for (PlotFunction pf : functions) {
                if (isStepFunction(pf)) {
                    drawStepFunction(g2, pf, w, h, sX, sY);
                } else {
                    drawFunction(g2, pf, w, h, sX, sY);
                }
            }
        }

        // Draw intersection points
        drawIntersectionPoints(g2, w, h, sX, sY);

        // Draw extrema points
        drawExtremaPoints(g2, w, h, sX, sY);

        // Draw non-differentiable points
        drawNonDifferentiablePoints(g2, w, h, sX, sY);

        if (!highlightedPoints.isEmpty()) {
            drawCriticalPoints(g2, w, h, sX, sY);
        }

        if (highlightedFunc != null) {
            drawHighlightedArea(g2, w, h, sX, sY);
        }

        drawLimitsInfo(g2);
    }

    // Check if function is a step function
    private boolean isStepFunction(PlotFunction pf) {
        return pf.getLabel().toLowerCase().contains("step");
    }

    // Specialized drawing method for step functions
    private void drawStepFunction(Graphics2D g2, PlotFunction pf, int w, int h, double sX, double sY) {
        g2.setColor(pf.getColor());
        g2.setStroke(new BasicStroke(2f));

        // Draw horizontal line segments for each integer interval
        for (int i = (int) Math.floor(xMin); i <= (int) Math.ceil(xMax); i++) {
            double stepValue = i; // The y-value for this step
            
            // Define the x-range for this step: [i, i+1)
            double xStart = Math.max(i, xMin);
            double xEnd = Math.min(i + 1, xMax);
            
            if (xStart < xEnd && stepValue >= yMin && stepValue <= yMax) {
                // Convert to screen coordinates
                int screenXStart = (int) ((xStart - xMin) * sX);
                int screenXEnd = (int) ((xEnd - xMin) * sX);
                int screenY = h - (int) ((stepValue - yMin) * sY);
                
                // Draw horizontal line segment
                g2.drawLine(screenXStart, screenY, screenXEnd, screenY);
                
                // Draw closed circle at left endpoint (included)
                if (i >= xMin && i <= xMax) {
                    int screenXLeft = (int) ((i - xMin) * sX);
                    g2.fillOval(screenXLeft - 3, screenY - 3, 6, 6);
                }
                
                // Draw open circle at right endpoint (excluded)
                if (i + 1 >= xMin && i + 1 <= xMax && i + 1 < xMax) {
                    int screenXRight = (int) ((i + 1 - xMin) * sX);
                    g2.setColor(Color.BLACK);
                    g2.fillOval(screenXRight - 3, screenY - 3, 6, 6);
                    g2.setColor(pf.getColor());
                    g2.drawOval(screenXRight - 3, screenY - 3, 6, 6);
                }
            }
        }
    }

    // Method to draw intersection points
    private void drawIntersectionPoints(Graphics2D g2, int w, int h, double sX, double sY) {
        for (IntersectionSolver.IntersectionPoint intersection : intersectionPoints) {
            Point2D point = intersection.point;
            
            // Convert world coordinates to screen coordinates
            int px = (int) ((point.getX() - xMin) * sX);
            int py = h - (int) ((point.getY() - yMin) * sY);
            
            // Only draw if the point is visible on screen
            if (px >= 0 && px <= w && py >= 0 && py <= h) {
                // Draw a smaller highlighted circle for intersection points (reduced from 12px to 8px)
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2f));
                g2.fillOval(px - 4, py - 4, 8, 8);
                
                // Draw border
                g2.setColor(Color.RED);
                g2.drawOval(px - 4, py - 4, 8, 8);
                
                // Draw a small cross in the center
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(px - 2, py, px + 2, py);
                g2.drawLine(px, py - 2, px, py + 2);
            }
        }
    }

    // Method to draw extrema points
    private void drawExtremaPoints(Graphics2D g2, int w, int h, double sX, double sY) {
        for (ExtremaFinder.ExtremaPoint extrema : extremaPoints) {
            Point2D point = extrema.point;
            
            // Convert to screen coordinates
            int px = (int) ((point.getX() - xMin) * sX);
            int py = h - (int) ((point.getY() - yMin) * sY);
            
            // Only draw if visible
            if (px >= 0 && px <= w && py >= 0 && py <= h) {
                // Choose color based on type
                Color color;
                if (extrema.type.contains("Global Maximum")) {
                    color = Color.RED;
                } else if (extrema.type.contains("Global Minimum")) {
                    color = Color.BLUE;
                } else if (extrema.type.contains("Local Maximum")) {
                    color = Color.ORANGE;
                } else if (extrema.type.contains("Local Minimum")) {
                    color = Color.CYAN;
                } else {
                    color = Color.YELLOW;
                }
                
                // Draw the point
                g2.setColor(color);
                g2.fillOval(px - 5, py - 5, 10, 10);
                
                // Draw border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(px - 5, py - 5, 10, 10);
                
                // Draw label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                String label = extrema.type.substring(0, Math.min(extrema.type.length(), 8));
                g2.drawString(label, px + 8, py - 8);
            }
        }
    }

    // Method to draw non-differentiable points
    private void drawNonDifferentiablePoints(Graphics2D g2, int w, int h, double sX, double sY) {
        for (Point2D point : nonDifferentiablePoints) {
            // Convert to screen coordinates
            int px = (int) ((point.getX() - xMin) * sX);
            int py = h - (int) ((point.getY() - yMin) * sY);
            
            // Only draw if visible
            if (px >= 0 && px <= w && py >= 0 && py <= h) {
                // Draw purple diamond for non-differentiable points
                g2.setColor(new Color(128, 0, 128)); // Purple
                g2.setStroke(new BasicStroke(2f));
                
                // Draw diamond shape
                int[] xPoints = {px, px + 6, px, px - 6};
                int[] yPoints = {py - 6, py, py + 6, py};
                g2.fillPolygon(xPoints, yPoints, 4);
                
                // Draw white border
                g2.setColor(Color.WHITE);
                g2.drawPolygon(xPoints, yPoints, 4);
                
                // Draw exclamation mark
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 8));
                g2.drawString("!", px - 2, py + 2);
            }
        }
    }

    private void drawCriticalPoints(Graphics2D g2, int w, int h, double sX, double sY) {
        g2.setStroke(new BasicStroke(3f));
        for (Map.Entry<Point2D, String> entry : highlightedPoints.entrySet()) {
            Point2D point = entry.getKey();
            String type = entry.getValue();
            int px = (int) ((point.getX() - xMin) * sX);
            int py = h - (int) ((point.getY() - yMin) * sY);

            if (type.contains("Maximum")) {
                g2.setColor(Color.RED);
            } else if (type.contains("Minimum")) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.YELLOW);
            }

            g2.fillOval(px - 4, py - 4, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawOval(px - 4, py - 4, 8, 8);
        }
    }

    private void drawHighlightedArea(Graphics2D g2, int w, int h, double sX, double sY) {
        Path2D.Double path = new Path2D.Double();
        boolean started = false;
        double step = (xMax - xMin) / w;

        for (double x = highlightA; x <= highlightB; x += step) {
            double y = highlightedFunc.apply(x);
            if (Double.isFinite(y)) {
                int px = (int) ((x - xMin) * sX);
                int py = h - (int) ((y - yMin) * sY);
                if (!started) {
                    path.moveTo(px, py);
                    started = true;
                } else {
                    path.lineTo(px, py);
                }
            }
        }

        int endX = (int) ((highlightB - xMin) * sX);
        int startX = (int) ((highlightA - xMin) * sX);
        int baseY = h - (int) ((0 - yMin) * sY);
        path.lineTo(endX, baseY);
        path.lineTo(startX, baseY);
        path.closePath();

        g2.setColor(new Color(255, 255, 0, 100));
        g2.fill(path);
    }

    private void drawGrid(Graphics2D g2, int w, int h, double sX, double sY) {
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(0.5f));

        for (double x = Math.ceil(xMin); x <= Math.floor(xMax); x++) {
            int px = (int) ((x - xMin) * sX);
            g2.drawLine(px, 0, px, h);
        }

        for (double y = Math.ceil(yMin); y <= Math.floor(yMax); y++) {
            int py = h - (int) ((y - yMin) * sY);
            g2.drawLine(0, py, w, py);
        }
    }

    private void drawAxes(Graphics2D g2, int w, int h, double sX, double sY) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));

        int y0 = h - (int) ((0 - yMin) * sY);
        if (y0 >= 0 && y0 <= h) g2.drawLine(0, y0, w, y0);

        int x0 = (int) ((0 - xMin) * sX);
        if (x0 >= 0 && x0 <= w) g2.drawLine(x0, 0, x0, h);
    }

    private void drawLabels(Graphics2D g2, int w, int h, double sX, double sY) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        int y0 = h - (int) ((0 - yMin) * sY);
        int x0 = (int) ((0 - xMin) * sX);

        for (double x = Math.ceil(xMin); x <= Math.floor(xMax); x++) {
            if (Math.abs(x) > 1e-6) {
                int px = (int) ((x - xMin) * sX);
                g2.drawString(String.format("%.0f", x), px - 5, Math.min(y0 + 18, h - 5));
            }
        }

        for (double y = Math.ceil(yMin); y <= Math.floor(yMax); y++) {
            if (Math.abs(y) > 1e-6) {
                int py = h - (int) ((y - yMin) * sY);
                g2.drawString(String.format("%.0f", y), Math.max(x0 + 8, 5), py + 4);
            }
        }

        if (xMin < 0 && xMax > 0 && yMin < 0 && yMax > 0) {
            g2.setColor(Color.WHITE);
            g2.drawString("0", x0 + 8, y0 + 18);
        }
    }

    // Regular function drawing for non-step functions
    private void drawFunction(Graphics2D g2, PlotFunction pf, int w, int h, double sX, double sY) {
        // Use different style for derivative functions
        if (pf.getLabel().startsWith("d/dx[")) {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                0, new float[]{5f, 5f}, 0)); // Dashed line for derivatives
        } else {
            g2.setStroke(new BasicStroke(2f));
        }
        
        g2.setColor(pf.getColor());

        Path2D.Double path = new Path2D.Double();
        boolean started = false;
        double step = Math.max((xMax - xMin) / w, 0.001);
        double prevY = Double.NaN;

        for (double x = xMin; x <= xMax; x += step) {
            double y = pf.getFunction().apply(x);
            boolean finite = Double.isFinite(y);
            boolean jump = finite && Double.isFinite(prevY) && Math.abs(y - prevY) > 10;

            if (finite && y >= yMin && y <= yMax && !jump) {
                int px = (int) ((x - xMin) * sX);
                int py = h - (int) ((y - yMin) * sY);
                if (!started) {
                    path.moveTo(px, py);
                    started = true;
                } else {
                    path.lineTo(px, py);
                }
            } else {
                started = false;
            }

            if (finite) prevY = y;
        }

        g2.draw(path);
    }

    private void drawLimitsInfo(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(String.format("Limits: X[%.2f, %.2f] Y[%.2f, %.2f]", xMin, xMax, yMin, yMax), 
                     10, getHeight() - 10);
        
        // Show intersection count
        if (!intersectionPoints.isEmpty()) {
            g2.setColor(Color.YELLOW);
            g2.drawString(String.format("Intersections: %d", intersectionPoints.size()), 
                         10, getHeight() - 30);
        }
        
        // Show extrema count
        if (!extremaPoints.isEmpty()) {
            g2.setColor(Color.ORANGE);
            g2.drawString(String.format("Extrema: %d", extremaPoints.size()), 
                         10, getHeight() - 50);
        }
        
        // Show non-differentiable points count
        if (!nonDifferentiablePoints.isEmpty()) {
            g2.setColor(new Color(128, 0, 128));
            g2.drawString(String.format("Non-diff points: %d", nonDifferentiablePoints.size()), 
                         10, getHeight() - 70);
        }
        
        // Show derivative info
        if (derivativeResult != null) {
            g2.setColor(Color.GREEN);
            g2.drawString("Derivative: " + derivativeResult.derivativeExpression, 
                         10, getHeight() - 90);
        }
    }

    public void exportToSVG(File file) {
        try {
            System.out.println("GraphPanel size: " + this.getWidth() + " x " + this.getHeight());
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
            String svgNS = "http://www.w3.org/2000/svg";
            Document document = domImpl.createDocument(svgNS, "svg", null);
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
            svgGenerator.setSVGCanvasSize(new java.awt.Dimension(this.getWidth(), this.getHeight()));
            this.paint(svgGenerator);
            boolean useCSS = true;
            try (Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
                svgGenerator.stream(out, useCSS);
            }
            System.out.println("Export complete. Saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
