package ui;

import controller.NeuerRaumController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class NeuerRaumControllerTest extends ApplicationTest {

    private static final AtomicReference<String> capturedName = new AtomicReference<>();
    private static final AtomicBoolean abbrechenCallback = new AtomicBoolean();

    @BeforeEach
    void reset() {
        capturedName.set(null);
        abbrechenCallback.set(false);
        interact(() -> lookup("#raumNameField").<TextField>query().clear());
    }

    @Override
    public void start(final Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/userInterface/neuer-raum-view.fxml"));
        Parent root = loader.load();
        NeuerRaumController controller = loader.getController();
        controller.setOnAnlegen(capturedName::set);
        controller.setOnAbbrechen(() -> abbrechenCallback.set(true));
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    @Test
    void testTitelSichtbar() {
        verifyThat("Neuen Raum anlegen", isVisible());
    }

    @Test
    void testButtonsSichtbar() {
        verifyThat("Raum anlegen", isVisible());
        verifyThat("Abbrechen", isVisible());
    }

    @Test
    void testRaumAnlegenFeuertCallbackMitName() {
        interact(() -> lookup("#raumNameField").<TextField>query().setText("Wohnzimmer"));
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertEquals("Wohnzimmer", capturedName.get());
    }

    @Test
    void testLeererNameWirdNichtAkzeptiert() {
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertNull(capturedName.get());
    }

    @Test
    void testNurLeerzeichenWirdNichtAkzeptiert() {
        interact(() -> lookup("#raumNameField").<TextField>query().setText(""));
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertNull(capturedName.get());
    }

    @Test
    void testNameWirdGetrimmt() {
        interact(() -> lookup("#raumNameField").<TextField>query().setText(" Küche "));
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertEquals("Küche", capturedName.get());
    }

    @Test
    void testAbbrechenCallback() {
        interact(() -> lookup("Abbrechen").<Button>query().fire());

        assertTrue(abbrechenCallback.get());
    }
}
