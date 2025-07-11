package src.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Function;
import src.util.PlotFunction;
import src.ui.DesmosCloneApp;

public class AreaCalculator extends JPanel {
    private JTextField lowerLimitField;
    private JTextField upperLimitField;
    private JButton calculateAreaButton;
    private JButton resetLimitsButton;
    private JButton clearHighlightButton;

    private final DesmosCloneApp app;

    public AreaCalculator(DesmosCloneApp app) {
        this.app = app;

        setBorder(BorderFactory.createTitledBorder("Area Under Curve"));
        setLayout(new GridLayout(2, 1));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lowerLimitField = new JTextField("0", 6);
        upperLimitField = new JTextField("1", 6);
        inputPanel.add(new JLabel("Lower Limit (a):"));
        inputPanel.add(lowerLimitField);
        inputPanel.add(new JLabel("Upper Limit (b):"));
        inputPanel.add(upperLimitField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        calculateAreaButton = new JButton("Calculate Area");
        resetLimitsButton = new JButton("Reset Limits");
        clearHighlightButton = new JButton("Clear Highlight");

        buttonPanel.add(calculateAreaButton);
        buttonPanel.add(resetLimitsButton);
        buttonPanel.add(clearHighlightButton);

        add(inputPanel);
        add(buttonPanel);

        calculateAreaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndDisplayArea();
            }
        });

        resetLimitsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lowerLimitField.setText("0");
                upperLimitField.setText("1");
            }
        });

        clearHighlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.getGraphPanel().clearHighlight();
            }
        });
    }

    public static double computeDefiniteIntegral(Function<Double, Double> func, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            double x0 = a + i * h;
            double x1 = x0 + h;
            double y0 = func.apply(x0);
            double y1 = func.apply(x1);
            if (Double.isFinite(y0) && Double.isFinite(y1)) {
                sum += (y0 + y1) / 2 * h;
            }
        }

        return sum;
    }

    private void calculateAndDisplayArea() {
        List<PlotFunction> functions = app.getFunctions();
        if (functions == null || functions.isEmpty()) {
            JOptionPane.showMessageDialog(app, "No function to integrate.");
            return;
        }

        PlotFunction lastFunction = functions.get(functions.size() - 1);
        Function<Double, Double> func = lastFunction.getFunction();

        double a, b;
        try {
            a = Double.parseDouble(lowerLimitField.getText());
            b = Double.parseDouble(upperLimitField.getText());
            if (a >= b) {
                JOptionPane.showMessageDialog(app, "Lower limit must be less than upper limit.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(app, "Invalid limits.");
            return;
        }

        double h = (b - a) / 1000.0;
        double area = 0.0;
        for (int i = 0; i < 1000; i++) {
            double x1 = a + i * h;
            double x2 = x1 + h;
            double y1 = func.apply(x1);
            double y2 = func.apply(x2);
            if (Double.isFinite(y1) && Double.isFinite(y2)) {
                area += 0.5 * (y1 + y2) * h;
            }
        }

        app.getGraphPanel().highlightAreaUnder(func, a, b);

        JOptionPane.showMessageDialog(app,
                String.format("Definite integral from %.3f to %.3f is approximately %.6f", a, b, area),
                "Area Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
