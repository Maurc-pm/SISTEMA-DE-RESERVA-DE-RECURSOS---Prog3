package reservas.data;

import reservas.logic.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCategoriaDao implements CategoriaDao {
    private final Database database;

    public JdbcCategoriaDao(Database database) {
        this.database = database;
    }

    @Override
    public Categoria buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, descripcion FROM categoria WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapear(resultSet) : null;
            }
        }
    }

    @Override
    public List<Categoria> listar() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, descripcion FROM categoria ORDER BY id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categorias.add(mapear(resultSet));
            }
        }
        return categorias;
    }

    @Override
    public void insertar(Categoria categoria) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO categoria (id, descripcion) VALUES (?, ?)")) {
            statement.setString(1, categoria.getId());
            statement.setString(2, categoria.getDescripcion());
            statement.executeUpdate();
        }
    }

    @Override
    public void actualizar(Categoria categoria) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE categoria SET descripcion = ? WHERE id = ?")) {
            statement.setString(1, categoria.getDescripcion());
            statement.setString(2, categoria.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void eliminar(String id) throws SQLException {
        ejecutarConId("DELETE FROM categoria WHERE id = ?", id);
    }

    @Override
    public boolean tieneRecursos(String id) throws SQLException {
        return existe("SELECT 1 FROM recurso WHERE categoria_id = ?", id);
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

    private Categoria mapear(ResultSet resultSet) throws SQLException {
        return new Categoria(resultSet.getString("id"), resultSet.getString("descripcion"));
    }
}
