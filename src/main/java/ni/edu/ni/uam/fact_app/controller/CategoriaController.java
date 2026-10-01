package ni.edu.ni.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.ni.uam.fact_app.dao.CategoriaDAO;
import ni.edu.ni.uam.fact_app.model.Categoria;

import java.sql.SQLException;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<String> cmbFiltroEstado;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private FilteredList<Categoria> filtroCategorias;

    @FXML
    private void initialize() {
        chkActiva.setSelected(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));

        colActiva.setCellFactory(col -> new TableCell<Categoria, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Activo" : "Inactivo");
                    setStyle(item
                            ? "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"
                            : "-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
            }
        });

        if (cmbFiltroEstado != null) {
            cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activas", "Inactivas"));
            cmbFiltroEstado.setValue("Todos");
            cmbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }

        filtroCategorias = new FilteredList<>(categorias, c -> true);

        if (txtBusqueda != null) {
            txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }

        SortedList<Categoria> sortedData = new SortedList<>(filtroCategorias);
        sortedData.comparatorProperty().bind(tblCategorias.comparatorProperty());
        tblCategorias.setItems(sortedData);

        cargarCategorias();
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(categoriaDAO.listar());
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = (txtBusqueda != null && txtBusqueda.getText() != null)
                ? txtBusqueda.getText().trim().toLowerCase()
                : "";
        String estado = (cmbFiltroEstado != null && cmbFiltroEstado.getValue() != null)
                ? cmbFiltroEstado.getValue()
                : "Todos";

        filtroCategorias.setPredicate(cat -> {
            boolean coincideTexto = true;
            if (!texto.isBlank()) {
                boolean coincideId = cat.getId() != null && String.valueOf(cat.getId()).contains(texto);
                boolean coincideNombre = cat.getNombre() != null && cat.getNombre().toLowerCase().contains(texto);
                coincideTexto = coincideId || coincideNombre;
            }

            boolean coincideEstado = true;
            if ("Activas".equals(estado)) {
                coincideEstado = cat.isActiva();
            } else if ("Inactivas".equals(estado)) {
                coincideEstado = !cat.isActiva();
            }

            return coincideTexto && coincideEstado;
        });
    }

    @FXML
    private void buscar() {
        aplicarFiltros();
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isBlank()) {
            mensaje(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.");
            return;
        }

        try {
            Categoria nueva = new Categoria(null, txtNombre.getText().trim(), chkActiva.isSelected());
            categoriaDAO.guardar(nueva);
            mensaje(Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
            cargarCategorias();
            limpiar();
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mensaje(Alert.AlertType.WARNING, "Debe seleccionar una categoría de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Está seguro de que desea eliminar la categoría \"" + seleccionada.getNombre() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                try {
                    categoriaDAO.eliminar(seleccionada.getId());
                    mensaje(Alert.AlertType.INFORMATION, "Categoría eliminada correctamente.");
                    cargarCategorias();
                    limpiar();
                } catch (SQLException e) {
                    mensaje(Alert.AlertType.ERROR, "No se puede eliminar la categoría porque tiene productos asignados o hubo un error: " + e.getMessage());
                }
            }
        });
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