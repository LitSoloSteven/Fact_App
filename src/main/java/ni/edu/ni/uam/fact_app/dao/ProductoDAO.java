package ni.edu.ni.uam.fact_app.dao;

import ni.edu.ni.uam.fact_app.model.Categoria;
import ni.edu.ni.uam.fact_app.model.Producto;
import ni.edu.ni.uam.fact_app.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements CrudDAO<Producto, Integer> {

    @Override
    public void guardar(Producto producto) throws SQLException {
        String sql = """
            INSERT INTO producto 
            (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Producto producto) throws SQLException {
        String sql = """
            UPDATE producto SET 
                codigo = ?, nombre = ?, categoria_id = ?, 
                precio_venta = ?, existencia = ?, ruta_imagen = ?, activo = ?
            WHERE id = ?
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCategoria().getId());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getExistencia());
            ps.setString(6, producto.getRutaImagen());
            ps.setBoolean(7, producto.isActivo());
            ps.setInt(8, producto.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    public List buscarPorCriterio(String criterio) throws SQLException {
        List lista = new ArrayList<>();
        String sql = """
            SELECT p.id, p.codigo, p.nombre, p.categoria_id, c.nombre AS cat_nombre, c.activa AS cat_activa,
                   p.precio_venta, p.existencia, p.ruta_imagen, p.activo
            FROM producto p
            INNER JOIN categoria c ON p.categoria_id = c.id
            WHERE LOWER(p.nombre) LIKE LOWER(?) OR LOWER(p.codigo) LIKE LOWER(?)
            ORDER BY p.id
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String filtro = "%" + criterio.trim() + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }
        }
        return lista;
    }
    @Override
    public Producto buscar(Integer id) throws SQLException {
        String sql = """
            SELECT p.id, p.codigo, p.nombre, p.categoria_id, c.nombre AS cat_nombre, c.activa AS cat_activa,
                   p.precio_venta, p.existencia, p.ruta_imagen, p.activo
            FROM producto p
            INNER JOIN categoria c ON p.categoria_id = c.id
            WHERE p.id = ?
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = """
            SELECT p.id, p.codigo, p.nombre, p.categoria_id, c.nombre AS cat_nombre, c.activa AS cat_activa,
                   p.precio_venta, p.existencia, p.ruta_imagen, p.activo
            FROM producto p
            INNER JOIN categoria c ON p.categoria_id = c.id
            ORDER BY p.id
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        }
        return lista;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Categoria cat = new Categoria(
                rs.getInt("categoria_id"),
                rs.getString("cat_nombre"),
                rs.getBoolean("cat_activa")
        );
        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                cat,
                rs.getBigDecimal("precio_venta"),
                rs.getInt("existencia"),
                rs.getString("ruta_imagen"),
                rs.getBoolean("activo")
        );
    }
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?))";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}