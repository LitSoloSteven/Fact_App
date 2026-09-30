package ni.edu.ni.uam.fact_app.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T, ID> {

    void guardar(T entidad) throws SQLException;

    void actualizar(T entidad) throws SQLException;

    void eliminar(ID id) throws SQLException;

    T buscar(ID id) throws SQLException;

    List<T> listar() throws SQLException;
}