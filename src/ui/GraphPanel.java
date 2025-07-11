package src.ui;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
=======
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3

import src.util.PlotFunction;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.Point2D;

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

    // private List<Point2D.Double> highlightedPoints = new ArrayList<>();
    private Map<Point2D.Double, String> highlightedPoints = new HashMap<>();
    private JTextArea resultsArea;

    public GraphPanel() {
        this.setBackground(Color.BLACK);
        initPanZoomListeners();
        ToolTipManager.sharedInstance().registerComponent(this);
        this.setToolTipText("");
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tooltipText = getFunctionPointText(e);
                setToolTipText(tooltipText);
                ToolTipManager.sharedInstance().mouseMoved(e);
            }
        });
    }

    public void setFunctions(List<PlotFunction> funcs) {
        this.functions = funcs;
        repaint();
    }

    public void setLimits(double xLo, double xHi, double yLo, double yHi) {
        this.xMin = xLo;
        this.xMax = xHi;
        this.yMin = yLo;
        this.yMax = yHi;
        repaint();
    }

<<<<<<< HEAD
    public void displayCriticalPointsResults(String results) {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = new JTextArea(results);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(new Color(30, 30, 30));
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 150));

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Critical Points Analysis",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void highlightAreaUnder(Function<Double, Double> f, double a, double b) {
        this.areaFunction = f;
        this.areaA = a;
        this.areaB = b;
=======
    public void highlightAreaUnder(Function<Double, Double> func, double a, double b) {
        this.highlightedFunc = func;
        this.highlightA = a;
        this.highlightB = b;
        repaint();
    }

    public void clearHighlight() {
        this.highlightedFunc = null;
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
        repaint();
    }

    public void highlightPoints(List<Point2D.Double> points, List<String> types) {
        highlightedPoints.clear();
        for (int i = 0; i < points.size(); i++) {
            highlightedPoints.put(points.get(i), types.get(i));
        }
        repaint();
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
                repaint();
            }
        };
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
    }

    private String getFunctionPointText(MouseEvent e) {
        if (functions == null || functions.isEmpty())
            return null;
        int w = getWidth();
        int h = getHeight();
        double mouseX = e.getX();
        double mouseY = e.getY();
        double worldX = xMin + mouseX * (xMax - xMin) / w;
        final int tol = 5;
        for (PlotFunction pf : functions) {
            double y = pf.getFunction().apply(worldX);
            if (Double.isFinite(y)) {
                double screenY = h - (y - yMin) * h / (yMax - yMin);
                if (Math.abs(screenY - mouseY) <= tol) {
                    return String.format("(%.3f, %.3f)", worldX, y);
                }
            }
        }
        return null;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return tooltipText;
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
                drawFunction(g2, pf, w, h, sX, sY);
            }
        }
<<<<<<< HEAD
        if (!highlightedPoints.isEmpty()) {
            drawHighlightedPoints(g2, w, h, sX, sY);
        }
        drawLimitsInfo(g2);
    }

    private void drawHighlightedArea(Graphics2D g2, Function<Double, Double> func, double a, double b,
            int w, int h, double sX, double sY) {
        int steps = 1000;
        double step = (b - a) / steps;
        int[] xPoints = new int[steps + 2];
        int[] yPoints = new int[steps + 2];
=======

        if (highlightedFunc != null) {
            drawHighlightedArea(g2, w, h, sX, sY);
        }

        drawLimitsInfo(g2);
    }

    private void drawHighlightedArea(Graphics2D g2, int w, int h, double sX, double sY) {
        Path2D.Double path = new Path2D.Double();
        boolean started = false;
        double step = (xMax - xMin) / w;
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3

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
        if (y0 >= 0 && y0 <= h)
            g2.drawLine(0, y0, w, y0);
        int x0 = (int) ((0 - xMin) * sX);
        if (x0 >= 0 && x0 <= w)
            g2.drawLine(x0, 0, x0, h);
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

    private void drawFunction(Graphics2D g2, PlotFunction pf, int w, int h, double sX, double sY) {
        g2.setColor(pf.getColor());
        g2.setStroke(new BasicStroke(2f));
        Path2D.Double path = new Path2D.Double();
        boolean started = false;
        double step = (xMax - xMin) / w;
        for (double x = xMin; x <= xMax; x += step) {
            double y = pf.getFunction().apply(x);
            if (Double.isFinite(y) && y >= yMin && y <= yMax) {
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
        }
        g2.draw(path);
    }

    private void drawLimitsInfo(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(String.format("Limits: X[%.2f, %.2f] Y[%.2f, %.2f]", xMin, xMax, yMin, yMax), 10,
                getHeight() - 10);
    }

    private void drawHighlightedPoints(Graphics2D g2, int w, int h, double sX, double sY) {
        highlightedPoints.forEach((point, type) -> {
            // Set color and label based on EXACT type strings
            Color pointColor;
            String upperType = type.toUpperCase();
            String label;

            switch (upperType) {
                case "MAX":
                    pointColor = Color.RED;
                    label = "M";
                    break;
                case "MIN":
                    pointColor = Color.GREEN;
                    label = "N";
                    break;
                case "INFLECTION":
                    pointColor = Color.YELLOW;
                    label = "I";
                    break;
                default:
                    pointColor = Color.BLUE;
                    label = "?";
                    System.out.println("Unknown point type: " + type); // Debug
            }

            g2.setColor(pointColor);
            int px = (int) ((point.x - xMin) * sX);
            int py = h - (int) ((point.y - yMin) * sY);

            if (px >= 0 && px <= w && py >= 0 && py <= h) {
                int size = 8;
                g2.fillOval(px - size / 2, py - size / 2, size, size);
                g2.setColor(Color.WHITE);
                g2.drawString(label, px + size, py - size);
            }
        });
    }

    public void displayExtremaResults(String results) {
        JTextArea textArea = new JTextArea(results, 10, 30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Custom styling
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Extrema Results",
                JOptionPane.PLAIN_MESSAGE);
    }

   public void exportToSVG(File file) {
    try {
        System.out.println("GraphPanel size: " + this.getWidth() + " x " + this.getHeight());

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new java.awt.Dimension(this.getWidth(), this.getHeight()));

        this.paint(svgGenerator); // Render your graph

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
