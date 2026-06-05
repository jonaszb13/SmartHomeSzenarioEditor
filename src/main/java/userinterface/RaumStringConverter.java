package userinterface;

import data.models.fachobjekte.Raum;
import javafx.util.StringConverter;

public class RaumStringConverter extends StringConverter<Raum> {
    @Override
    public String toString(final Raum raum) {
        return raum != null ? raum.getName() : "";
    }

    @Override
    public Raum fromString(final String s) {
        return null;
    }
}
