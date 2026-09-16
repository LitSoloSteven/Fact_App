package ni.edu.ni.uam.fact_app.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ni.edu.ni.uam.fact_app.model.Cargo;
import ni.edu.ni.uam.fact_app.model.Categoria;
import ni.edu.ni.uam.fact_app.model.Producto;

public final class DataStore {

    private static final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private static final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private static final ObservableList<Cargo> cargos = FXCollections.observableArrayList();

    static {
        // Datos iniciales precargados
        cargos.addAll(
                new Cargo(1, "Administrador", "Acceso total al sistema"),
                new Cargo(2, "Cajero", "Gestión de facturas y cobros")
        );

        categorias.addAll(
                new Categoria(1, "Alimentos", true),
                new Categoria(2, "Bebidas", true),
                new Categoria(3, "Limpieza", true)
        );
    }

    private DataStore() {}

    public static ObservableList<Producto> getProductos() { return productos; }
    public static ObservableList<Categoria> getCategorias() { return categorias; }
    public static ObservableList<Cargo> getCargos() { return cargos; }
}