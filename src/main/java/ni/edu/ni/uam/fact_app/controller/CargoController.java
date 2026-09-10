package ni.edu.ni.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.ni.uam.fact_app.model.Cargo;

public class CargoController {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cargo> tblCargos;
    @FXML private TableColumn<Cargo, Integer> colId;
    @FXML private TableColumn<Cargo, String> colNombre;
    @FXML private TableColumn<Cargo, String> colDescripcion;

    private final ObservableList<Cargo> listaCargos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));


        listaCargos.add(new Cargo(1, "Administrador", "Acceso total al sistema"));
        listaCargos.add(new Cargo(2, "Cajero", "Gestión de facturas y cobros"));

        tblCargos.setItems(listaCargos);
    }

    @FXML
    private void agregar() {
        if (txtId.getText().isBlank() || txtNombre.getText().isBlank() || txtDescripcion.getText().isBlank()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText().trim());


            boolean existe = listaCargos.stream().anyMatch(c -> c.getId().equals(id));
            if (existe) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "El ID ya está registrado.");
                return;
            }

            listaCargos.add(new Cargo(id, txtNombre.getText().trim(), txtDescripcion.getText().trim()));
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cargo agregado correctamente.");
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "El ID debe ser un número entero válido.");
        }
    }

    @FXML
    private void buscar() {
        if (txtBuscar.getText().isBlank()) {
            tblCargos.setItems(listaCargos);
            return;
        }

        try {
            int idBusqueda = Integer.parseInt(txtBuscar.getText().trim());
            ObservableList<Cargo> resultado = FXCollections.observableArrayList();

            for (Cargo c : listaCargos) {
                if (c.getId().equals(idBusqueda)) {
                    resultado.add(c);
                }
            }

            tblCargos.setItems(resultado);
            if (resultado.isEmpty()) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Búsqueda", "No se encontró ningún cargo con ese ID.");
            }
        } catch (NumberFormatException e) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Ingrese un ID numérico para buscar.");
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


        listaCargos.remove(seleccionado);
    }

    @FXML
    private void eliminar() {
        Cargo seleccionado = tblCargos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaCargos.remove(seleccionado);
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Cargo eliminado.");
            limpiarCampos();
        } else {
            mostrarMensaje(Alert.AlertType.WARNING, "Advertencia", "Seleccione un cargo de la tabla para eliminar.");
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtId.setDisable(false);
        txtNombre.clear();
        txtDescripcion.clear();
        txtBuscar.clear();
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}