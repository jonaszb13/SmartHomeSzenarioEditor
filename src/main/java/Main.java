import userInterface.views.View;
import controller.Controller;
import data.models.Model;

import javax.swing.*;

public class Main {
    public static void main(String[] arsgs) {
        SwingUtilities.invokeLater(() -> new Controller());
    }
}
