package reservas.data;

import reservas.logic.Recurso;

import java.sql.SQLException;
import java.util.List;

public interface RecursoDao {
    Recurso buscarPorId(String id) throws SQLException;
    List<Recurso> listar() throws SQLException;
    List<Recurso> listarPorCategoria(String categoriaId) throws SQLException;
    void insertar(Recurso recurso) throws SQLException;
    void actualizar(Recurso recurso) throws SQLException;
    void eliminar(String id) throws SQLException;
    boolean tieneReservas(String id) throws SQLException;
}
