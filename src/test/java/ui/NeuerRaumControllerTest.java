package ui;

import controller.NeuerRaumController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    private static final AtomicReference<String> CAPTURED_NAME = new AtomicReference<>();
    private static final AtomicBoolean ABBRECHEN_CALLBACK = new AtomicBoolean();

    @BeforeEach
    void reset() {
        CAPTURED_NAME.set(null);
        ABBRECHEN_CALLBACK.set(false);
        interact(() -> lookup("#raumNameField").<TextField>query().clear());
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/userinterface/neuer-raum-view.fxml"));
        final Parent root = loader.load();
        final NeuerRaumController controller = loader.getController();
        controller.setOnAnlegen(CAPTURED_NAME::set);
        controller.setOnAbbrechen(() -> ABBRECHEN_CALLBACK.set(true));
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

        assertEquals("Wohnzimmer", CAPTURED_NAME.get());
    }

    @Test
    void testLeererNameWirdNichtAkzeptiert() {
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertNull(CAPTURED_NAME.get());
    }

    @Test
    void testNurLeerzeichenWirdNichtAkzeptiert() {
        interact(() -> lookup("#raumNameField").<TextField>query().setText(""));
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertNull(CAPTURED_NAME.get());
    }

    @Test
    void testNameWirdGetrimmt() {
        interact(() -> lookup("#raumNameField").<TextField>query().setText(" Küche "));
        interact(() -> lookup("Raum anlegen").<Button>query().fire());

        assertEquals("Küche", CAPTURED_NAME.get());
    }

    @Test
    void testAbbrechenCallback() {
        interact(() -> lookup("Abbrechen").<Button>query().fire());

        assertTrue(ABBRECHEN_CALLBACK.get());
    }
}
