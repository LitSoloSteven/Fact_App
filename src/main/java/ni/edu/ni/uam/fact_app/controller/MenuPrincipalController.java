package ni.edu.ni.uam.fact_app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.ni.uam.fact_app.util.SceneManager;
import java.io.IOException;

public class MenuPrincipalController {


    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalCategorias;
    @FXML private Label lblTotalCargos;

    @FXML
    public void initialize() {
        actualizarDashboard();
    }

    private void actualizarDashboard() {

        lblTotalProductos.setText("12");
        lblTotalCategorias.setText("3");
        lblTotalCargos.setText("5");
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