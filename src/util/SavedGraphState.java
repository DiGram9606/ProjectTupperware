package src.util;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

public class SavedGraphState implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class SerializableFunction implements Serializable {
        private static final long serialVersionUID = 1L;
        public String expression;

        public SerializableFunction(String expression) {
            this.expression = expression;
        }

        public String getLabel() {
            return expression;
        }
    }

    public List<SerializableFunction> functions;
    public Color background;

    public SavedGraphState(List<SerializableFunction> functions, Color background) {
        this.functions = functions;
        this.background = background;
    }
}
