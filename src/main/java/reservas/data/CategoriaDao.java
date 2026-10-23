package reservas.data;

import reservas.logic.Categoria;

import java.sql.SQLException;
import java.util.List;

public interface CategoriaDao {
    Categoria buscarPorId(String id) throws SQLException;
    List<Categoria> listar() throws SQLException;
    void insertar(Categoria categoria) throws SQLException;
    void actualizar(Categoria categoria) throws SQLException;
    void eliminar(String id) throws SQLException;
    boolean tieneRecursos(String id) throws SQLException;
}
