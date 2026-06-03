package data.models.fachobjekte;

import util.statusmeldungen.StatusLog;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;


public abstract class Geraet extends DAO {
    private static final String DIGITS = "(\\p{Digit}+)";
    private static final String HEX_DIGITS = "(\\p{XDigit}+)";
    private static final String EXP = "[eE][+-]?"+ DIGITS;
    private static final String FP_REGEX =
           ("[\\x00-\\x20]*"+
                    "[+-]?(" +
                    "NaN|" +
                    "Infinity|" +
                    "((("+ DIGITS +"(\\.)?("+ DIGITS +"?)("+ EXP +")?)|"+
                   "(\\.("+ DIGITS +")("+ EXP +")?)|"+
                   "((" +
                    "(0[xX]" + HEX_DIGITS + "(\\.)?)|" +
                    "(0[xX]" + HEX_DIGITS + "?(\\.)" + HEX_DIGITS + ")" +
                   ")[pP][+-]?" + DIGITS + "))" +
                   "[fFdD]?))" +
                   "[\\x00-\\x20]*");

    private Raum raum;

    public Geraet(final UUID id, final String name, final Raum raum) {
        super(id, name);
        this.raum = raum;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(final Raum raum) {
        this.raum = raum;
    }

    public void setValues(final Map<String, String> map) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            updateValue(entry.getKey(), entry.getValue());
        }
    }
    public abstract boolean isGueltigeAttribute(Map<String, String> attributeMap);

    public abstract void updateValue(String key, String value);

    public abstract Map<String, String> getValues();

    public abstract Map<String, Class<?>> getAttributTypen();

    public boolean isDouble(String myString) {
        return Pattern.matches(FP_REGEX, myString);
    }
    public String formatiereZahlenwerteInsEnglische(String unformatierterWert) {
        String formatierterWert = unformatierterWert.replace(".", "").replace(",", ".");
        if (formatierterWert.equals(unformatierterWert)) StatusLog.addHinweis("Der Dezimalpunkt ist das Komma");
        return formatierterWert;
    }
    public String formatiereZahlenwerteInsDeutsche(String unformatierterWert) {
        return unformatierterWert.replace(".",",");
    }
}
