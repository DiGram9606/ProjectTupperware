package src.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class EquationMaker extends JDialog {
    private JTextField equationField;
    private JButton sinButton, cosButton, tanButton, cotButton, secButton, cscButton;
    private JButton powButton, plusButton, minusButton, mulButton, divButton, leftParenButton, rightParenButton, xButton, clearButton, deleteButton;
    private JButton addPolyButton, piButton, eButton, logButton, sqrtButton, expButton;
    private List<JTextField> polyCoeffFields = new ArrayList<>();
    private JTextField polyDegreeField;
    private JButton doneButton, cancelButton;
    private String resultEquation = null;
    private JLabel statusLabel;

    public EquationMaker(JFrame parent) {
        super(parent, "Advanced Equation Maker", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));
        
        initializeComponents();
        layoutComponents();
        setupEventListeners();
        setupAdvancedKeyboardSupport();
        
        // Focus the text field when dialog opens
        SwingUtilities.invokeLater(() -> {
            equationField.requestFocusInWindow();
            equationField.setCaretPosition(0);
        });
    }

    private void initializeComponents() {
        // Enhanced equation field with better formatting
        equationField = new JTextField();
        equationField.setFont(new Font("Monospaced", Font.BOLD, 18));
        equationField.setEditable(true);
        equationField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Initialize all buttons with consistent styling
        initializeAllButtons();
        
        // Polynomial degree field
        polyDegreeField = new JTextField("2", 3);
        polyDegreeField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Control buttons
        doneButton = new JButton("Done");
        cancelButton = new JButton("Cancel");
        styleControlButton(doneButton);
        styleControlButton(cancelButton);
        
        // Status label for real-time feedback
        statusLabel = new JLabel("Ready - Type equations or use buttons");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        statusLabel.setForeground(Color.BLUE);
    }

    private void initializeAllButtons() {
        // Function buttons
        sinButton = createStyledButton("sin", Color.BLUE);
        cosButton = createStyledButton("cos", Color.BLUE);
        tanButton = createStyledButton("tan", Color.BLUE);
        cotButton = createStyledButton("cot", Color.BLUE);
        secButton = createStyledButton("sec", Color.BLUE);
        cscButton = createStyledButton("csc", Color.BLUE);
        logButton = createStyledButton("log", Color.BLUE);
        sqrtButton = createStyledButton("√", Color.BLUE);
        expButton = createStyledButton("exp", Color.BLUE);
        
        // Operator buttons
        powButton = createStyledButton("^", Color.RED);
        plusButton = createStyledButton("+", Color.BLACK);
        minusButton = createStyledButton("-", Color.BLACK);
        mulButton = createStyledButton("×", Color.BLACK);
        divButton = createStyledButton("÷", Color.BLACK);
        leftParenButton = createStyledButton("(", Color.DARK_GRAY);
        rightParenButton = createStyledButton(")", Color.DARK_GRAY);
        
        // Variable and constant buttons
        xButton = createStyledButton("x", Color.GREEN);
        piButton = createStyledButton("π", Color.MAGENTA);
        eButton = createStyledButton("e", Color.MAGENTA);
        
        // Control buttons
        clearButton = createStyledButton("Clear", Color.ORANGE);
        deleteButton = createStyledButton("⌫", Color.RED);
        addPolyButton = createStyledButton("Poly", Color.CYAN);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(color);
        button.setPreferredSize(new Dimension(50, 35));
        button.setFocusable(false); // Prevent buttons from stealing focus
        return button;
    }

    private void styleControlButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(80, 35));
    }

    private void layoutComponents() {
        // Top panel with equation field and hints
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        topPanel.add(equationField, BorderLayout.CENTER);
        
        // Enhanced hints panel
        JPanel hintsPanel = new JPanel(new GridLayout(2, 1));
        JLabel hintsLabel1 = new JLabel("Shortcuts: F1(sin) F2(cos) F3(tan) F4(log) F5(√) | Ctrl+P(π) Ctrl+E(e)");
        JLabel hintsLabel2 = new JLabel("Type directly: 2*x^2+3*x-1, sin(x), log(x), sqrt(x) | Use * for multiplication");
        hintsLabel1.setFont(new Font("Arial", Font.PLAIN, 10));
        hintsLabel2.setFont(new Font("Arial", Font.PLAIN, 10));
        hintsLabel1.setForeground(Color.GRAY);
        hintsLabel2.setForeground(Color.GRAY);
        hintsPanel.add(hintsLabel1);
        hintsPanel.add(hintsLabel2);
        
        topPanel.add(hintsPanel, BorderLayout.SOUTH);
        
        // Enhanced button panel with better organization
        JPanel buttonPanel = new JPanel(new GridLayout(5, 6, 3, 3));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Function & Operator Buttons"));
        
        // Row 1: Trigonometric functions
        buttonPanel.add(sinButton);
        buttonPanel.add(cosButton);
        buttonPanel.add(tanButton);
        buttonPanel.add(cotButton);
        buttonPanel.add(secButton);
        buttonPanel.add(cscButton);
        
        // Row 2: Other functions
        buttonPanel.add(logButton);
        buttonPanel.add(sqrtButton);
        buttonPanel.add(expButton);
        buttonPanel.add(piButton);
        buttonPanel.add(eButton);
        buttonPanel.add(xButton);
        
        // Row 3: Basic operators
        buttonPanel.add(plusButton);
        buttonPanel.add(minusButton);
        buttonPanel.add(mulButton);
        buttonPanel.add(divButton);
        buttonPanel.add(powButton);
        buttonPanel.add(leftParenButton);
        
        // Row 4: More operators and controls
        buttonPanel.add(rightParenButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(addPolyButton);
        buttonPanel.add(new JLabel()); // Empty space
        buttonPanel.add(new JLabel()); // Empty space
        
        // Polynomial panel
        JPanel polyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        polyPanel.setBorder(BorderFactory.createTitledBorder("Polynomial Generator"));
        polyPanel.add(new JLabel("Degree:"));
        polyPanel.add(polyDegreeField);
        
        // Bottom panel with status and control buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(doneButton);
        controlPanel.add(cancelButton);
        bottomPanel.add(controlPanel, BorderLayout.EAST);
        
        // Layout everything
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(polyPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // Enhanced button listeners with smart insertion
        sinButton.addActionListener(e -> smartInsert("sin("));
        cosButton.addActionListener(e -> smartInsert("cos("));
        tanButton.addActionListener(e -> smartInsert("tan("));
        cotButton.addActionListener(e -> smartInsert("cot("));
        secButton.addActionListener(e -> smartInsert("sec("));
        cscButton.addActionListener(e -> smartInsert("csc("));
        logButton.addActionListener(e -> smartInsert("log("));
        sqrtButton.addActionListener(e -> smartInsert("sqrt("));
        expButton.addActionListener(e -> smartInsert("exp("));
        
        // Operator buttons with smart spacing
        powButton.addActionListener(e -> smartInsert("^"));
        plusButton.addActionListener(e -> smartInsert(" + "));
        minusButton.addActionListener(e -> smartInsert(" - "));
        mulButton.addActionListener(e -> smartInsert("*"));
        divButton.addActionListener(e -> smartInsert("/"));
        leftParenButton.addActionListener(e -> smartInsert("("));
        rightParenButton.addActionListener(e -> smartInsert(")"));
        
        // Variable and constant buttons
        xButton.addActionListener(e -> smartInsert("x"));
        piButton.addActionListener(e -> smartInsert("pi"));
        eButton.addActionListener(e -> smartInsert("e"));
        
        // Control buttons
        clearButton.addActionListener(e -> {
            equationField.setText("");
            updateStatus("Equation cleared");
        });
        
        deleteButton.addActionListener(e -> smartDelete());
        addPolyButton.addActionListener(e -> showEnhancedPolyInputDialog());
        
        // Control buttons
        doneButton.addActionListener(e -> {
            if (validateEquation()) {
                resultEquation = equationField.getText().trim();
                if (!resultEquation.isEmpty()) {
                    dispose();
                } else {
                    updateStatus("Please enter an equation");
                }
            } else {
                updateStatus("Please fix equation errors before proceeding");
            }
        });
        
        cancelButton.addActionListener(e -> {
            resultEquation = null;
            dispose();
        });
    }

    private void setupAdvancedKeyboardSupport() {
        // Enhanced key listener with function keys and better shortcuts
        equationField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleSpecialKeys(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                validateEquationRealTime();
            }
        });
        
        // Document listener for real-time validation
        equationField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateEquationRealTime(); }
            public void removeUpdate(DocumentEvent e) { validateEquationRealTime(); }
            public void insertUpdate(DocumentEvent e) { validateEquationRealTime(); }
        });
        
        // Enhanced input map for better keyboard shortcuts
        setupInputMap();
    }

    private void handleSpecialKeys(KeyEvent e) {
        if (e.isControlDown()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P:
                    smartInsert("pi");
                    e.consume();
                    break;
                case KeyEvent.VK_E:
                    smartInsert("e");
                    e.consume();
                    break;
                case KeyEvent.VK_A:
                    equationField.selectAll();
                    e.consume();
                    break;
            }
        } else {
            // Function key shortcuts
            switch (e.getKeyCode()) {
                case KeyEvent.VK_F1:
                    smartInsert("sin(");
                    e.consume();
                    break;
                case KeyEvent.VK_F2:
                    smartInsert("cos(");
                    e.consume();
                    break;
                case KeyEvent.VK_F3:
                    smartInsert("tan(");
                    e.consume();
                    break;
                case KeyEvent.VK_F4:
                    smartInsert("log(");
                    e.consume();
                    break;
                case KeyEvent.VK_F5:
                    smartInsert("sqrt(");
                    e.consume();
                    break;
                case KeyEvent.VK_ENTER:
                    if (validateEquation()) {
                        doneButton.doClick();
                    }
                    e.consume();
                    break;
                case KeyEvent.VK_ESCAPE:
                    cancelButton.doClick();
                    e.consume();
                    break;
            }
        }
    }

    private void setupInputMap() {
        InputMap inputMap = equationField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = equationField.getActionMap();
        
        // Add convenient key mappings
        inputMap.put(KeyStroke.getKeyStroke("shift 8"), "multiply");
        actionMap.put("multiply", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // Replace * with × for display (but keep * for parsing)
                // This is handled in smartInsert
            }
        });
    }

    private void smartInsert(String text) {
        try {
            int caretPos = equationField.getCaretPosition();
            String current = equationField.getText();
            
            // Smart insertion logic
            String beforeCaret = current.substring(0, caretPos);
            String afterCaret = current.substring(caretPos);
            
            // Auto-add multiplication symbol if needed
            if (needsMultiplication(beforeCaret, text)) {
                text = "*" + text;
            }
            
            String newText = beforeCaret + text + afterCaret;
            equationField.setText(newText);
            equationField.setCaretPosition(caretPos + text.length());
            
            updateStatus("Inserted: " + text);
        } catch (Exception e) {
            updateStatus("Error inserting text");
        }
    }

    private boolean needsMultiplication(String beforeText, String toInsert) {
        if (beforeText.isEmpty()) return false;
        
        char lastChar = beforeText.charAt(beforeText.length() - 1);
        
        // Add multiplication if:
        // - Last character is a digit or letter and we're inserting a function
        // - Last character is ) and we're inserting anything
        return (Character.isLetterOrDigit(lastChar) && 
                (toInsert.contains("(") || toInsert.equals("x") || toInsert.equals("pi") || toInsert.equals("e"))) ||
               (lastChar == ')' && !toInsert.startsWith(" "));
    }

    private void smartDelete() {
        try {
            int caretPos = equationField.getCaretPosition();
            String text = equationField.getText();
            
            if (caretPos > 0) {
                // Smart deletion - remove function names as a unit
                String beforeCaret = text.substring(0, caretPos);
                String toDelete = getLastToken(beforeCaret);
                
                int deleteLength = Math.min(toDelete.length(), caretPos);
                String newText = text.substring(0, caretPos - deleteLength) + text.substring(caretPos);
                equationField.setText(newText);
                equationField.setCaretPosition(caretPos - deleteLength);
                
                updateStatus("Deleted: " + toDelete);
            }
        } catch (Exception e) {
            updateStatus("Error deleting text");
        }
    }

    private String getLastToken(String text) {
        if (text.isEmpty()) return "";
        
        // Check for function names
        String[] functions = {"sin", "cos", "tan", "cot", "sec", "csc", "log", "sqrt", "exp"};
        for (String func : functions) {
            if (text.endsWith(func + "(") || text.endsWith(func)) {
                return func + (text.endsWith("(") ? "(" : "");
            }
        }
        
        // Default to single character
        return String.valueOf(text.charAt(text.length() - 1));
    }

    private boolean validateEquation() {
        String equation = equationField.getText().trim();
        if (equation.isEmpty()) {
            updateStatus("Equation is empty");
            return false;
        }
        
        // Check for balanced parentheses
        int openParens = 0;
        for (char c : equation.toCharArray()) {
            if (c == '(') openParens++;
            else if (c == ')') openParens--;
            if (openParens < 0) {
                updateStatus("Unmatched closing parenthesis");
                return false;
            }
        }
        
        if (openParens > 0) {
            updateStatus("Unmatched opening parenthesis");
            return false;
        }
        
        updateStatus("Equation is valid");
        return true;
    }

    private void validateEquationRealTime() {
        String equation = equationField.getText();
        
        // Real-time visual feedback
        int openParens = 0;
        boolean hasError = false;
        
        for (char c : equation.toCharArray()) {
            if (c == '(') openParens++;
            else if (c == ')') openParens--;
            if (openParens < 0) {
                hasError = true;
                break;
            }
        }
        
        if (hasError || openParens != 0) {
            equationField.setBackground(new Color(255, 240, 240)); // Light red
        } else {
            equationField.setBackground(Color.WHITE);
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(message.contains("Error") || message.contains("Unmatched") ? 
                                 Color.RED : Color.BLUE);
    }

    private void showEnhancedPolyInputDialog() {
        try {
            int degree = Integer.parseInt(polyDegreeField.getText());
            if (degree < 1 || degree > 10) {
                JOptionPane.showMessageDialog(this, "Degree must be between 1 and 10.");
                return;
            }
            
            JPanel coeffPanel = new JPanel(new GridLayout(degree + 1, 3, 5, 5));
            coeffPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            polyCoeffFields.clear();
            
            // Add headers
            coeffPanel.add(new JLabel("Power", JLabel.CENTER));
            coeffPanel.add(new JLabel("Coefficient", JLabel.CENTER));
            coeffPanel.add(new JLabel("Preview", JLabel.CENTER));
            
            for (int i = degree; i >= 0; i--) {
                // Power label
                JLabel powerLabel = new JLabel("x^" + i, JLabel.CENTER);
                powerLabel.setFont(new Font("Arial", Font.BOLD, 12));
                coeffPanel.add(powerLabel);
                
                // Coefficient field
                JTextField coeffField = new JTextField(i == degree ? "1" : "0", 8);
                coeffField.setHorizontalAlignment(JTextField.CENTER);
                polyCoeffFields.add(coeffField);
                coeffPanel.add(coeffField);
                
                // Preview label
                JLabel previewLabel = new JLabel("", JLabel.CENTER);
                previewLabel.setFont(new Font("Monospaced", Font.ITALIC, 10));
                coeffPanel.add(previewLabel);
                
                // Update preview when coefficient changes
                final int power = i;
                final JLabel preview = previewLabel;
                coeffField.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) { updatePreview(); }
                    public void removeUpdate(DocumentEvent e) { updatePreview(); }
                    public void insertUpdate(DocumentEvent e) { updatePreview(); }
                    
                    private void updatePreview() {
                        String coeff = coeffField.getText().trim();
                        if (!coeff.isEmpty() && !coeff.equals("0")) {
                            if (power == 0) {
                                preview.setText(coeff);
                            } else if (power == 1) {
                                preview.setText(coeff + "x");
                            } else {
                                preview.setText(coeff + "x^" + power);
                            }
                        } else {
                            preview.setText("");
                        }
                    }
                });
            }
            
            int result = JOptionPane.showConfirmDialog(this, coeffPanel, 
                "Enter Polynomial Coefficients (Degree " + degree + ")", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION) {
                StringBuilder equation = new StringBuilder();
                boolean firstTerm = true;
                
                for (int i = degree; i >= 0; i--) {
                    String coeff = polyCoeffFields.get(degree - i).getText().trim();
                    if (!coeff.isEmpty() && !coeff.equals("0")) {
                        if (!firstTerm && !coeff.startsWith("-")) {
                            equation.append(" + ");
                        } else if (!firstTerm) {
                            equation.append(" ");
                        }
                        
                        if (coeff.equals("1") && i > 0) {
                            // Don't show coefficient 1 for x terms
                        } else if (coeff.equals("-1") && i > 0) {
                            equation.append("-");
                        } else {
                            equation.append(coeff);
                            if (i > 0) equation.append("*");
                        }
                        
                        if (i > 0) equation.append("x");
                        if (i > 1) equation.append("^").append(i);
                        
                        firstTerm = false;
                    }
                }
                
                if (equation.length() > 0) {
                    smartInsert(equation.toString());
                } else {
                    updateStatus("No valid polynomial generated");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid degree entered.");
        }
    }

    public String getEquation() {
        return resultEquation;
    }
}
