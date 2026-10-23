package reservas.data;

import reservas.logic.Funcionario;
import reservas.logic.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDao {
    Usuario buscarPorId(String id) throws SQLException;
    List<Usuario> listar() throws SQLException;
    List<Funcionario> listarFuncionarios() throws SQLException;
    void insertar(Usuario usuario) throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void eliminar(String id) throws SQLException;
    boolean tieneReservas(String id) throws SQLException;
}
