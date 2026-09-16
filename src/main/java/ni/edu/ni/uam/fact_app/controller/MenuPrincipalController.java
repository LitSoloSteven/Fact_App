package ni.edu.ni.uam.fact_app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import ni.edu.ni.uam.fact_app.util.DataStore;
import ni.edu.ni.uam.fact_app.util.SceneManager;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalCategorias;
    @FXML private Label lblTotalCargos;

    @FXML private Button btnProductos;
    @FXML private Button btnCategorias;
    @FXML private Button btnCargos;
    @FXML private Button btnSalir;

    @FXML
    public void initialize() {
        configurarTooltips();
        actualizarDashboard();
    }

    private void configurarTooltips() {
        Button[] botones = {btnProductos, btnCategorias, btnCargos, btnSalir};
        for (Button b : botones) {
            if (b != null && b.getTooltip() != null) {
                b.getTooltip().setShowDelay(Duration.millis(100));
            }
        }
    }

    private void actualizarDashboard() {
        lblTotalProductos.setText(String.valueOf(DataStore.getProductos().size()));
        lblTotalCategorias.setText(String.valueOf(DataStore.getCategorias().size()));
        lblTotalCargos.setText(String.valueOf(DataStore.getCargos().size()));
    }

    @FXML
    private void abrirProductos() {
        try {
            SceneManager.abrirVentana("/ni/edu/ni/uam/fact_app/fxml/producto-view.fxml", "Gestión de productos");
            actualizarDashboard();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void abrirCategorias() {
        try {
            SceneManager.abrirVentana("/ni/edu/ni/uam/fact_app/fxml/categoria-view.fxml", "Gestión de categorías");
            actualizarDashboard();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void abrirCargos() {
        try {
            SceneManager.abrirVentana("/ni/edu/ni/uam/fact_app/fxml/cargo-view.fxml", "Gestión de cargos");
            actualizarDashboard();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}