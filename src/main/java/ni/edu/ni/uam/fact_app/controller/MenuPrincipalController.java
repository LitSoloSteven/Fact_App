package ni.edu.ni.uam.fact_app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.ni.uam.fact_app.util.SceneManager;
import java.io.IOException;

public class MenuPrincipalController {

    @FXML
    private void abrirProductos() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/ni/uam/fact_app/fxml/producto-view.fxml",
                    "Gestión de productos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Productos.\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void abrirCategorias() {
        try {
            SceneManager.abrirVentana(
                    "/ni/edu/ni/uam/fact_app/fxml/categoria-view.fxml",
                    "Gestión de categorías");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Categorías.\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void abrirCargos() {
        try {
            // Asegúrate de que tu archivo fxml de cargos se llame exactamente "cargo-view.fxml"
            SceneManager.abrirVentana(
                    "/ni/edu/ni/uam/fact_app/fxml/cargo-view.fxml",
                    "Gestión de cargos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "No fue posible abrir Cargos.\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}