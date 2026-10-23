package reservas.data;

import reservas.logic.Funcionario;
import reservas.logic.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcUsuarioDao implements UsuarioDao {
    private final Database database;

    public JdbcUsuarioDao(Database database) {
        this.database = database;
    }

    @Override
    public Usuario buscarPorId(String id) throws SQLException {
        String sql = "SELECT id, clave, rol, nombre, telefono FROM usuario WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapear(resultSet) : null;
            }
        }
    }

    @Override
    public List<Funcionario> listarFuncionarios() throws SQLException {
        String sql = "SELECT id, clave, rol, nombre, telefono FROM usuario WHERE rol = ? ORDER BY id";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Usuario.FUNCIONARIO);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    funcionarios.add((Funcionario) mapear(resultSet));
                }
            }
        }
        return funcionarios;
    }

    @Override
    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (id, clave, rol, nombre, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getId());
            statement.setString(2, usuario.getClave());
            statement.setString(3, usuario.getRol());
            if (usuario instanceof Funcionario funcionario) {
                statement.setString(4, funcionario.getNombre());
                statement.setString(5, funcionario.getTelefono());
            } else {
                statement.setNull(4, java.sql.Types.VARCHAR);
                statement.setNull(5, java.sql.Types.VARCHAR);
            }
            statement.executeUpdate();
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        boolean esFuncionario = usuario instanceof Funcionario;
        String sql = esFuncionario
                ? "UPDATE usuario SET clave = ?, nombre = ?, telefono = ? WHERE id = ?"
                : "UPDATE usuario SET clave = ? WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getClave());
            if (esFuncionario) {
                Funcionario funcionario = (Funcionario) usuario;
                statement.setString(2, funcionario.getNombre());
                statement.setString(3, funcionario.getTelefono());
                statement.setString(4, funcionario.getId());
            } else {
                statement.setString(2, usuario.getId());
            }
            statement.executeUpdate();
        }
    }

    @Override
    public void eliminar(String id) throws SQLException {
        ejecutarConId("DELETE FROM usuario WHERE id = ?", id);
    }

    @Override
    public boolean tieneReservas(String id) throws SQLException {
        return existe("SELECT 1 FROM reserva WHERE funcionario_id = ?", id);
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

    private Usuario mapear(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String clave = resultSet.getString("clave");
        String rol = resultSet.getString("rol");
        if (Usuario.FUNCIONARIO.equals(rol)) {
            return new Funcionario(id, clave, resultSet.getString("nombre"), resultSet.getString("telefono"));
        }
        return new Usuario(id, clave, rol);
    }
}
