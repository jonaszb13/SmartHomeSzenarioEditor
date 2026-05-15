package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class RaeumeViewTest extends ApplicationTest {

    @Override
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/userInterface/raume-view.fxml")
        );
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    @Test
    void testUeberschriftSichtbar() {
        verifyThat("Raumübersicht", isVisible());
    }

    @Test
    void testButtonNeuenRaumAnlegenSichtbar() {
        verifyThat("Neuen Raum anlegen", isVisible());
    }

    @Test
    void testButtonAuswahlLoeschenSichtbar() {
        verifyThat("Auswahl löschen", isVisible());
    }

    @Test
    void testButtonNeuenRaumAnlegenKlickbar() {
        clickOn("Neuen Raum anlegen");
    }

    @Test
    void testButtonAuswahlLoeschenKlickbar() {
        clickOn("Auswahl löschen");
    }
}
