package ni.edu.ni.uam.fact_app.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.ni.uam.fact_app.model.Categoria;

public class CategoriaController {
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private int contadorId = 4; // Comienza en 4 porque ya quemamos 3 en initialize

    @FXML
    private void initialize() {
        // Precargar las mismas categorías que usas en el ProductoController
        categorias.addAll(
                new Categoria(1, "Alimentos", true),
                new Categoria(2, "Bebidas", true),
                new Categoria(3, "Limpieza", true)
        );
        tblCategorias.setItems(categorias);
        chkActiva.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            mensaje(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.");
            return;
        }

        categorias.add(new Categoria(
                contadorId++,
                txtNombre.getText().trim(),
                chkActiva.isSelected()
        ));

        mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
        limpiar();
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombre.clear();
        chkActiva.setSelected(true);
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}