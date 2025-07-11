package src.ui;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import src.ui.GraphPanel;
import src.util.PlotFunction;
import src.util.IntersectionSolver;
import src.util.SavedGraphState;
import src.util.EquationMaker;
import src.util.AreaCalculator;
import src.util.EquationParser;
import src.util.ExtremaFinder;
import src.util.Derivative;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DesmosCloneApp extends JFrame {
    private GraphPanel graphPanel;
    private JTextField expressionField;
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
    private JTextField lowerLimitField, upperLimitField;
    private JButton calculateAreaButton;
    private JButton equationMakerButton;
    
    // Extrema fields
    private JTextField extremaXMinField, extremaXMaxField;
    private JButton findExtremaButton;
    private JButton clearExtremaButton;
    
    // Derivative fields
    private JButton calculateDerivativeButton;
    private JButton clearDerivativeButton;
    private JButton showNonDiffPointsButton;
    private JTextField derivativeXField;
    private JButton evaluateDerivativeButton;
    private JLabel derivativeValueLabel;

    public DesmosCloneApp() {
        setTitle("Function Plotter - Advanced Calculus Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        initializeComponents();
        layoutComponents();
        setupEventListeners();
        updateStatus();
    }

    private void initializeComponents() {
        expressionField = new JTextField("sin(x)", 15);
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

        lowerLimitField = new JTextField("0", 6);
        upperLimitField = new JTextField("5", 6);
        calculateAreaButton = new JButton("Calculate Area");
        equationMakerButton = new JButton("Equation Maker");
        equationMakerButton.setFont(new Font("Arial", Font.PLAIN, 12));

        // Initialize extrema components
        extremaXMinField = new JTextField("-5", 6);
        extremaXMaxField = new JTextField("5", 6);
        findExtremaButton = new JButton("Find Extrema");
        clearExtremaButton = new JButton("Clear Extrema");

        // Initialize derivative components
        calculateDerivativeButton = new JButton("Calculate Derivative");
        clearDerivativeButton = new JButton("Clear Derivative");
        showNonDiffPointsButton = new JButton("Show Non-Diff Points");
        derivativeXField = new JTextField("0", 6);
        evaluateDerivativeButton = new JButton("Evaluate f'(x)");
        derivativeValueLabel = new JLabel("f'(x): N/A");
        derivativeValueLabel.setFont(new Font("Arial", Font.PLAIN, 11));

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
        functionPanel.add(new JLabel("f(x):"));
        functionPanel.add(expressionField);
        functionPanel.add(plotButton);
        functionPanel.add(clearButton);
        functionPanel.add(bgColorButton);
        functionPanel.add(intersectionButton);
        functionPanel.add(saveButton);
        functionPanel.add(loadButton);
        functionPanel.add(exportSVGButton);

        JScrollPane functionScroll = new JScrollPane(functionPanel, 
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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

        JPanel extremaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        extremaPanel.setBorder(BorderFactory.createTitledBorder("Extrema Analysis"));
        extremaPanel.add(new JLabel("X Min:"));
        extremaPanel.add(extremaXMinField);
        extremaPanel.add(new JLabel("X Max:"));
        extremaPanel.add(extremaXMaxField);
        extremaPanel.add(findExtremaButton);
        extremaPanel.add(clearExtremaButton);

        // Add derivative panel
        JPanel derivativePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        derivativePanel.setBorder(BorderFactory.createTitledBorder("Derivative Analysis"));
        derivativePanel.add(calculateDerivativeButton);
        derivativePanel.add(clearDerivativeButton);
        derivativePanel.add(showNonDiffPointsButton);
        derivativePanel.add(new JLabel("x ="));
        derivativePanel.add(derivativeXField);
        derivativePanel.add(evaluateDerivativeButton);

        topPanel.add(functionScroll, BorderLayout.NORTH);
        
        JPanel middlePanel = new JPanel(new GridLayout(4, 1, 5, 5));
        middlePanel.add(limitsPanel);
        middlePanel.add(areaPanel);
        middlePanel.add(extremaPanel);
        middlePanel.add(derivativePanel);
        
        topPanel.add(middlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeft.add(statusLabel);
        bottomLeft.add(integralValueLabel);
        bottomLeft.add(derivativeValueLabel);
        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(functionCountLabel, BorderLayout.EAST);

        JPanel eqMakerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        eqMakerPanel.add(equationMakerButton);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(eqMakerPanel);
        south.add(bottomPanel);

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        plotButton.addActionListener(e -> plotFunction());
        expressionField.addActionListener(e -> plotFunction());
        clearButton.addActionListener(e -> {
            functions.clear();
            graphPanel.clearHighlight();
            graphPanel.highlightExtrema(new ArrayList<>());
            graphPanel.clearDerivative();
            graphPanel.repaint();
            updateStatus();
            integralValueLabel.setText("Integral: N/A");
            derivativeValueLabel.setText("f'(x): N/A");
        });
        bgColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", bgColor);
            if (newColor != null) {
                bgColor = newColor;
                graphPanel.setBackground(bgColor);
            }
        });
        intersectionButton.addActionListener(e -> findIntersections());
        saveButton.addActionListener(e -> saveGraphStateToFile());
        loadButton.addActionListener(e -> loadGraphStateFromFile());
        exportSVGButton.addActionListener(e -> exportGraphToSVG());
        setLimitsButton.addActionListener(e -> setAxisLimits());
        resetLimitsButton.addActionListener(e -> resetLimits());
        calculateAreaButton.addActionListener(e -> calculateDefiniteIntegral());
        equationMakerButton.addActionListener(e -> openEquationMaker());
        
        // Add extrema event listeners
        findExtremaButton.addActionListener(e -> findAndDisplayExtrema());
        clearExtremaButton.addActionListener(e -> clearExtrema());
        
        // Add derivative event listeners
        calculateDerivativeButton.addActionListener(e -> calculateAndDisplayDerivative());
        clearDerivativeButton.addActionListener(e -> clearDerivative());
        showNonDiffPointsButton.addActionListener(e -> showNonDifferentiablePoints());
        evaluateDerivativeButton.addActionListener(e -> evaluateDerivativeAtPoint());
    }

    private void plotFunction() {
        String expr = expressionField.getText().trim();
        if (expr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an expression.");
            return;
        }

        try {
            Function<Double, Double> f = EquationParser.parse(expr);
            functions.add(new PlotFunction(expr, f));
            graphPanel.setFunctions(functions);
            graphPanel.repaint();
            updateStatus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid expression: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateDefiniteIntegral() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function plotted.");
            return;
        }

        try {
            double a = Double.parseDouble(lowerLimitField.getText());
            double b = Double.parseDouble(upperLimitField.getText());
            if (a >= b) {
                JOptionPane.showMessageDialog(this, "Lower limit must be less than upper limit.");
                return;
            }

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

    private void setAxisLimits() {
        try {
            double xmin = Double.parseDouble(xMinField.getText());
            double xmax = Double.parseDouble(xMaxField.getText());
            double ymin = Double.parseDouble(yMinField.getText());
            double ymax = Double.parseDouble(yMaxField.getText());

            if (xmin >= xmax || ymin >= ymax) {
                JOptionPane.showMessageDialog(this, "Invalid axis limits.", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            graphPanel.setLimits(xmin, xmax, ymin, ymax);
            graphPanel.repaint();
            statusLabel.setText("Limits updated");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
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

    private void findIntersections() {
        if (functions.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 functions.", 
                "Error", JOptionPane.INFORMATION_MESSAGE);
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
            sb.append(String.format("Between %s and %s:\n Point: (%.3f, %.3f)\n\n", 
                p.function1Name, p.function2Name, p.point.getX(), p.point.getY()));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Intersections", JOptionPane.INFORMATION_MESSAGE);
    }

    // Extrema methods
    private void findAndDisplayExtrema() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No functions to analyze.");
            return;
        }
        
        try {
            double xMin = Double.parseDouble(extremaXMinField.getText());
            double xMax = Double.parseDouble(extremaXMaxField.getText());
            
            if (xMin >= xMax) {
                JOptionPane.showMessageDialog(this, "X Min must be less than X Max.");
                return;
            }
            
            List<ExtremaFinder.ExtremaResult> results = 
                ExtremaFinder.findExtremaForAllFunctions(functions, xMin, xMax, 0.01);
            
            List<ExtremaFinder.ExtremaPoint> allExtrema = new ArrayList<>();
            StringBuilder summaryText = new StringBuilder("Extrema Analysis Results:\n\n");
            
            for (ExtremaFinder.ExtremaResult result : results) {
                allExtrema.addAll(result.allExtrema);
                summaryText.append(result.summary).append("\n");
            }
            
            graphPanel.highlightExtrema(allExtrema);
            
            JOptionPane.showMessageDialog(this, summaryText.toString(), 
                "Extrema Analysis", JOptionPane.INFORMATION_MESSAGE);
            
            statusLabel.setText("Found " + allExtrema.size() + " extrema point(s)");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid interval limits.");
        }
    }

    private void clearExtrema() {
        graphPanel.highlightExtrema(new ArrayList<>());
        statusLabel.setText("Extrema cleared");
    }

    // Derivative methods
    private void calculateAndDisplayDerivative() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function to differentiate.");
            return;
        }

        try {
            PlotFunction originalFunction = functions.get(functions.size() - 1);
            double xMin = Double.parseDouble(xMinField.getText());
            double xMax = Double.parseDouble(xMaxField.getText());
            
            Derivative.DerivativeResult result = Derivative.createDerivativeFunction(originalFunction, xMin, xMax);
            
            // Create derivative plot function
            PlotFunction derivativeFunction = new PlotFunction(
                "d/dx[" + originalFunction.getLabel() + "]", 
                result.derivativeFunction
            );
            
            // Add derivative to functions list
            functions.add(derivativeFunction);
            graphPanel.setFunctions(functions);
            graphPanel.setDerivativeResult(result);
            graphPanel.repaint();
            
            // Show analysis
            JOptionPane.showMessageDialog(this, result.summary, 
                "Derivative Analysis", JOptionPane.INFORMATION_MESSAGE);
            
            statusLabel.setText("Derivative calculated and plotted");
            updateStatus();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error calculating derivative: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDerivative() {
        graphPanel.clearDerivative();
        statusLabel.setText("Derivative cleared");
        derivativeValueLabel.setText("f'(x): N/A");
    }

    private void showNonDifferentiablePoints() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function to analyze.");
            return;
        }

        try {
            PlotFunction originalFunction = functions.get(functions.size() - 1);
            double xMin = Double.parseDouble(xMinField.getText());
            double xMax = Double.parseDouble(xMaxField.getText());
            
            Derivative.DerivativeResult result = Derivative.createDerivativeFunction(originalFunction, xMin, xMax);
            
            if (result.nonDifferentiablePoints.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No non-differentiable points found in the current domain.",
                    "Differentiability Analysis", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder("Non-differentiable points found:\n\n");
                for (var point : result.nonDifferentiablePoints) {
                    sb.append(String.format("x = %.4f (function may have sharp corner or cusp)\n", point.getX()));
                }
                JOptionPane.showMessageDialog(this, sb.toString(), 
                    "Non-differentiable Points", JOptionPane.WARNING_MESSAGE);
                
                graphPanel.highlightNonDifferentiablePoints(result.nonDifferentiablePoints);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error in differentiability analysis: " + ex.getMessage());
        }
    }

    private void evaluateDerivativeAtPoint() {
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No function to differentiate.");
            return;
        }

        try {
            double x = Double.parseDouble(derivativeXField.getText());
            PlotFunction originalFunction = functions.get(functions.size() - 1);
            
            double derivativeValue = Derivative.numericalDerivative(originalFunction.getFunction(), x);
            
            if (Double.isFinite(derivativeValue)) {
                derivativeValueLabel.setText(String.format("f'(%.3f) = %.6f", x, derivativeValue));
                
                // Check differentiability at this point
                Derivative.LimitResult limitCheck = Derivative.checkDifferentiability(originalFunction.getFunction(), x);
                
                String message = String.format("f'(%.3f) = %.6f\n\n", x, derivativeValue);
                message += "Differentiability check:\n";
                message += limitCheck.reason + "\n";
                if (limitCheck.limitExists) {
                    message += String.format("Left limit: %.6f\n", limitCheck.leftLimit);
                    message += String.format("Right limit: %.6f\n", limitCheck.rightLimit);
                }
                
                JOptionPane.showMessageDialog(this, message, 
                    "Derivative Evaluation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                derivativeValueLabel.setText("f'(x): undefined");
                JOptionPane.showMessageDialog(this, 
                    "Derivative is undefined at x = " + x,
                    "Derivative Evaluation", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid x value.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error evaluating derivative: " + ex.getMessage());
        }
    }

    private void saveGraphStateToFile() {
        List<SavedGraphState.SerializableFunction> serialized = new ArrayList<>();
        for (PlotFunction pf : functions) {
            serialized.add(new SavedGraphState.SerializableFunction(pf.getLabel()));
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Graph State");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(new SavedGraphState(serialized, bgColor));
                JOptionPane.showMessageDialog(this, "Graph saved to " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Save error: " + e.getMessage());
            }
        }
    }

    private void loadGraphStateFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Graph State");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                SavedGraphState state = (SavedGraphState) in.readObject();
                functions.clear();
                for (SavedGraphState.SerializableFunction sf : state.functions) {
                    try {
                        Function<Double, Double> func = EquationParser.parse(sf.expression);
                        functions.add(new PlotFunction(sf.expression, func));
                    } catch (Exception e) {
                        System.err.println("Could not parse expression: " + sf.expression);
                    }
                }
                bgColor = state.background;
                graphPanel.setBackground(bgColor);
                graphPanel.setFunctions(functions);
                graphPanel.repaint();
                updateStatus();
                JOptionPane.showMessageDialog(this, "Loaded from " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
            }
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

    private void exportGraphToSVG() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Graph as SVG");
        int sel = chooser.showSaveDialog(this);
        if (sel == JFileChooser.APPROVE_OPTION) {
            File out = chooser.getSelectedFile();
            if (!out.getName().toLowerCase().endsWith(".svg")) {
                out = new File(out.getParentFile(), out.getName() + ".svg");
            }
            graphPanel.exportToSVG(out);
        }
    }

    private void updateStatus() {
        functionCountLabel.setText("Functions: " + functions.size());
        statusLabel.setText(functions.isEmpty() ? "Ready to plot functions" : 
            "Displaying " + functions.size() + " function(s)");
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public List<PlotFunction> getFunctions() {
        return functions;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesmosCloneApp().setVisible(true));
    }
}
