package userInterface;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

public final class WertControlFactory {

    private WertControlFactory() {}

    public static Node erstelle(final Class<?> typ, final String wert) {
        if (typ == boolean.class || typ == Boolean.class) {
            final CheckBox cb = new CheckBox();
            cb.setSelected(Boolean.parseBoolean(wert));
            return cb;
        }
        if (typ == java.awt.Color.class) {
            final ColorPicker cp = new ColorPicker();
            try {
                if (!wert.isBlank()) {
                    final java.awt.Color awtColor = java.awt.Color.decode(wert);
                    cp.setValue(javafx.scene.paint.Color.rgb(
                            awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
                }
            } catch (NumberFormatException ignored) {}
            return cp;
        }
        return new TextField(wert);
    }

    public static String leseWert(final Node control) {
        if (control instanceof CheckBox cb) return Boolean.toString(cb.isSelected());
        if (control instanceof ColorPicker cp) {
            final javafx.scene.paint.Color c = cp.getValue();
            return String.format("#%02x%02x%02x",
                    (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
        }
        if (control instanceof TextField tf) return tf.getText();
        return "";
    }
}
