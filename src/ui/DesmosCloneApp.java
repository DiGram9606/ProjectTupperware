package src.ui;

import src.util.PlotFunction;
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
    private List<PlotFunction> functions = new ArrayList<>();
    private Color bgColor = Color.BLACK;
    
    private JTextField xMinField, xMaxField, yMinField, yMaxField;
    private JButton setLimitsButton;
    private JButton resetLimitsButton;
    
    private JLabel statusLabel;
    private JLabel functionCountLabel;

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
        
        topPanel.add(functionPanel, BorderLayout.NORTH);
        topPanel.add(limitsPanel, BorderLayout.CENTER);
        
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
        setLimitsButton.addActionListener(e -> setAxisLimits());
        resetLimitsButton.addActionListener(e -> resetLimits());
        
        parametersField.addActionListener(e -> plotFunction());
        xMinField.addActionListener(e -> setAxisLimits());
        xMaxField.addActionListener(e -> setAxisLimits());
        yMinField.addActionListener(e -> setAxisLimits());
        yMaxField.addActionListener(e -> setAxisLimits());
    }

    private void plotFunction() {
        String type = (String) functionTypeBox.getSelectedItem();
        double param;
        
        try {
            param = Double.parseDouble(parametersField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid parameter value. Please enter a valid number.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Function<Double, Double> func;
        switch (type) {
            case "sin(x)":
                func = x -> param * Math.sin(x);
                break;
            case "cos(x)":
                func = x -> param * Math.cos(x);
                break;
            case "tan(x)":
                func = x -> param * Math.tan(x);
                break;
            case "x^2":
                func = x -> param * x * x;
                break;
            case "x^3":
                func = x -> param * x * x * x;
                break;
            case "2x+3":
                func = x -> param * (2 * x + 3);
                break;
            case "log(x)":
                func = x -> x <= 0 ? Double.NaN : param * Math.log(x);
                break;
            case "exp(x)":
                func = x -> param * Math.exp(x);
                break;
            case "step(x)":
                func = x -> x >= 0 ? param : 0;
                break;
            default:
                return;
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

            if (xMin >= xMax) {
                JOptionPane.showMessageDialog(this, 
                    "X Min must be less than X Max", 
                    "Invalid Range", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (yMin >= yMax) {
                JOptionPane.showMessageDialog(this, 
                    "Y Min must be less than Y Max", 
                    "Invalid Range", JOptionPane.WARNING_MESSAGE);
                return;
            }

            graphPanel.setLimits(xMin, xMax, yMin, yMax);
            graphPanel.repaint();
            statusLabel.setText("Limits updated successfully");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for all limit fields.", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetLimits() {
        xMinField.setText("-10");
        xMaxField.setText("10");
        yMinField.setText("-10");
        yMaxField.setText("10");
        setAxisLimits();
        statusLabel.setText("Limits reset to default values");
    }

    private void updateStatus() {
        functionCountLabel.setText("Functions: " + functions.size());
        if (functions.isEmpty()) {
            statusLabel.setText("Ready to plot functions");
        } else {
            statusLabel.setText("Displaying " + functions.size() + " function(s)");
        }
    }
}
