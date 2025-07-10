package src.ui;

import src.util.PlotFunction;
import src.util.IntersectionSolver;
import src.util.SavedGraphState;
import src.util.EquationMaker;
import src.util.AreaCalculator;
import src.util.EquationParser;
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
    private JButton plotButton;
    private JButton clearButton;
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
        plotButton.setFont(new Font("Arial", Font.PLAIN, 12));

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
        areaPanel.setBorder(BorderFactory.createTitledBorder("Definite Integral"));

        areaPanel.add(new JLabel("Lower Limit:"));
        areaPanel.add(lowerLimitField);
        areaPanel.add(new JLabel("Upper Limit:"));
        areaPanel.add(upperLimitField);
        areaPanel.add(calculateAreaButton);

        topPanel.add(functionPanel, BorderLayout.NORTH);
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
        add(southPanel, BorderLayout.SOUTH);
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

        setLimitsButton.addActionListener(e -> setAxisLimits());
        resetLimitsButton.addActionListener(e -> resetLimits());

        parametersField.addActionListener(e -> plotFunction());
        xMinField.addActionListener(e -> setAxisLimits());
        xMaxField.addActionListener(e -> setAxisLimits());
        yMinField.addActionListener(e -> setAxisLimits());
        yMaxField.addActionListener(e -> setAxisLimits());

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
}
