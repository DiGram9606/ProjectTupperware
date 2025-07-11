package src.util;

public class Derivative {
    public static String differentiate(String expr) {
        expr = expr.replaceAll("\\s+", "").toLowerCase();

        // Handle parentheses by recursion
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return differentiate(expr.substring(1, expr.length() - 1));
        }

        // Sum rule: f(x) + g(x)
        int plus = findTopLevelOperator(expr, '+');
        if (plus > 0) {
            return differentiate(expr.substring(0, plus)) + " + " +
                    differentiate(expr.substring(plus + 1));
        }

        // Difference rule: f(x) - g(x)
        int minus = findTopLevelOperator(expr, '-');
        if (minus > 0) {
            return differentiate(expr.substring(0, minus)) + " - " +
                    differentiate(expr.substring(minus + 1));
        }

        // Product rule: f(x) * g(x)
        int mult = findTopLevelOperator(expr, '*');
        if (mult > 0) {
            String u = expr.substring(0, mult);
            String v = expr.substring(mult + 1);
            return "(" + differentiate(u) + ")*(" + v + ") + (" + u + ")*(" + differentiate(v) + ")";
        }

        // Quotient rule: f(x) / g(x)
        int div = findTopLevelOperator(expr, '/');
        if (div > 0) {
            String u = expr.substring(0, div);
            String v = expr.substring(div + 1);
            return "((" + differentiate(u) + ")*(" + v + ") - (" + u + ")*(" + differentiate(v) + ")) / ((" + v + ")^2)";
        }

        // Power rule: x^n
        int pow = findTopLevelOperator(expr, '^');
        if (pow > 0) {
            String base = expr.substring(0, pow);
            String exp = expr.substring(pow + 1);
            if (base.equals("x")) {
                return exp + "*x^(" + exp + "-1)";
            }
            // Chain rule for (g(x))^n
            return exp + "*(" + base + ")^(" + exp + "-1)*(" + differentiate(base) + ")";
        }

        // Elementary functions
        if (expr.equals("x")) return "1";
        if (expr.matches("-?\\d+(\\.\\d+)?")) return "0"; // constant

        if (expr.startsWith("sin(") && expr.endsWith(")")) {
            String arg = expr.substring(4, expr.length() - 1);
            return "cos(" + arg + ")*(" + differentiate(arg) + ")";
        }
        if (expr.startsWith("cos(") && expr.endsWith(")")) {
            String arg = expr.substring(4, expr.length() - 1);
            return "-sin(" + arg + ")*(" + differentiate(arg) + ")";
        }
        if (expr.startsWith("tan(") && expr.endsWith(")")) {
            String arg = expr.substring(4, expr.length() - 1);
            return "1/(cos(" + arg + ")^2)*(" + differentiate(arg) + ")";
        }
        if (expr.startsWith("exp(") && expr.endsWith(")")) {
            String arg = expr.substring(4, expr.length() - 1);
            return "exp(" + arg + ")*(" + differentiate(arg) + ")";
        }
        if (expr.startsWith("log(") && expr.endsWith(")")) {
            String arg = expr.substring(4, expr.length() - 1);
            return "1/(" + arg + ")*(" + differentiate(arg) + ")";
        }
        if (expr.startsWith("sqrt(") && expr.endsWith(")")) {
            String arg = expr.substring(5, expr.length() - 1);
            return "0.5/sqrt(" + arg + ")*(" + differentiate(arg) + ")";
        }

        throw new UnsupportedOperationException("Cannot differentiate: " + expr);
    }

    // Helper to find top-level operator (not inside parentheses)
    private static int findTopLevelOperator(String expr, char op) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == op && depth == 0) return i;
        }
        return -1;
    }
}

