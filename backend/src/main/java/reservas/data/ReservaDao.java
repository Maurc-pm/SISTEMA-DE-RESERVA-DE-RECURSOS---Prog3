package reservas.data;

import reservas.logic.Reserva;

import java.sql.SQLException;
import java.util.List;

public interface ReservaDao {
    Reserva buscarPorId(String id) throws SQLException;
    List<Reserva> listar() throws SQLException;
    List<Reserva> listarPorFuncionario(String funcionarioId) throws SQLException;
    void insertar(Reserva reserva) throws SQLException;
    void actualizarEstado(String id, String estado) throws SQLException;
}
