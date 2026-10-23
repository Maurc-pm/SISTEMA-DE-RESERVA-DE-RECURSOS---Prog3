package reservas.data;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservaDao implements ReservaDao {
    private static final String SELECT_RESERVA = "SELECT r.id AS reserva_id, r.actividad, r.fecha, r.hora_inicio, "
            + "r.hora_fin, r.estado, u.id AS funcionario_id, u.clave AS funcionario_clave, "
            + "u.nombre AS funcionario_nombre, u.telefono AS funcionario_telefono "
            + "FROM reserva r JOIN usuario u ON u.id = r.funcionario_id ";
    private static final String ORDER = " ORDER BY r.fecha, r.hora_inicio, r.id";

    private final Database database;

    public JdbcReservaDao(Database database) {
        this.database = database;
    }

    @Override
    public Reserva buscarPorId(String id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RESERVA + "WHERE r.id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapear(connection, resultSet) : null;
            }
        }
    }

    @Override
    public List<Reserva> listar() throws SQLException {
        return listar(SELECT_RESERVA + ORDER, null);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String funcionarioId) throws SQLException {
        return listar(SELECT_RESERVA + "WHERE r.funcionario_id = ?" + ORDER, funcionarioId);
    }

    @Override
    public void insertar(Reserva reserva) throws SQLException {
        String reservaSql = "INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String recursoSql = "INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (?, ?)";
        try (Connection connection = database.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement reservaStatement = connection.prepareStatement(reservaSql);
                     PreparedStatement recursoStatement = connection.prepareStatement(recursoSql)) {
                    reservaStatement.setString(1, reserva.getId());
                    reservaStatement.setString(2, reserva.getFuncionario().getId());
                    reservaStatement.setString(3, reserva.getActividad());
                    reservaStatement.setDate(4, java.sql.Date.valueOf(reserva.getFecha()));
                    reservaStatement.setTime(5, java.sql.Time.valueOf(reserva.getHoraInicio()));
                    reservaStatement.setTime(6, java.sql.Time.valueOf(reserva.getHoraFin()));
                    reservaStatement.setString(7, reserva.getEstado());
                    reservaStatement.executeUpdate();

                    for (Recurso recurso : reserva.getRecursos()) {
                        recursoStatement.setString(1, reserva.getId());
                        recursoStatement.setString(2, recurso.getId());
                        recursoStatement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    @Override
    public void actualizarEstado(String id, String estado) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE reserva SET estado = ? WHERE id = ?")) {
            statement.setString(1, estado);
            statement.setString(2, id);
            statement.executeUpdate();
        }
    }

    private List<Reserva> listar(String sql, String funcionarioId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (funcionarioId != null) {
                statement.setString(1, funcionarioId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservas.add(mapear(connection, resultSet));
                }
            }
        }
        return reservas;
    }

    private Reserva mapear(Connection connection, ResultSet resultSet) throws SQLException {
        Funcionario funcionario = new Funcionario(resultSet.getString("funcionario_id"),
                resultSet.getString("funcionario_clave"), resultSet.getString("funcionario_nombre"),
                resultSet.getString("funcionario_telefono"));
        String reservaId = resultSet.getString("reserva_id");
        return new Reserva(reservaId, funcionario, resultSet.getString("actividad"),
                resultSet.getDate("fecha").toLocalDate(), resultSet.getTime("hora_inicio").toLocalTime(),
                resultSet.getTime("hora_fin").toLocalTime(), cargarRecursos(connection, reservaId),
                resultSet.getString("estado"));
    }

    private List<Recurso> cargarRecursos(Connection connection, String reservaId) throws SQLException {
        String sql = "SELECT r.id AS recurso_id, r.descripcion AS recurso_descripcion, c.id AS categoria_id, "
                + "c.descripcion AS categoria_descripcion FROM reserva_recurso rr "
                + "JOIN recurso r ON r.id = rr.recurso_id JOIN categoria c ON c.id = r.categoria_id "
                + "WHERE rr.reserva_id = ? ORDER BY r.id";
        List<Recurso> recursos = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reservaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Categoria categoria = new Categoria(resultSet.getString("categoria_id"),
                            resultSet.getString("categoria_descripcion"));
                    recursos.add(new Recurso(resultSet.getString("recurso_id"), categoria,
                            resultSet.getString("recurso_descripcion")));
                }
            }
        }
        return recursos;
    }
}
