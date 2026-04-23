package UserInterface.views;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    private UebersichtView uebersichtView;
    private WerkzeugleisteView werkzeugleisteView;
    private final StatusbereichView statusbereichView;
    private final HauptView hauptView;

    public View() {
        hauptView = new HauptView();
        statusbereichView = new StatusbereichView();
        uebersichtView = new UebersichtView();
        werkzeugleisteView = new WerkzeugleisteView();

        setTitle("Smart Home");
        setLayout(new GridBagLayout());

        JPanel untererBereich = new JPanel();
        JPanel rechterBereich = new JPanel();

        JSplitPane verticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                werkzeugleisteView,
                untererBereich
        );
        verticalSplit.setResizeWeight(0.2);

        JSplitPane horizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                uebersichtView,
                rechterBereich
        );
        horizontalSplit.setResizeWeight(0.2);

        JSplitPane horizontalSplitRechterTeilbereich = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                hauptView,
                statusbereichView
        );

        horizontalSplitRechterTeilbereich.setResizeWeight(0.6);

        add(verticalSplit);
        add(horizontalSplit);
        add(horizontalSplitRechterTeilbereich);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
