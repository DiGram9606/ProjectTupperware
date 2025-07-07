package src.util;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

// This class stores just the serializable data
public class SavedGraphState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class SerializableFunction implements Serializable {
        private static final long serialVersionUID = 1L;
        public String label;
        public double parameter;
        public String type;

        public SerializableFunction(String type, double parameter) {
            this.type = type;
            this.parameter = parameter;
            this.label = type + " with param " + parameter;
        }

        public Function<Double, Double> toFunction() {
            double param = this.parameter;
            switch (type) {
                case "sin(x)": return x -> param * Math.sin(x);
                case "cos(x)": return x -> param * Math.cos(x);
                case "tan(x)": return x -> param * Math.tan(x);
                case "x^2": return x -> param * x * x;
                case "x^3": return x -> param * x * x * x;
                case "2x+3": return x -> param * (2.0 * x + 3.0);
                case "log(x)": return x -> x <= 0 ? Double.NaN : param * Math.log(x);
                case "exp(x)": return x -> param * Math.exp(x);
                case "step(x)": return x -> x >= 0 ? param : 0.0;
                default: return x -> 0.0;
            }
        }
    }

    public List<SerializableFunction> functions;
    public Color background;

    public SavedGraphState(List<SerializableFunction> functions, Color background) {
        this.functions = functions;
        this.background = background;
    }
}
