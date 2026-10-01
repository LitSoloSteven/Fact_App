package ni.edu.ni.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.ni.uam.fact_app.dao.CargoDAO;
import ni.edu.ni.uam.fact_app.model.Cargo;

import java.sql.SQLException;

public class CargoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final CargoDAO cargoDAO = new CargoDAO();
    private final ObservableList<Cargo> listaCargos = FXCollections.observableArrayList();
    private FilteredList<Cargo> filtroCargos;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        filtroCargos = new FilteredList<>(listaCargos, c -> true);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());

        SortedList<Cargo> listaOrdenada = new SortedList<>(filtroCargos);
        listaOrdenada.comparatorProperty().bind(tblCargos.comparatorProperty());
        tblCargos.setItems(listaOrdenada);

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            listaCargos.setAll(cargoDAO.listar());
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los cargos: " + e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String criterio = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();

        filtroCargos.setPredicate(cargo -> {
            if (criterio.isBlank()) return true;

            boolean coincideId = cargo.getId() != null && String.valueOf(cargo.getId()).contains(criterio);
            boolean coincideNombre = cargo.getNombre() != null && cargo.getNombre().toLowerCase().contains(criterio);
            boolean coincideDesc = cargo.getDescripcion() != null && cargo.getDescripcion().toLowerCase().contains(criterio);

            return coincideId || coincideNombre || coincideDesc;
        });
    }

    @FXML
    private void buscar() {
        aplicarFiltro();
    }

    @FXML
    private void agregar() {
        if (txtId.getText().isBlank() || txtNombre.getText().isBlank() || txtDescripcion.getText().isBlank()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Cargo cargo = new Cargo(id, txtNombre.getText().trim(), txtDescripcion.getText().trim());

            if (modoEdicion) {
                cargoDAO.actualizar(cargo);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cargo actualizado correctamente.");
            } else {
                cargoDAO.guardar(cargo);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cargo guardado correctamente.");
            }

            cargarDatos();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "El ID debe ser un número entero válido.");
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error BD", "Error al guardar en base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void editar() {
        Cargo seleccionado = tblCargos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Seleccione un cargo de la tabla para editar.");
            return;
        }

        txtId.setText(String.valueOf(seleccionado.getId()));
        txtId.setDisable(true);
        txtNombre.setText(seleccionado.getNombre());
        txtDescripcion.setText(seleccionado.getDescripcion());
        modoEdicion = true;
    }

    @FXML
    private void eliminar() {
        Cargo seleccionado = tblCargos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Seleccione un cargo de la tabla para eliminar.");
            return;
        }

        try {
            cargoDAO.eliminar(seleccionado.getId());
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cargo eliminado.");
            cargarDatos();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error BD", "No se pudo eliminar: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtId.setDisable(false);
        txtNombre.clear();
        txtDescripcion.clear();
        txtBuscar.clear();
        modoEdicion = false;
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}