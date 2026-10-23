package reservas.data;

import reservas.logic.Categoria;
import reservas.logic.Recurso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcRecursoDao implements RecursoDao {
    private static final String SELECT_RECURSO = "SELECT r.id AS recurso_id, r.descripcion AS recurso_descripcion, "
            + "c.id AS categoria_id, c.descripcion AS categoria_descripcion "
            + "FROM recurso r JOIN categoria c ON c.id = r.categoria_id ";

    private final Database database;

    public JdbcRecursoDao(Database database) {
        this.database = database;
    }

    @Override
    public Recurso buscarPorId(String id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RECURSO + "WHERE r.id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapear(resultSet) : null;
            }
        }
    }

    @Override
    public List<Recurso> listar() throws SQLException {
        return listar(SELECT_RECURSO + "ORDER BY r.id", null);
    }

    @Override
    public List<Recurso> listarPorCategoria(String categoriaId) throws SQLException {
        return listar(SELECT_RECURSO + "WHERE r.categoria_id = ? ORDER BY r.id", categoriaId);
    }

    @Override
    public void insertar(Recurso recurso) throws SQLException {
        String sql = "INSERT INTO recurso (id, categoria_id, descripcion) VALUES (?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, recurso.getId());
            statement.setString(2, recurso.getCategoria().getId());
            statement.setString(3, recurso.getDescripcion());
            statement.executeUpdate();
        }
    }

    @Override
    public void actualizar(Recurso recurso) throws SQLException {
        String sql = "UPDATE recurso SET categoria_id = ?, descripcion = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, recurso.getCategoria().getId());
            statement.setString(2, recurso.getDescripcion());
            statement.setString(3, recurso.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void eliminar(String id) throws SQLException {
        ejecutarConId("DELETE FROM recurso WHERE id = ?", id);
    }

    @Override
    public boolean tieneReservas(String id) throws SQLException {
        return existe("SELECT 1 FROM reserva_recurso WHERE recurso_id = ?", id);
    }

    private List<Recurso> listar(String sql, String categoriaId) throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (categoriaId != null) {
                statement.setString(1, categoriaId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    recursos.add(mapear(resultSet));
                }
            }
        }
        return recursos;
    }

    private void ejecutarConId(String sql, String id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    private boolean existe(String sql, String id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Recurso mapear(ResultSet resultSet) throws SQLException {
        Categoria categoria = new Categoria(resultSet.getString("categoria_id"),
                resultSet.getString("categoria_descripcion"));
        return new Recurso(resultSet.getString("recurso_id"), categoria,
                resultSet.getString("recurso_descripcion"));
    }
}
