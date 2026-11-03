package reservas.data;

import reservas.logic.*;

public interface Persistencia {
    Data cargar() throws Exception;
    void insertarUsuario(Usuario usuario) throws Exception;
    void actualizarUsuario(Usuario usuario) throws Exception;
    void eliminarUsuario(String id) throws Exception;
    void insertarCategoria(Categoria categoria) throws Exception;
    void actualizarCategoria(Categoria categoria) throws Exception;
    void eliminarCategoria(String id) throws Exception;
    void insertarRecurso(Recurso recurso) throws Exception;
    void actualizarRecurso(Recurso recurso) throws Exception;
    void eliminarRecurso(String id) throws Exception;
    void insertarReserva(Reserva reserva) throws Exception;
    void actualizarEstadoReserva(String id, String estado) throws Exception;
}
