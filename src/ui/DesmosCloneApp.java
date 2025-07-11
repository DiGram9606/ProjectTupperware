package src.ui;

import src.ui.GraphPanel;
import src.util.PlotFunction;
import src.util.IntersectionSolver;
import src.util.SavedGraphState;
<<<<<<< HEAD
// import src.ui.DesmosCloneApp.ExtremaPoint;
=======
import src.util.EquationMaker;
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
import src.util.AreaCalculator;
import src.util.EquationParser;
import javax.swing.*;
import java.awt.*;
<<<<<<< HEAD
// import java.awt.event.*;
=======
import java.awt.event.*;
import java.io.File;
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
// Add this import at the top of DesmosCloneApp.java
import java.awt.geom.Point2D;

public class DesmosCloneApp extends JFrame {
    private GraphPanel graphPanel;
    private JComboBox<String> functionTypeBox;
    private JTextField parametersField;
    private JButton plotButton;
    private JButton clearButton;
    private JButton exportSVGButton;
    private JButton bgColorButton;
    private JButton intersectionButton;
    private JButton saveButton;
    private JButton loadButton;

    private List<PlotFunction> functions = new ArrayList<>();
    private Color bgColor = Color.BLACK;

    private JTextField xMinField, xMaxField, yMinField, yMaxField;
    private JButton setLimitsButton;
    private JButton resetLimitsButton;

    private JLabel statusLabel;
    private JLabel functionCountLabel;
    private JLabel integralValueLabel;

    // New components for integration
    private JTextField lowerLimitField, upperLimitField;
    private JButton calculateAreaButton;

    // Equation Maker
    private JButton equationMakerButton;

    private JButton findExtremaButton;
    private JTextField extremaSearchMinField;
    private JTextField extremaSearchMaxField;
    private JLabel extremaInfoLabel;

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

    public class CriticalPoint {
        public Point2D.Double point;
        public String type; // "MAX", "MIN", "INFLECTION", or "UNKNOWN"

        public CriticalPoint(Point2D.Double point, String type) {
            this.point = point;
            this.type = type.toUpperCase();
        }

        @Override
        public String toString() {
            return String.format("(%.3f, %.3f) - %s", point.x, point.y, type);
        }
    }

    private void initializeComponents() {
        functionTypeBox = new JComboBox<>(new String[]{
            "sin(x)", "cos(x)", "tan(x)", "x^2", "x^3", "2x+3", "log(x)", "exp(x)", "step(x)"
        });

        parametersField = new JTextField("1", 8);

        plotButton = new JButton("Plot Function");
        plotButton.setFont(new Font("Arial", Font.PLAIN, 12));

        exportSVGButton = new JButton("Export as SVG");
        exportSVGButton.setFont(new Font("Arial", Font.PLAIN, 12));

        clearButton = new JButton("Clear All");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));

        bgColorButton = new JButton("Background Color");
        bgColorButton.setFont(new Font("Arial", Font.PLAIN, 12));

        intersectionButton = new JButton("Find Intersections");
        intersectionButton.setFont(new Font("Arial", Font.PLAIN, 12));

        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 12));

        loadButton = new JButton("Load");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 12));

        xMinField = new JTextField("-10", 8);
        xMaxField = new JTextField("10", 8);
        yMinField = new JTextField("-10", 8);
        yMaxField = new JTextField("10", 8);

        setLimitsButton = new JButton("Apply Limits");
        setLimitsButton.setFont(new Font("Arial", Font.PLAIN, 12));

        resetLimitsButton = new JButton("Reset");
        resetLimitsButton.setFont(new Font("Arial", Font.PLAIN, 12));

        statusLabel = new JLabel("Ready to plot functions");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        functionCountLabel = new JLabel("Functions: 0");
        functionCountLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        integralValueLabel = new JLabel("Integral: N/A");
        integralValueLabel.setFont(new Font("Arial", Font.PLAIN, 11));

