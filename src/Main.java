package src;

import javax.swing.SwingUtilities;
import src.ui.DesmosCloneApp;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesmosCloneApp().setVisible(true));
    }
}
