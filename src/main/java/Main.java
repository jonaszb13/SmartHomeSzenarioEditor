import UserInterface.views.View;
import controller.Controller;
import data.models.Model;

import javax.swing.*;

public class Main {
    public static void main(String[] arsgs) {
        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View();
            Controller controller = new Controller(model, view);
        });
    }
}
