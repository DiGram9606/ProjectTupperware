package src.ui;

import src.util.PlotFunction;
import src.util.IntersectionSolver;
import src.util.AreaCalculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DesmosCloneApp extends JFrame {
    private GraphPanel graphPanel;
    private JComboBox<String> functionTypeBox;
    private JTextField parametersField;
    private JButton plotButton, clearButton, bgColorButton, intersectionButton;
    private JButton saveButton, loadButton;
    private JButton areaButton;
    private JTextField areaFromField, areaToField;

    private List<PlotFunction> functions = new ArrayList<>();
    private Color bgColor = Color.BLACK;

    private JTextField xMinField, xMaxField, yMinField, yMaxField;
    private JButton setLimitsButton, resetLimitsButton;

    private JLabel statusLabel, functionCountLabel;

    public DesmosCloneApp() {
        setTitle("Function Plotter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        setupEventListeners();
        updateStatus();
    }

    private void initializeComponents() {
        functionTypeBox = new JComboBox<>(new String[]{
            "sin(x)", "cos(x)", "tan(x)", "x^2", "x^3", "2x+3", "log(x)", "exp(x)", "step(x)"
        });

        parametersField = new JTextField("1", 8);
        plotButton = new JButton("Plot Function");
        clearButton = new JButton("Clear All");
        bgColorButton = new JButton("Background Color");
        intersectionButton = new JButton("Find Intersections");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");

        xMinField = new JTextField("-10", 5);
        xMaxField = new JTextField("10", 5);
        yMinField = new JTextField("-10", 5);
        yMaxField = new JTextField("10", 5);
        setLimitsButton = new JButton("Apply Limits");
        resetLimitsButton = new JButton("Reset");

        areaButton = new JButton("Calculate Area");
        areaFromField = new JTextField("-5", 5);
        areaToField = new JTextField("5", 5);

        statusLabel = new JLabel("Ready to plot functions");
        functionCountLabel = new JLabel("Functions: 0");

        graphPanel = new GraphPanel();
        graphPanel.setBackground(bgColor);
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel functionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        functionPanel.setBorder(BorderFactory.createTitledBorder("Function Controls"));
        functionPanel.add(new JLabel("Function:"));
        functionPanel.add(functionTypeBox);
        functionPanel.add(new JLabel("Parameter:"));
        functionPanel.add(parametersField);
        functionPanel.add(plotButton);
        functionPanel.add(clearButton);
        functionPanel.add(bgColorButton);
        functionPanel.add(intersectionButton);
        functionPanel.add(saveButton);
        functionPanel.add(loadButton);

        JPanel limitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        limitsPanel.setBorder(BorderFactory.createTitledBorder("Axis Limits"));
        limitsPanel.add(new JLabel("X Min:"));
        limitsPanel.add(xMinField);
        limitsPanel.add(new JLabel("X Max:"));
        limitsPanel.add(xMaxField);
        limitsPanel.add(new JLabel("Y Min:"));
        limitsPanel.add(yMinField);
        limitsPanel.add(new JLabel("Y Max:"));
        limitsPanel.add(yMaxField);
        limitsPanel.add(setLimitsButton);
        limitsPanel.add(resetLimitsButton);

        JPanel areaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        areaPanel.setBorder(BorderFactory.createTitledBorder("Area Under Curve"));
        areaPanel.add(new JLabel("From x:"));
        areaPanel.add(areaFromField);
        areaPanel.add(new JLabel("To x:"));
        areaPanel.add(areaToField);
        areaPanel.add(areaButton);

        topPanel.add(functionPanel, BorderLayout.NORTH);
        topPanel.add(limitsPanel, BorderLayout.CENTER);
        topPanel.add(areaPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(functionCountLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        plotButton.addActionListener(e -> plotFunction());
        clearButton.addActionListener(e -> {
            functions.clear();
            graphPanel.repaint();
            updateStatus();
        });
        bgColorButton.addActionListener(e -> {
            bgColor = JColorChooser.showDialog(this, "Choose Background Color", bgColor);
            if (bgColor != null) {
                graphPanel.setBackground(bgColor);
            }
        });
        intersectionButton.addActionListener(e -> findIntersections());
        setLimitsButton.addActionListener(e -> setAxisLimits());
        resetLimitsButton.addActionListener(e -> resetLimits());
        areaButton.addActionListener(e -> calculateArea());
    }

    private void plotFunction() {
        String type = (String) functionTypeBox.getSelectedItem();
        double param;

        try {
            param = Double.parseDouble(parametersField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid parameter.");
            return;
        }

        Function<Double, Double> func;
        switch (type) {
            case "sin(x)": func = x -> param * Math.sin(x); break;
            case "cos(x)": func = x -> param * Math.cos(x); break;
            case "tan(x)": func = x -> param * Math.tan(x); break;
            case "x^2":    func = x -> param * x * x; break;
            case "x^3":    func = x -> param * x * x * x; break;
            case "2x+3":   func = x -> param * (2 * x + 3); break;
            case "log(x)": func = x -> x <= 0 ? Double.NaN : param * Math.log(x); break;
            case "exp(x)": func = x -> param * Math.exp(x); break;
            case "step(x)":func = x -> x >= 0 ? param : 0; break;
            default: return;
        }

        functions.add(new PlotFunction(type + " (param: " + param + ")", func));
        graphPanel.setFunctions(functions);
        graphPanel.repaint();
        updateStatus();
    }

    private void setAxisLimits() {
        try {
            double xMin = Double.parseDouble(xMinField.getText());
            double xMax = Double.parseDouble(xMaxField.getText());
            double yMin = Double.parseDouble(yMinField.getText());
            double yMax = Double.parseDouble(yMaxField.getText());

            if (xMin >= xMax || yMin >= yMax) {
                JOptionPane.showMessageDialog(this, "Invalid range.");
                return;
            }

            graphPanel.setLimits(xMin, xMax, yMin, yMax);
            graphPanel.repaint();
            statusLabel.setText("Limits updated");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers.");
        }
    }

    private void resetLimits() {
        xMinField.setText("-10");
        xMaxField.setText("10");
        yMinField.setText("-10");
        yMaxField.setText("10");
        setAxisLimits();
        statusLabel.setText("Limits reset");
    }

    private void updateStatus() {
        functionCountLabel.setText("Functions: " + functions.size());
        statusLabel.setText(functions.isEmpty() ? "Ready to plot functions" : "Displaying " + functions.size() + " function(s)");
    }

    private void findIntersections() {
        if (functions.size() < 2) {
            JOptionPane.showMessageDialog(this, "At least 2 functions required.");
            return;
        }

        List<IntersectionSolver.IntersectionPoint> intersections = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            for (int j = i + 1; j < functions.size(); j++) {
                intersections.addAll(
                    IntersectionSolver.findAllIntersections(
                        functions.get(i), functions.get(j), -10, 10));
            }
        }

        if (intersections.isEmpty()) {
            statusLabel.setText("No intersections found");
        } else {
            statusLabel.setText("Found " + intersections.size() + " intersections");
            StringBuilder msg = new StringBuilder();
            for (var pt : intersections) {
                msg.append(String.format("Between %s and %s: (%.3f, %.3f)\n",
                        pt.function1Name, pt.function2Name,
                        pt.point.getX(), pt.point.getY()));
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Intersections", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void calculateArea() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            double xFrom = Double.parseDouble(areaFromField.getText());
            double xTo = Double.parseDouble(areaToField.getText());
            if (xFrom >= xTo) {
                JOptionPane.showMessageDialog(this, "From x must be less than To x");
                return;
            }

            PlotFunction func = functions.get(functions.size() - 1);
            double area = AreaCalculator.calculateArea(func.getFunction(), xFrom, xTo, 1000);
            JOptionPane.showMessageDialog(this, String.format("Area from %.2f to %.2f = %.5f", xFrom, xTo, area));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid x-range for area.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesmosCloneApp().setVisible(true));
    }
}
