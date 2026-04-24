package userInterface.views;

import userInterface.GuiHelper;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class View extends JFrame {

    private final static Dimension WERKZEUGLEISTE_PREFERRED_SIZE = new Dimension((int) (GuiHelper.getFullSizeWidth() * 0.3), (int) (GuiHelper.getFullSizeHeight() * 0.1));
    private final static Dimension UEBERSICHTSBEREICH_PREFERRED_SIZE = new Dimension((int) (GuiHelper.getFullSizeWidth() * 0.2), (int) (GuiHelper.getFullSizeHeight() * 0.9));
    private final static Dimension STATUSBEREICH_PREFERRED_SIZE = new Dimension((int) (GuiHelper.getFullSizeWidth() * 0.3), (int) (GuiHelper.getFullSizeHeight() * 0.9));
    private final static Dimension HAUPTBEREICH_PREFERRED_SIZE = new Dimension((int) (GuiHelper.getFullSizeWidth() * 0.7), (int) (GuiHelper.getFullSizeHeight() * 0.9));

    private final static Color HELLGRAU = Color.lightGray;

    private final JPanel werkzeugleistePanel;
    private final JPanel uebersichtPanel;
    private final JPanel statusbereichPanel;
    private final JPanel hauptPanel;

    private final JTree uebersichtTree = new JTree(createTreeModel());

    public View() {

        setTitle("Smart Home");
        setLayout(new BorderLayout());
        setPreferredSize(GuiHelper.getFullSize());


        werkzeugleistePanel = createWerkzeugleistePanel();
        uebersichtPanel = createUebersichtPanel();
        statusbereichPanel = createStatusbereichPanel();
        hauptPanel = createHauptPanel();

        add(werkzeugleistePanel, BorderLayout.NORTH);
        add(uebersichtPanel, BorderLayout.WEST);
        add(statusbereichPanel, BorderLayout.EAST);
        add(hauptPanel, BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addUebersichtTreeSelectionListener(TreeSelectionListener listener) {
        uebersichtTree.addTreeSelectionListener(listener);
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) uebersichtTree.getLastSelectedPathComponent();
    }

    public JPanel getHauptPanel() {
        return hauptPanel;
    }

    private JPanel createWerkzeugleistePanel() {
        JPanel werkzeugleistePanel = new JPanel();

        werkzeugleistePanel.setPreferredSize(WERKZEUGLEISTE_PREFERRED_SIZE);
        werkzeugleistePanel.setBackground(HELLGRAU);
        werkzeugleistePanel.setOpaque(true);

        return werkzeugleistePanel;
    }

    private JPanel createUebersichtPanel() {
        JPanel uebersichtPanel = new JPanel();
        uebersichtPanel.setPreferredSize(UEBERSICHTSBEREICH_PREFERRED_SIZE);
        uebersichtPanel.setBackground(HELLGRAU);
        uebersichtPanel.setOpaque(true);

        uebersichtTree.setRootVisible(false);
        uebersichtTree.setBackground(HELLGRAU);
        uebersichtPanel.add(uebersichtTree);

        return uebersichtPanel;
    }

    private DefaultMutableTreeNode createTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        //TODO Erstellung der Kinderknoten aller drei Kategorien
        DefaultMutableTreeNode raeume = new DefaultMutableTreeNode("   Räume ");
        DefaultMutableTreeNode geraete = new DefaultMutableTreeNode("   Geräte   ");
        DefaultMutableTreeNode szenarien = new DefaultMutableTreeNode("Szenarien");

        root.add(raeume);
        root.add(geraete);
        root.add(szenarien);

        return root;
    }

    private JPanel createStatusbereichPanel() {
        JPanel statusbereichPanel = new JPanel();
        statusbereichPanel.setPreferredSize(STATUSBEREICH_PREFERRED_SIZE);
        statusbereichPanel.setBackground(HELLGRAU);
        statusbereichPanel.setOpaque(true);

        return statusbereichPanel;
    }

    private JPanel createHauptPanel() {
        JPanel hauptPanel = new JPanel();
        hauptPanel.setPreferredSize(HAUPTBEREICH_PREFERRED_SIZE);

        return hauptPanel;
    }
}
