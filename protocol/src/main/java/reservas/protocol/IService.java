package reservas.protocol;

import reservas.ai.ReservaExtraccion;
import reservas.logic.*;
import java.util.List;

public interface IService {
    Usuario login(String id, String clave) throws Exception;
    Usuario buscarUsuarioPorId(String id) throws Exception;
    void cambiarClave(Usuario usuario, String claveActual, String claveNueva, String confirmarClave) throws Exception;
    void crearFuncionario(Funcionario funcionario) throws Exception;
    Funcionario buscarFuncionarioPorId(String id) throws Exception;
    List<Funcionario> buscarFuncionarios(String id, String nombre) throws Exception;
    List<Funcionario> listarFuncionarios() throws Exception;
    void modificarFuncionario(Funcionario funcionario) throws Exception;
    void eliminarFuncionario(String id) throws Exception;
    void crearCategoria(Categoria categoria) throws Exception;
    Categoria buscarCategoriaPorId(String id) throws Exception;
    List<Categoria> buscarCategorias(String descripcion) throws Exception;
    List<Categoria> listarCategorias() throws Exception;
    void modificarCategoria(Categoria categoria) throws Exception;
    void eliminarCategoria(String id) throws Exception;
    void crearRecurso(Recurso recurso) throws Exception;
    Recurso buscarRecursoPorId(String id) throws Exception;
    List<Recurso> listarRecursos() throws Exception;
    List<Recurso> buscarRecursosPorCategoria(Categoria categoria) throws Exception;
    void modificarRecurso(Recurso recurso) throws Exception;
    void eliminarRecurso(String id) throws Exception;
    ReservaExtraccion extraerReservaConIA(String frase) throws Exception;
    List<Reserva> listarReservas() throws Exception;
    List<Reserva> listarReservasFuncionario(Funcionario funcionario) throws Exception;
    void crearReserva(Reserva reserva, List<Categoria> categorias) throws Exception;
    void cancelarReserva(Reserva reserva) throws Exception;
}
