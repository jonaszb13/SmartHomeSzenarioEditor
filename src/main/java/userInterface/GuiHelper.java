package userInterface;

import java.awt.*;

public class GuiHelper {
    private final static Dimension FULL_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public static Dimension getFullSize(){
        return FULL_SIZE;
    }

    public static int getFullSizeWidth() {
        return FULL_SIZE.width;
    }

    public static int getFullSizeHeight() {
        return FULL_SIZE.height;
    }
}
