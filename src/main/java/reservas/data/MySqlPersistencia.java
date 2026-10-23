package reservas.data;

import reservas.logic.*;

public class MySqlPersistencia implements Persistencia {
    private final UsuarioDao usuarios; private final CategoriaDao categorias; private final RecursoDao recursos; private final ReservaDao reservas;
    public MySqlPersistencia(Database database) { usuarios = new JdbcUsuarioDao(database); categorias = new JdbcCategoriaDao(database); recursos = new JdbcRecursoDao(database); reservas = new JdbcReservaDao(database); }
    public Data cargar() throws Exception {
        Data data = new Data(); data.getUsuarios().addAll(usuarios.listar()); data.getCategorias().addAll(categorias.listar()); data.getRecursos().addAll(recursos.listar()); data.getReservas().addAll(reservas.listar());
        for (Recurso r : data.getRecursos()) r.setCategoria(data.getCategorias().stream().filter(c -> c.getId().equals(r.getCategoria().getId())).findFirst().orElse(r.getCategoria()));
        for (Reserva r : data.getReservas()) { r.setFuncionario((Funcionario) data.getUsuarios().stream().filter(u -> u instanceof Funcionario && u.getId().equals(r.getFuncionario().getId())).findFirst().orElse(r.getFuncionario())); for (Recurso x : r.getRecursos()) x.setCategoria(data.getCategorias().stream().filter(c -> c.getId().equals(x.getCategoria().getId())).findFirst().orElse(x.getCategoria())); r.setRecursos(r.getRecursos().stream().map(x -> data.getRecursos().stream().filter(y -> y.getId().equals(x.getId())).findFirst().orElse(x)).toList()); }
        return data;
    }
    public void insertarUsuario(Usuario u) throws Exception { usuarios.insertar(u); } public void actualizarUsuario(Usuario u) throws Exception { usuarios.actualizar(u); } public void eliminarUsuario(String id) throws Exception { usuarios.eliminar(id); }
    public void insertarCategoria(Categoria c) throws Exception { categorias.insertar(c); } public void actualizarCategoria(Categoria c) throws Exception { categorias.actualizar(c); } public void eliminarCategoria(String id) throws Exception { categorias.eliminar(id); }
    public void insertarRecurso(Recurso r) throws Exception { recursos.insertar(r); } public void actualizarRecurso(Recurso r) throws Exception { recursos.actualizar(r); } public void eliminarRecurso(String id) throws Exception { recursos.eliminar(id); }
    public void insertarReserva(Reserva r) throws Exception { reservas.insertar(r); } public void actualizarEstadoReserva(String id, String e) throws Exception { reservas.actualizarEstado(id, e); }
}
