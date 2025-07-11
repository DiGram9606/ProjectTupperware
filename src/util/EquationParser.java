package src.util;

import java.util.function.Function;

public class EquationParser {
    public static Function<Double, Double> parse(String expression) {
        return new Parser(expression)::evaluate;
    }

    private static class Parser {
        private final String expr;
        private int index;
        private double x;

        Parser(String expr) {
            this.expr = expr.replaceAll("\\s+", "").toLowerCase();
        }

        double evaluate(double xVal) {
            this.x = xVal;
            this.index = 0;
            try {
                return parseExpression();
            } catch (Exception e) {
                return Double.NaN;
            }
        }

        double parseExpression() {
            double value = parseTerm();
            while (current() == '+' || current() == '-') {
                char op = consume();
                double next = parseTerm();
                value = (op == '+') ? value + next : value - next;
            }
            return value;
        }

        double parseTerm() {
            double value = parseFactor();
            while (current() == '*' || current() == '/') {
                char op = consume();
                double next = parseFactor();
                value = (op == '*') ? value * next : value / next;
            }
            return value;
        }

        double parseFactor() {
            double value = parseUnary();
            while (current() == '^') {
                consume();
                double exponent = parseFactor();
                value = Math.pow(value, exponent);
            }
            return value;
        }

        double parseUnary() {
            if (current() == '-') {
                consume();
                return -parseUnary();
            } else if (current() == '+') {
                consume();
            }
            return parsePrimary();
        }

        double parsePrimary() {
            char ch = current();
            if (ch == '(') {
                consume();
                double value = parseExpression();
                if (current() == ')') consume();
                return value;
            } else if (Character.isDigit(ch) || ch == '.') {
                return parseNumber();
            } else if (Character.isLetter(ch)) {
                return parseFunctionOrVariable();
            }
            throw new RuntimeException("Unexpected character: " + ch);
        }

        double parseNumber() {
            int start = index;
            while (Character.isDigit(current()) || current() == '.') consume();
            return Double.parseDouble(expr.substring(start, index));
        }

        double parseFunctionOrVariable() {
            int start = index;
            while (Character.isLetter(current())) consume();
            String name = expr.substring(start, index);
            if (name.equals("x")) return x;
            if (name.equals("pi")) return Math.PI;
            if (name.equals("e")) return Math.E;
            if (current() == '(') {
                consume();
                double arg = parseExpression();
                if (current() == ')') consume();
                switch (name) {
                    case "sin": return Math.sin(arg);
                    case "cos": return Math.cos(arg);
                    case "tan": return Math.tan(arg);
                    case "log": return Math.log(arg);
                    case "sqrt": return Math.sqrt(arg);
                    case "exp": return Math.exp(arg);
                    default: throw new RuntimeException("Unknown function: " + name);
                }
            }
            throw new RuntimeException("Unknown variable or function: " + name);
        }

        char current() {
            return index < expr.length() ? expr.charAt(index) : '\0';
        }

        char consume() {
            return index < expr.length() ? expr.charAt(index++) : '\0';
        }
    }
}
