package userInterface;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DynamischeFelderBuilder {

    private final Map<String, Node> controls = new LinkedHashMap<>();
    private final Map<String, Class<?>> typen;
    private final VBox vBox;

    private DynamischeFelderBuilder(final Map<String, Class<?>> typen, final Map<String, String> werte, final boolean editierbar) {
        this.typen = typen;
        this.vBox = buildVBox(werte, editierbar);
    }

    public static DynamischeFelderBuilder fuerAnzeige(final Map<String, Class<?>> typen, final Map<String, String> werte) {
        return new DynamischeFelderBuilder(typen, werte, false);
    }

    public static DynamischeFelderBuilder fuerBearbeitung(final Map<String, Class<?>> typen, final Map<String, String> werte) {
        return new DynamischeFelderBuilder(typen, werte, true);
    }

    public static DynamischeFelderBuilder fuerNeuesGeraet(final Map<String, Class<?>> typen) {
        return new DynamischeFelderBuilder(typen, Map.of(), true);
    }

    private VBox buildVBox(final Map<String, String> werte, final boolean editierbar) {
        final VBox box = new VBox(10);
        for (final Map.Entry<String, Class<?>> entry : typen.entrySet()) {
            final String key = entry.getKey();
            final String wert = werte.getOrDefault(key, "");
            final Node control = createControl(entry.getValue(), wert, editierbar);
            controls.put(key, control);
            box.getChildren().add(new VBox(4, new Label(key), control));
        }
        return box;
    }

    private Node createControl(final Class<?> typ, final String wert, final boolean editierbar) {
        if (typ == boolean.class || typ == Boolean.class) {
            final CheckBox cb = new CheckBox();
            cb.setSelected(Boolean.parseBoolean(wert));
            cb.setDisable(!editierbar);
            return cb;
        }
        if (typ == java.awt.Color.class) {
            final ColorPicker cp = new ColorPicker();
            try {
                if (!wert.isBlank()) {
                    final java.awt.Color awtColor = java.awt.Color.decode(wert);
                    cp.setValue(javafx.scene.paint.Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
                }
            } catch (NumberFormatException ignored) {}
            cp.setDisable(!editierbar);
            return cp;
        }
        final TextField tf = new TextField(wert);
        tf.setEditable(editierbar);
        if (!editierbar) tf.setStyle("-fx-background-color: #f0f0f0;");
        return tf;
    }

    public VBox getVBox() {
        return vBox;
    }

    public Map<String, String> getWerte() {
        final Map<String, String> result = new LinkedHashMap<>();
        for (final Map.Entry<String, Node> entry : controls.entrySet()) {
            final String key = entry.getKey();
            final Node node = entry.getValue();
            if (node instanceof CheckBox cb) {
                result.put(key, Boolean.toString(cb.isSelected()));
            } else if (node instanceof ColorPicker cp) {
                final javafx.scene.paint.Color fxColor = cp.getValue();
                result.put(key, String.format("#%02x%02x%02x",
                        (int) (fxColor.getRed() * 255),
                        (int) (fxColor.getGreen() * 255),
                        (int) (fxColor.getBlue() * 255)));
            } else if (node instanceof TextField tf) {
                result.put(key, tf.getText());
            }
        }
        return result;
    }
}
