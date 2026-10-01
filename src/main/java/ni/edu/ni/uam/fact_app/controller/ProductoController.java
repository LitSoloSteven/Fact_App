package ni.edu.ni.uam.fact_app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.ni.uam.fact_app.dao.CategoriaDAO;
import ni.edu.ni.uam.fact_app.dao.ProductoDAO;
import ni.edu.ni.uam.fact_app.model.Categoria;
import ni.edu.ni.uam.fact_app.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private TextField txtBusqueda;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;
    @FXML private ComboBox<String> cmbFiltroEstado;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colFoto;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private FilteredList<Producto> filtroProductos;
    private String rutaImagen;

    @FXML
    private void initialize() {
        chkActivo.setSelected(true);

        colFoto.setCellValueFactory(new PropertyValueFactory<>("rutaImagen"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colFoto.setCellFactory(col -> new TableCell<Producto, String>() {
            private final ImageView thumbnail = new ImageView();
            {
                thumbnail.setFitHeight(36);
                thumbnail.setFitWidth(36);
                thumbnail.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String ruta, boolean empty) {
                super.updateItem(ruta, empty);
                if (empty || ruta == null || ruta.isBlank()) {
                    setGraphic(null);
                } else {
                    try {
                        thumbnail.setImage(new Image(ruta, 36, 36, true, true));
                        setGraphic(thumbnail);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        colActivo.setCellFactory(col -> new TableCell<Producto, Boolean>() {
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
            cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
            cmbFiltroEstado.setValue("Todos");
            cmbFiltroEstado.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        }

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtCodigo.setText(seleccionado.getCodigo());
                txtNombre.setText(seleccionado.getNombre());

                for (Categoria cat : cmbCategoria.getItems()) {
                    if (cat.getId().equals(seleccionado.getCategoria().getId())) {
                        cmbCategoria.setValue(cat);
                        break;
                    }
                }

                txtPrecio.setText(seleccionado.getPrecioVenta() != null ? seleccionado.getPrecioVenta().toString() : "");
                txtExistencia.setText(String.valueOf(seleccionado.getExistencia()));
                chkActivo.setSelected(seleccionado.isActivo());

                if (seleccionado.getRutaImagen() != null && !seleccionado.getRutaImagen().isBlank()) {
                    try {
                        imgProducto.setImage(new Image(seleccionado.getRutaImagen()));
                        rutaImagen = seleccionado.getRutaImagen();
                    } catch (Exception e) {
                        imgProducto.setImage(null);
                        rutaImagen = null;
                    }
                } else {
                    imgProducto.setImage(null);
                    rutaImagen = null;
                }
            }
        });

        filtroProductos = new FilteredList<>(productos, p -> true);

        if (txtBusqueda != null) {
            txtBusqueda.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        }
        if (cmbFiltroCategoria != null) {
            cmbFiltroCategoria.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        }

        SortedList<Producto> sortedData = new SortedList<>(filtroProductos);
        sortedData.comparatorProperty().bind(tblProductos.comparatorProperty());
        tblProductos.setItems(sortedData);

        cargarCategorias();
        cargarProductos();
    }

    private void cargarCategorias() {
        try {
            List<Categoria> lista = categoriaDAO.listar();
            cmbCategoria.setItems(FXCollections.observableArrayList(lista));

            if (cmbFiltroCategoria != null) {
                List<Categoria> listaFiltro = new ArrayList<>();
                listaFiltro.add(new Categoria(null, "Todas las categorías", true));
                listaFiltro.addAll(lista);
                cmbFiltroCategoria.setItems(FXCollections.observableArrayList(listaFiltro));
                cmbFiltroCategoria.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String texto = (txtBusqueda != null && txtBusqueda.getText() != null)
                ? txtBusqueda.getText().trim().toLowerCase()
                : "";
        Categoria catFiltro = cmbFiltroCategoria != null ? cmbFiltroCategoria.getValue() : null;
        String estado = (cmbFiltroEstado != null && cmbFiltroEstado.getValue() != null)
                ? cmbFiltroEstado.getValue()
                : "Todos";

        filtroProductos.setPredicate(p -> {
            boolean coincideTexto = true;
            if (!texto.isBlank()) {
                boolean coincideCodigo = p.getCodigo() != null && p.getCodigo().toLowerCase().contains(texto);
                boolean coincideNombre = p.getNombre() != null && p.getNombre().toLowerCase().contains(texto);
                coincideTexto = coincideCodigo || coincideNombre;
            }

            boolean coincideCategoria = true;
            if (catFiltro != null && catFiltro.getId() != null) {
                coincideCategoria = p.getCategoria() != null && catFiltro.getId().equals(p.getCategoria().getId());
            }

            boolean coincideEstado = true;
            if ("Activos".equals(estado)) {
                coincideEstado = p.isActivo();
            } else if ("Inactivos".equals(estado)) {
                coincideEstado = !p.isActivo();
            }

            return coincideTexto && coincideCategoria && coincideEstado;
        });
    }

    @FXML
    private void buscar() {
        aplicarFiltros();
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int existencia = Integer.parseInt(txtExistencia.getText().trim());

            if (precio.signum() <= 0 || existencia < 0) {
                mensaje(Alert.AlertType.WARNING, "Precio debe ser mayor a 0 y existencia no negativa.");
                return;
            }

            String nombre = txtNombre.getText().trim();

            if (productoDAO.existeNombre(nombre)) {
                mensaje(Alert.AlertType.WARNING, "Ya existe un producto registrado con el nombre \"" + nombre + "\".");
                txtNombre.requestFocus();
                return;
            }

            Producto nuevo = new Producto(
                    null,
                    txtCodigo.getText().trim(),
                    nombre,
                    cmbCategoria.getValue(),
                    precio,
                    existencia,
                    rutaImagen,
                    chkActivo.isSelected()
            );

            productoDAO.guardar(nuevo);
            mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
            cargarProductos();
            limpiar();

        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "Error al guardar en base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Debe seleccionar un producto de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Está seguro de que desea eliminar el producto \"" + seleccionado.getNombre() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                try {
                    productoDAO.eliminar(seleccionado.getId());
                    mensaje(Alert.AlertType.INFORMATION, "Producto eliminado correctamente.");
                    cargarProductos();
                    limpiar();
                } catch (SQLException e) {
                    mensaje(Alert.AlertType.ERROR, "Error al eliminar producto: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void cerrar() {
        ((Stage) txtCodigo.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}