<<<<<<< HEAD
        diffPointField = new JTextField("1", 6);
        calculateDerivativeButton = new JButton("Differentiate");
        derivativeValueLabel = new JLabel("Derivative: N/A");
        derivativeValueLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        findExtremaButton = new JButton("Find Extrema");
        findExtremaButton.setFont(new Font("Arial", Font.PLAIN, 12));
        extremaSearchMinField = new JTextField("-5", 6);
        extremaSearchMaxField = new JTextField("5", 6);
        extremaInfoLabel = new JLabel("Extrema: N/A");
        extremaInfoLabel.setFont(new Font("Arial", Font.PLAIN, 11));

=======
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
        lowerLimitField = new JTextField("0", 6);
        upperLimitField = new JTextField("5", 6);
        calculateAreaButton = new JButton("Calculate Area");

        equationMakerButton = new JButton("Equation Maker");
        equationMakerButton.setFont(new Font("Arial", Font.PLAIN, 12));

        graphPanel = new GraphPanel();
        graphPanel.setBackground(bgColor);
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.X_AXIS));
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
        functionPanel.add(exportSVGButton);

        JScrollPane functionScrollPane = new JScrollPane(
        functionPanel,
        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

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
        areaPanel.setBorder(BorderFactory.createTitledBorder("Definite Integral"));

        areaPanel.add(new JLabel("Lower Limit:"));
        areaPanel.add(lowerLimitField);
        areaPanel.add(new JLabel("Upper Limit:"));
        areaPanel.add(upperLimitField);
        areaPanel.add(calculateAreaButton);

        topPanel.add(functionScrollPane, BorderLayout.NORTH);
        topPanel.add(limitsPanel, BorderLayout.CENTER);
        topPanel.add(areaPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeft.add(statusLabel);
        bottomLeft.add(integralValueLabel);

        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(functionCountLabel, BorderLayout.EAST);

        // --- Equation Maker button below the graph ---
        JPanel equationMakerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        equationMakerPanel.add(equationMakerButton);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(equationMakerPanel);
        southPanel.add(bottomPanel);

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
<<<<<<< HEAD
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        diffPanel.setBorder(BorderFactory.createTitledBorder("Differentiation"));

        diffPanel.add(new JLabel("At x ="));
        diffPanel.add(diffPointField);
        diffPanel.add(calculateDerivativeButton);
        diffPanel.add(derivativeValueLabel);

        diffPanel.add(new JLabel("Search Range:"));
        diffPanel.add(extremaSearchMinField);
        diffPanel.add(new JLabel("to"));
        diffPanel.add(extremaSearchMaxField);
        diffPanel.add(findExtremaButton);
        diffPanel.add(extremaInfoLabel);

        // Add to topPanel below areaPanel:
        topPanel.add(diffPanel, BorderLayout.AFTER_LAST_LINE);

=======
        add(southPanel, BorderLayout.SOUTH);
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
    }

    private void setupEventListeners() {
        plotButton.addActionListener(e -> plotFunction());
        clearButton.addActionListener(e -> {
            functions.clear();
            graphPanel.clearHighlight();
            graphPanel.repaint();
            updateStatus();
            integralValueLabel.setText("Integral: N/A");
        });
        bgColorButton.addActionListener(e -> {
            bgColor = JColorChooser.showDialog(this, "Choose Background Color", bgColor);
            if (bgColor != null) {
                graphPanel.setBackground(bgColor);
            }
        });
        intersectionButton.addActionListener(e -> findIntersections());
        saveButton.addActionListener(e -> saveGraphStateToFile());
        loadButton.addActionListener(e -> loadGraphStateFromFile());

        exportSVGButton.addActionListener(e -> exportGraphToSVG());

        setLimitsButton.addActionListener(e -> setAxisLimits());
        resetLimitsButton.addActionListener(e -> resetLimits());

        parametersField.addActionListener(e -> plotFunction());
        xMinField.addActionListener(e -> setAxisLimits());
        xMaxField.addActionListener(e -> setAxisLimits());
        yMinField.addActionListener(e -> setAxisLimits());
        yMaxField.addActionListener(e -> setAxisLimits());

<<<<<<< HEAD
        calculateDerivativeButton.addActionListener(e -> calculateDerivative());
        findExtremaButton.addActionListener(e -> findExtrema());

=======
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
        calculateAreaButton.addActionListener(e -> calculateDefiniteIntegral());

        equationMakerButton.addActionListener(e -> openEquationMaker());
    }

    private void calculateDefiniteIntegral() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            double a = Double.parseDouble(lowerLimitField.getText());
            double b = Double.parseDouble(upperLimitField.getText());
            PlotFunction pf = functions.get(functions.size() - 1);
            Function<Double, Double> f = pf.getFunction();

            double area = AreaCalculator.computeDefiniteIntegral(f, a, b, 1000);
            graphPanel.highlightAreaUnder(f, a, b);
            integralValueLabel.setText(String.format("Integral: %.5f", area));
            JOptionPane.showMessageDialog(this,
                String.format("Definite integral from %.2f to %.2f:\nArea = %.5f", a, b, area),
                "Integral Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid limits for integration.");
        }
    }

