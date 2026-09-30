package ni.edu.ni.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.ni.uam.fact_app.dao.CargoDAO;
import ni.edu.ni.uam.fact_app.model.Cargo;

import java.sql.SQLException;
import java.util.List;

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
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tblCargos.setItems(listaCargos);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            listaCargos.setAll(cargoDAO.listar());
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los cargos: " + e.getMessage());
        }
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
    private void buscar() {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isBlank()) {
            cargarDatos();
            return;
        }

        try {
            List encontrados = cargoDAO.buscarPorCriterio(criterio);

            if (!encontrados.isEmpty()) {
                listaCargos.setAll(encontrados);
            } else {
                listaCargos.clear();
                mostrarMensaje(Alert.AlertType.INFORMATION, "Búsqueda",
                        "No se encontró ningún cargo que coincida con: \"" + criterio + "\".");
            }
        } catch (SQLException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error BD", "Error al buscar cargo: " + e.getMessage());
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