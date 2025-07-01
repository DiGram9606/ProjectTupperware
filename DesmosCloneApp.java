package Tupperware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class DesmosCloneApp extends JFrame {
    private GraphPanel graphPanel;
    private JComboBox<String> functionTypeBox;
    private JTextField parametersField;
    private JButton plotButton;
    private JButton clearButton;
    private JButton bgColorButton;
    private List<PlotFunction> functions = new ArrayList<>();
    private Color bgColor = Color.BLACK;

    public DesmosCloneApp() {
        setTitle("Desmos Clone - Function Plotter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel controlPanel = new JPanel();
        functionTypeBox = new JComboBox<>(new String[]{
            "sin(x)", "cos(x)", "tan(x)", "x^2", "x^3", "2x+3", "log(x)", "exp(x)", "step(x)"
        });
        parametersField = new JTextField("1", 5);
        plotButton = new JButton("Plot");
        clearButton = new JButton("Clear");
        bgColorButton = new JButton("BG Color");

        controlPanel.add(new JLabel("Function:"));
        controlPanel.add(functionTypeBox);
        controlPanel.add(new JLabel("Parameter:"));
        controlPanel.add(parametersField);
        controlPanel.add(plotButton);
        controlPanel.add(clearButton);
        controlPanel.add(bgColorButton);

        graphPanel = new GraphPanel();
        add(controlPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

        plotButton.addActionListener(e -> plotFunction());
        clearButton.addActionListener(e -> {
            functions.clear();
            graphPanel.repaint();
        });
        bgColorButton.addActionListener(e -> {
            bgColor = JColorChooser.showDialog(this, "Choose Background Color", bgColor);
            graphPanel.setBackground(bgColor);
        });
    }

    private void plotFunction() {
        String type = (String) functionTypeBox.getSelectedItem();
        double param;
        try {
            param = Double.parseDouble(parametersField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid parameter");
            return;
        }

        Function<Double, Double> func;
        switch (type) {
            case "sin(x)": func = x -> param * Math.sin(x); break;
            case "cos(x)": func = x -> param * Math.cos(x); break;
            case "tan(x)": func = x -> param * Math.tan(x); break;
            case "x^2": func = x -> param * x * x; break;
            case "x^3": func = x -> param * x * x * x; break;
            case "2x+3": func = x -> param * (2 * x + 3); break;
            case "log(x)": func = x -> x <= 0 ? Double.NaN : param * Math.log(x); break;
            case "exp(x)": func = x -> param * Math.exp(x); break;
            case "step(x)": func = x -> x >= 0 ? param : 0; break;
            default: return;
        }

        functions.add(new PlotFunction(type + " with param " + param, func));
        graphPanel.setFunctions(functions);
        graphPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesmosCloneApp().setVisible(true));
    }

    static class PlotFunction {
        String label;
        Function<Double, Double> function;
        Color color;

        PlotFunction(String label, Function<Double, Double> function) {
            this.label = label;
            this.function = function;
            this.color = new Color((int)(Math.random() * 0xFFFFFF));
        }
    }

    static class GraphPanel extends JPanel {
        private List<PlotFunction> functions = new ArrayList<>();
        private int xMin = -20, xMax = 20, yMin = -20, yMax = 20;

        GraphPanel() {
            setBackground(Color.BLACK);
        }

        public void setFunctions(List<PlotFunction> functions) {
            this.functions = functions;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            double xScale = w / (double)(xMax - xMin);
            double yScale = h / (double)(yMax - yMin);

            g2.setColor(Color.GRAY);
            g2.drawLine(0, h / 2, w, h / 2);
            g2.drawLine(w / 2, 0, w / 2, h);

            for (PlotFunction pf : functions) {
                g2.setColor(pf.color);
                Path2D path = new Path2D.Double();
                boolean first = true;
                for (double x = xMin; x <= xMax; x += 0.1) {
                    double y = pf.function.apply(x);
                    if (Double.isFinite(y)) {
                        int px = (int)((x - xMin) * xScale);
                        int py = (int)((yMax - y) * yScale);
                        if (first) {
                            path.moveTo(px, py);
                            first = false;
                        } else {
                            path.lineTo(px, py);
                        }
                    } else {
                        first = true;
                    }
                }
                g2.draw(path);
            }
        }
    }
}