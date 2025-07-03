package src.ui;

import src.util.PlotFunction;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

public class GraphPanel extends JPanel {
    private List<PlotFunction> functions;
    private double xMin = -10, xMax = 10, yMin = -10, yMax = 10;

    public void setFunctions(List<PlotFunction> functions) {
        this.functions = functions;
    }

    public void setLimits(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        repaint();
    }

    public GraphPanel() {
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        double xScale = width / (xMax - xMin);
        double yScale = height / (yMax - yMin);

        drawGrid(g2, width, height, xScale, yScale);
        drawAxes(g2, width, height, xScale, yScale);
        drawLabels(g2, width, height, xScale, yScale);

        if (functions != null) {
            for (PlotFunction pf : functions) {
                drawFunction(g2, pf, width, height, xScale, yScale);
            }
        }
        
        drawLimitsInfo(g2);
    }

    private void drawGrid(Graphics2D g2, int width, int height, double xScale, double yScale) {
        // Simple grid lines
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(0.5f));
        
        for (double x = Math.ceil(xMin); x <= Math.floor(xMax); x++) {
            int xPos = (int) ((x - xMin) * xScale);
            g2.drawLine(xPos, 0, xPos, height);
        }
        
        for (double y = Math.ceil(yMin); y <= Math.floor(yMax); y++) {
            int yPos = height - (int) ((y - yMin) * yScale);
            g2.drawLine(0, yPos, width, yPos);
        }
    }

    private void drawAxes(Graphics2D g2, int width, int height, double xScale, double yScale) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f));
        
        int xAxisY = height - (int) ((0 - yMin) * yScale);
        if (xAxisY >= 0 && xAxisY <= height) {
            g2.drawLine(0, xAxisY, width, xAxisY);
        }
        
        int yAxisX = (int) ((0 - xMin) * xScale);
        if (yAxisX >= 0 && yAxisX <= width) {
            g2.drawLine(yAxisX, 0, yAxisX, height);
        }
    }

    private void drawLabels(Graphics2D g2, int width, int height, double xScale, double yScale) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        
        int xAxisY = height - (int) ((0 - yMin) * yScale);
        int yAxisX = (int) ((0 - xMin) * xScale);
        
        for (double x = Math.ceil(xMin); x <= Math.floor(xMax); x++) {
            if (Math.abs(x) < 0.001) continue;
            int xPos = (int) ((x - xMin) * xScale);
            String label = String.format("%.0f", x);
            g2.drawString(label, xPos - 5, Math.min(xAxisY + 18, height - 5));
        }
        
        for (double y = Math.ceil(yMin); y <= Math.floor(yMax); y++) {
            if (Math.abs(y) < 0.001) continue;
            int yPos = height - (int) ((y - yMin) * yScale);
            String label = String.format("%.0f", y);
            g2.drawString(label, Math.max(yAxisX + 8, 5), yPos + 4);
        }
        
        if (xMin < 0 && xMax > 0 && yMin < 0 && yMax > 0) {
            g2.setColor(Color.WHITE);
            g2.drawString("0", yAxisX + 8, xAxisY + 18);
        }
    }

    private void drawFunction(Graphics2D g2, PlotFunction pf, int width, int height, double xScale, double yScale) {
        g2.setColor(pf.getColor());
        g2.setStroke(new BasicStroke(2.0f));
        
        Path2D path = new Path2D.Double();
        boolean firstPoint = true;
        double step = (xMax - xMin) / width;
        
        for (double x = xMin; x <= xMax; x += step) {
            double y = pf.getFunction().apply(x);
            
            if (Double.isFinite(y) && y >= yMin && y <= yMax) {
                int px = (int) ((x - xMin) * xScale);
                int py = height - (int) ((y - yMin) * yScale);
                
                if (firstPoint) {
                    path.moveTo(px, py);
                    firstPoint = false;
                } else {
                    path.lineTo(px, py);
                }
            } else {
                firstPoint = true;
            }
        }
        
        g2.draw(path);
    }

    private void drawLimitsInfo(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String info = String.format("Limits: X[%.1f, %.1f] Y[%.1f, %.1f]", xMin, xMax, yMin, yMax);
        g2.drawString(info, 10, getHeight() - 10);
    }
}
