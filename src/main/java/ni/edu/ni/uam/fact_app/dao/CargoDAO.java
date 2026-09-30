package ni.edu.ni.uam.fact_app.dao;

import ni.edu.ni.uam.fact_app.model.Cargo;
import ni.edu.ni.uam.fact_app.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO implements CrudDAO<Cargo, Integer> {

    @Override
    public void guardar(Cargo cargo) throws SQLException {
        String sql = "INSERT INTO cargo (id, nombre, descripcion) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cargo.getId());
            ps.setString(2, cargo.getNombre());
            ps.setString(3, cargo.getDescripcion());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cargo cargo) throws SQLException {
        String sql = "UPDATE cargo SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cargo.getNombre());
            ps.setString(2, cargo.getDescripcion());
            ps.setInt(3, cargo.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws SQLException {
        String sql = "DELETE FROM cargo WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Cargo buscar(Integer id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion FROM cargo WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cargo(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"));
                }
            }
        }
        return null;
    }
    public List buscarPorCriterio(String criterio) throws SQLException {
        List lista = new ArrayList<>();
        String sql = """
            SELECT id, nombre, descripcion 
            FROM cargo 
            WHERE LOWER(nombre) LIKE LOWER(?) OR CAST(id AS TEXT) = ?
            ORDER BY id
            """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String texto = criterio.trim();
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, texto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cargo(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion")));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Cargo> listar() throws SQLException {
        List<Cargo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM cargo ORDER BY id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cargo(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return lista;
    }
}