<<<<<<< HEAD
    // private void calculateDerivative() {
    // if (functions.isEmpty()) {
    // JOptionPane.showMessageDialog(this, "No function plotted.");
    // return;
    // }

    // try {
    // double x = Double.parseDouble(diffPointField.getText());
    // PlotFunction pf = functions.get(functions.size() - 1); // Most recent
    // function
    // Function<Double, Double> f = pf.getFunction();

    // double h = 1e-5;
    // double derivative = (f.apply(x + h) - f.apply(x - h)) / (2 * h);

    // derivativeValueLabel.setText(String.format("Derivative: f'(%1.2f) = %1.5f",
    // x, derivative));
    // JOptionPane.showMessageDialog(this,
    // String.format("f'(%1.4f) ≈ %.6f", x, derivative),
    // "Differentiation Result", JOptionPane.INFORMATION_MESSAGE);
    // } catch (NumberFormatException ex) {
    // JOptionPane.showMessageDialog(this, "Invalid input for x.");
    // } catch (Exception e) {
    // JOptionPane.showMessageDialog(this, "Error during differentiation: " +
    // e.getMessage());
    // }
    // }

    private void findExtrema() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            double a = Double.parseDouble(extremaSearchMinField.getText());
            double b = Double.parseDouble(extremaSearchMaxField.getText());
            if (a >= b) {
                JOptionPane.showMessageDialog(this, "Invalid range: min must be less than max");
                return;
            }

            PlotFunction pf = functions.get(functions.size() - 1);
            Function<Double, Double> f = pf.getFunction();

            List<Point2D.Double> criticalPoints = findCriticalPoints(f, a, b);
            // List<Point2D.Double> extrema = classifyCriticalPoints(f, criticalPoints);
            // List<ExtremaPointCr> extrema = classifyCriticalPoints(f, criticalPoints);
            List<CriticalPoint> inflectionPoints = findInflectionPoints(f, a, b);

            List<CriticalPoint> allPoints = new ArrayList<>();
            allPoints.addAll(classifyCriticalPoints(f, criticalPoints));
            allPoints.addAll(inflectionPoints);

            if (allPoints.isEmpty()) {
                extremaInfoLabel.setText("No critical points found");
                JOptionPane.showMessageDialog(this, "No critical points found in range.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Critical Points in range [").append(a).append(",").append(b).append("]:\n\n");

                // Separate by type
                Map<String, List<CriticalPoint>> grouped = allPoints.stream()
                        .collect(Collectors.groupingBy(cp -> cp.type));

                grouped.forEach((type, points) -> {
                    sb.append(type).append(":\n");
                    points.forEach(p -> sb.append("  ").append(p).append("\n"));
                });

                extremaInfoLabel.setText("Found " + allPoints.size() + " critical points");
                graphPanel.displayCriticalPointsResults(sb.toString());

                // Highlight all points on graph
                List<Point2D.Double> pointsToHighlight = allPoints.stream()
                        .map(cp -> cp.point)
                        .collect(Collectors.toList());
                List<String> pointTypes = new ArrayList<>();
                for (int i = 0; i < pointsToHighlight.size(); i++) {
                    pointTypes.add("INTERSECTION"); // or "MAX"/"MIN" depending on context
                }
                graphPanel.highlightPoints(pointsToHighlight, pointTypes);
            }
        } catch (

        NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid range values.");
        }
    }

    // private List<Point2D.Double> findCriticalPoints(Function<Double, Double> f,
    // double a, double b) {
    // List<Point2D.Double> points = new ArrayList<>();
    // double step = (b - a) / 1000.0;
    // double h = 1e-5;

    // for (double x = a + step; x < b - step; x += step) {
    // try {
    // double derivative = (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    // if (Math.abs(derivative) < 0.1) { // Threshold for critical point
    // points.add(new Point2D.Double(x, f.apply(x)));
    // }
    // } catch (Exception e) {
    // // Skip points where function is not differentiable
    // }
    // }
    // return points;
    // }

    private void findCriticalPoints() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            // 1. Get search range
            double a = Double.parseDouble(extremaSearchMinField.getText());
            double b = Double.parseDouble(extremaSearchMaxField.getText());
            if (a >= b) {
                JOptionPane.showMessageDialog(this, "Invalid range: min must be less than max");
                return;
            }

            // 2. Get the target function (most recently plotted)
            PlotFunction pf = functions.get(functions.size() - 1);
            Function<Double, Double> f = pf.getFunction();

            // 3. Find all critical points (where f'≈0)
            List<Point2D.Double> criticalPoints = findCriticalPoints(f, a, b);

            // 4. Find inflection points (where f''≈0)
            List<CriticalPoint> inflectionPoints = findInflectionPoints(f, a, b);

            // 5. Classify all found points
            List<CriticalPoint> allPoints = new ArrayList<>();
            allPoints.addAll(classifyCriticalPoints(f, criticalPoints));
            allPoints.addAll(inflectionPoints);

            // 6. Prepare visualization data
            List<Point2D.Double> pointsToHighlight = allPoints.stream()
                    .map(cp -> cp.point)
                    .collect(Collectors.toList());

            List<String> pointTypes = allPoints.stream()
                    .map(cp -> cp.type)
                    .collect(Collectors.toList());

            System.out.println("Point types being sent: " + pointTypes);

            graphPanel.highlightPoints(pointsToHighlight, pointTypes);

            // 7. Display results
            if (allPoints.isEmpty()) {
                extremaInfoLabel.setText("No critical points found");
                JOptionPane.showMessageDialog(this, "No critical points found in range.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Critical Points in range [")
                        .append(String.format("%.2f", a))
                        .append(",")
                        .append(String.format("%.2f", b))
                        .append("]:\n\n");

                // Group by type (MAX/MIN/INFLECTION)
                Map<String, List<CriticalPoint>> grouped = allPoints.stream()
                        .collect(Collectors.groupingBy(cp -> cp.type));

                // Format output
                grouped.forEach((type, points) -> {
                    sb.append(type).append(" (")
                            .append(points.size()).append("):\n");
                    points.forEach(p -> sb.append("  ").append(p).append("\n"));
                });

                extremaInfoLabel.setText("Found " + allPoints.size() + " critical points");
                // graphPanel.displayCriticalPointsResults(sb.toString());
                if (!allPoints.isEmpty()) {
                    graphPanel.displayCriticalPointsResults(sb.toString());
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid range values.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // Helper method to find raw critical points
    private List<Point2D.Double> findCriticalPoints(Function<Double, Double> f, double a, double b) {
        List<Point2D.Double> points = new ArrayList<>();
        double step = (b - a) / 500.0;
        double h = 1e-5;
        double prevDerivative = (f.apply(a + h) - f.apply(a - h)) / (2 * h);

        for (double x = a + step; x < b - step; x += step) {
            try {
                double derivative = (f.apply(x + h) - f.apply(x - h)) / (2 * h);

                // Detect sign changes in derivative
                if (prevDerivative * derivative <= 0 && Math.abs(derivative) < 0.1) {
                    double refinedX = refineCriticalPoint(f, x - step, x + step);
                    points.add(new Point2D.Double(refinedX, f.apply(refinedX)));
                }
                prevDerivative = derivative;
            } catch (Exception e) {
                // Skip discontinuities
            }
        }
        return points;
    }

    private double refineCriticalPoint(Function<Double, Double> f, double a, double b) {
        // Use bisection to find precise critical point
        double tol = 1e-6;
        double h = 1e-5;
        while (b - a > tol) {
            double c = (a + b) / 2;
            double derivC = (f.apply(c + h) - f.apply(c - h)) / (2 * h);
            double derivA = (f.apply(a + h) - f.apply(a - h)) / (2 * h);

            if (derivA * derivC <= 0) {
                b = c;
            } else {
                a = c;
            }
        }
        return (a + b) / 2;
    }

    private List<CriticalPoint> classifyCriticalPoints(Function<Double, Double> f,
            List<Point2D.Double> criticalPoints) {
        List<CriticalPoint> classified = new ArrayList<>();
        double h = 1e-5;

        for (Point2D.Double p : criticalPoints) {
            try {
                // double secondDeriv = calculateSecondDerivative(f, p.x, h);
                double secondDeriv = (f.apply(p.x + h) - 2 * f.apply(p.x) + f.apply(p.x - h)) / (h * h);
                String type;

                if (secondDeriv > 1e-3) { // Threshold for numerical stability
                    type = "MIN";
                } else if (secondDeriv < -1e-3) {
                    type = "MAX";
                } else {
                    // Check third derivative to confirm inflection
                    double thirdDeriv = (f.apply(p.x + h) - 3 * f.apply(p.x) +
                            3 * f.apply(p.x - h) - f.apply(p.x - 2 * h)) / (h * h * h);
                    type = Math.abs(thirdDeriv) > 1e-3 ? "INFLECTION" : "FLAT";
                }
                classified.add(new CriticalPoint(p, type));
            } catch (Exception e) {
                // Skip undifferentiable points
            }
        }
        return classified;
    }

    private void calculateDerivative() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            double x = Double.parseDouble(diffPointField.getText());
            PlotFunction pf = functions.get(functions.size() - 1); // Use the last plotted function
            Function<Double, Double> f = pf.getFunction();

            // Check if function is defined at the point
            try {
                double test = f.apply(x);
                if (!Double.isFinite(test)) {
                    throw new ArithmeticException("Function undefined at this point");
                }
            } catch (Exception e) {
                derivativeValueLabel.setText("Derivative: Undefined at x = " + x);
                JOptionPane.showMessageDialog(this,
                        "Function is not defined at x = " + x,
                        "Differentiation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double h = 1e-5;
            // Central difference method for more accuracy
            double derivative = (f.apply(x + h) - f.apply(x - h)) / (2 * h);

            // Check if result is valid
            if (!Double.isFinite(derivative)) {
                derivativeValueLabel.setText("Derivative: Undefined at x = " + x);
                JOptionPane.showMessageDialog(this,
                        "Derivative does not exist at x = " + x,
                        "Differentiation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            derivativeValueLabel.setText(String.format("Derivative: f'(%.2f) = %.5f", x, derivative));
            JOptionPane.showMessageDialog(this,
                    String.format("f'(%.4f) ≈ %.6f", x, derivative),
                    "Differentiation Result", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for x.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating derivative: " + e.getMessage());
        }
    }

    private List<CriticalPoint> findInflectionPoints(Function<Double, Double> f, double a, double b) {
        List<CriticalPoint> inflections = new ArrayList<>();
        double step = (b - a) / 500.0;
        double h = 1e-5;
        double prevSecondDeriv = calculateSecondDerivative(f, a, h);

        for (double x = a + step; x < b - step; x += step) {
            double secondDeriv = calculateSecondDerivative(f, x, h);

            // Detect sign change in second derivative
            if (prevSecondDeriv * secondDeriv <= 0 &&
                    Math.abs(secondDeriv) < 10.0) { // Threshold for numerical stability
                double refinedX = refineInflectionPoint(f, x - step, x + step);
                inflections.add(new CriticalPoint(
                        new Point2D.Double(refinedX, f.apply(refinedX)),
                        "INFLECTION"));
            }
            prevSecondDeriv = secondDeriv;
        }
        return inflections;
    }

    private double calculateSecondDerivative(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
    }

    private double refineInflectionPoint(Function<Double, Double> f, double a, double b) {
        double tol = 1e-6;
        double h = 1e-5;
        while (b - a > tol) {
            double c = (a + b) / 2;
            double secondDerivC = calculateSecondDerivative(f, c, h);
            double secondDerivA = calculateSecondDerivative(f, a, h);

            if (secondDerivA * secondDerivC <= 0) {
                b = c;
            } else {
                a = c;
            }
        }
        return (a + b) / 2;
    }

=======
>>>>>>> b4646251617d6a5080d0cffff3c78e4299ebc9f3
    private void plotFunction() {
        String type = (String) functionTypeBox.getSelectedItem();
        double param;
        try {
            param = Double.parseDouble(parametersField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid parameter value.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Invalid axis limits.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            graphPanel.setLimits(xMin, xMax, yMin, yMax);
            graphPanel.repaint();
            statusLabel.setText("Limits updated");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Need at least 2 functions.", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<IntersectionSolver.IntersectionPoint> all = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            for (int j = i + 1; j < functions.size(); j++) {
                all.addAll(IntersectionSolver.findAllIntersections(functions.get(i), functions.get(j), -10, 10));
            }
        }

        if (all.isEmpty()) {
            statusLabel.setText("No intersections found");
        } else {
            statusLabel.setText("Found " + all.size() + " intersection(s)");
            showIntersections(all);
        }
    }

    private void showIntersections(List<IntersectionSolver.IntersectionPoint> intersections) {
        StringBuilder sb = new StringBuilder("Intersection Points:\n\n");
        for (var p : intersections) {
            sb.append(String.format("Between %s and %s:\n  Point: (%.3f, %.3f)\n\n",
                p.function1Name, p.function2Name, p.point.getX(), p.point.getY()));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Intersections", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveGraphStateToFile() {
        List<SavedGraphState.SerializableFunction> serialized = new ArrayList<>();
        for (PlotFunction pf : functions) {
            try {
                String label = pf.getLabel();
                String type = label.split(" ")[0];
                double param = Double.parseDouble(label.replaceAll(".*\\(param: ", "").replaceAll("\\)", ""));
                serialized.add(new SavedGraphState.SerializableFunction(type, param));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to serialize function: " + e.getMessage());
            }
        }
        SavedGraphState state = new SavedGraphState(serialized, bgColor);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream("saved_graph.ser"))) {
            out.writeObject(state);
            JOptionPane.showMessageDialog(this, "Graph saved.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }

    private void loadGraphStateFromFile() {
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream("saved_graph.ser"))) {
            SavedGraphState state = (SavedGraphState) in.readObject();
            functions.clear();
            for (SavedGraphState.SerializableFunction sf : state.functions) {
                functions.add(new PlotFunction(sf.label, sf.toFunction()));
            }
            this.bgColor = state.background;
            graphPanel.setBackground(bgColor);
            graphPanel.setFunctions(functions);
            graphPanel.repaint();
            updateStatus();
            JOptionPane.showMessageDialog(this, "Graph loaded.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading: " + e.getMessage());
        }
    }

    private void openEquationMaker() {
        EquationMaker maker = new EquationMaker(this);
        maker.setVisible(true);
        String eq = maker.getEquation();
        if (eq != null && !eq.trim().isEmpty()) {
            try {
                Function<Double, Double> func = EquationParser.parse(eq);
                functions.add(new PlotFunction(eq, func));
                graphPanel.setFunctions(functions);
                graphPanel.repaint();
                updateStatus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid equation format.");
            }
        }
    }

    // --- Getter methods for external access (for AreaCalculator, etc.) ---
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
    public List<PlotFunction> getFunctions() {
        return functions;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesmosCloneApp().setVisible(true));
    }

    
private void exportGraphToSVG() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Graph as SVG");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();

        // Ensure .svg extension
        if (!fileToSave.getName().toLowerCase().endsWith(".svg")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".svg");
        }

        System.out.println("Exporting SVG to: " + fileToSave.getAbsolutePath());

        graphPanel.exportToSVG(fileToSave);
    }
}
}
