package src.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class EquationMaker extends JDialog {
    private JTextField equationField;
    private JButton sinButton, cosButton, tanButton, cotButton, secButton, cscButton;
    private JButton powButton, plusButton, minusButton, mulButton, divButton, leftParenButton, rightParenButton, xButton, clearButton, deleteButton;
    private JButton addPolyButton;
    private List<JTextField> polyCoeffFields = new ArrayList<>();
    private JTextField polyDegreeField;
    private JButton doneButton, cancelButton;
    private String resultEquation = null;

    public EquationMaker(JFrame parent) {
        super(parent, "Equation Maker", true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        equationField = new JTextField();
        equationField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        equationField.setEditable(false);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 6, 5, 5));
        sinButton = new JButton("sin");
        cosButton = new JButton("cos");
        tanButton = new JButton("tan");
        cotButton = new JButton("cot");
        secButton = new JButton("sec");
        cscButton = new JButton("csc");
        powButton = new JButton("^");
        plusButton = new JButton("+");
        minusButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");
        leftParenButton = new JButton("(");
        rightParenButton = new JButton(")");
        xButton = new JButton("x");
        clearButton = new JButton("Clear");
        deleteButton = new JButton("Del");
        addPolyButton = new JButton("Poly");

        buttonPanel.add(sinButton);
        buttonPanel.add(cosButton);
        buttonPanel.add(tanButton);
        buttonPanel.add(cotButton);
        buttonPanel.add(secButton);
        buttonPanel.add(cscButton);
        buttonPanel.add(powButton);
        buttonPanel.add(plusButton);
        buttonPanel.add(minusButton);
        buttonPanel.add(mulButton);
        buttonPanel.add(divButton);
        buttonPanel.add(leftParenButton);
        buttonPanel.add(rightParenButton);
        buttonPanel.add(xButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addPolyButton);

        JPanel polyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        polyPanel.add(new JLabel("Polynomial Degree:"));
        polyDegreeField = new JTextField("2", 3);
        polyPanel.add(polyDegreeField);

        doneButton = new JButton("Done");
        cancelButton = new JButton("Cancel");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(doneButton);
        bottomPanel.add(cancelButton);

        add(equationField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(polyPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        ActionListener insertListener = e -> {
            String text = ((JButton) e.getSource()).getText();
            equationField.setText(equationField.getText() + text);
        };
        sinButton.addActionListener(e -> equationField.setText(equationField.getText() + "sin("));
        cosButton.addActionListener(e -> equationField.setText(equationField.getText() + "cos("));
        tanButton.addActionListener(e -> equationField.setText(equationField.getText() + "tan("));
        cotButton.addActionListener(e -> equationField.setText(equationField.getText() + "cot("));
        secButton.addActionListener(e -> equationField.setText(equationField.getText() + "sec("));
        cscButton.addActionListener(e -> equationField.setText(equationField.getText() + "csc("));
        powButton.addActionListener(insertListener);
        plusButton.addActionListener(insertListener);
        minusButton.addActionListener(insertListener);
        mulButton.addActionListener(insertListener);
        divButton.addActionListener(insertListener);
        leftParenButton.addActionListener(insertListener);
        rightParenButton.addActionListener(insertListener);
        xButton.addActionListener(insertListener);

        clearButton.addActionListener(e -> equationField.setText(""));
        deleteButton.addActionListener(e -> {
            String text = equationField.getText();
            if (!text.isEmpty()) {
                equationField.setText(text.substring(0, text.length() - 1));
            }
        });

        addPolyButton.addActionListener(e -> showPolyInputDialog());

        doneButton.addActionListener(e -> {
            resultEquation = equationField.getText();
            dispose();
        });
        cancelButton.addActionListener(e -> {
            resultEquation = null;
            dispose();
        });
    }

    private void showPolyInputDialog() {
        try {
            int degree = Integer.parseInt(polyDegreeField.getText());
            if (degree < 1 || degree > 10) {
                JOptionPane.showMessageDialog(this, "Degree must be between 1 and 10.");
                return;
            }
            JPanel coeffPanel = new JPanel(new GridLayout(degree + 1, 2));
            polyCoeffFields.clear();
            for (int i = degree; i >= 0; i--) {
                coeffPanel.add(new JLabel("Coeff x^" + i + ":"));
                JTextField coeffField = new JTextField("1", 5);
                polyCoeffFields.add(coeffField);
                coeffPanel.add(coeffField);
            }
            int res = JOptionPane.showConfirmDialog(this, coeffPanel, "Enter Polynomial Coefficients", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                StringBuilder eq = new StringBuilder();
                for (int i = degree; i >= 0; i--) {
                    String coeff = polyCoeffFields.get(degree - i).getText().trim();
                    if (!coeff.equals("0") && !coeff.isEmpty()) {
                        if (eq.length() > 0 && !coeff.startsWith("-")) eq.append("+");
                        eq.append(coeff);
                        if (i > 0) eq.append("*x");
                        if (i > 1) eq.append("^").append(i);
                    }
                }
                equationField.setText(equationField.getText() + eq.toString());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid degree.");
        }
    }

    public String getEquation() {
        return resultEquation;
    }
}
