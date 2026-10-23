package reservas.client;

import reservas.ai.ReservaExtraccion;
import reservas.logic.*;
import reservas.protocol.*;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ServiceProxy implements IService {
    private static final long TIMEOUT_SEGUNDOS = 60;
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final LinkedBlockingQueue<Response> respuestas = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<Consumer<Reserva>> listenersReservas = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Runnable> listenersDesconexion = new CopyOnWriteArrayList<>();
    private volatile boolean conectado = true;

    public ServiceProxy(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            try { socket.close(); } catch (IOException ignored) { }
            throw e;
        }
        Thread lector = new Thread(this::leer, "reservas-proxy-reader");
        lector.setDaemon(true);
        lector.start();
    }

    private void leer() {
        try {
            while (conectado) {
                Object mensaje = in.readObject();
                if (mensaje instanceof Response response) {
                    respuestas.offer(response);
                } else if (mensaje instanceof Notification notification) {
                    for (Consumer<Reserva> listener : listenersReservas) {
                        SwingUtilities.invokeLater(() -> listener.accept(notification.getReserva()));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            desconectar();
        }
    }

    private synchronized void desconectar() {
        if (!conectado) return;
        conectado = false;
        respuestas.offer(Response.error("Se perdió la conexión con el servidor"));
        for (Runnable listener : listenersDesconexion) {
            SwingUtilities.invokeLater(listener);
        }
    }

    private synchronized Object llamar(Operacion operacion, Object... argumentos) throws Exception {
        if (!conectado) throw new Exception("No hay conexión con el servidor");
        try {
            out.writeObject(new Request(operacion, argumentos));
            out.flush();
            out.reset();
        } catch (IOException e) {
            desconectar();
            throw new Exception("Se perdió la conexión con el servidor", e);
        }
        Response response = respuestas.poll(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        if (response == null) throw new Exception("El servidor no respondió");
        if (!response.isOk()) throw new Exception(response.getMensajeError());
        return response.getValor();
    }

    public void agregarListenerReservas(Consumer<Reserva> listener) { listenersReservas.add(listener); }
    public void quitarListenerReservas(Consumer<Reserva> listener) { listenersReservas.remove(listener); }
    public void agregarListenerDesconexion(Runnable listener) {
        listenersDesconexion.add(listener);
        if (!conectado) SwingUtilities.invokeLater(listener);
    }

    public void cerrar() {
        try { socket.close(); } catch (IOException ignored) { }
    }

    @Override public Usuario login(String id, String clave) throws Exception { return (Usuario) llamar(Operacion.LOGIN, id, clave); }
    @Override public Usuario buscarUsuarioPorId(String id) throws Exception { return (Usuario) llamar(Operacion.BUSCAR_USUARIO_POR_ID, id); }
    @Override public void cambiarClave(Usuario usuario, String claveActual, String claveNueva, String confirmarClave) throws Exception {
        llamar(Operacion.CAMBIAR_CLAVE, usuario, claveActual, claveNueva, confirmarClave);
        usuario.setClave(claveNueva);
    }
    @Override public void crearFuncionario(Funcionario funcionario) throws Exception {
        Funcionario actualizado = (Funcionario) llamar(Operacion.CREAR_FUNCIONARIO, funcionario);
        if (actualizado != null) funcionario.setId(actualizado.getId());
    }
    @Override public Funcionario buscarFuncionarioPorId(String id) throws Exception { return (Funcionario) llamar(Operacion.BUSCAR_FUNCIONARIO_POR_ID, id); }
    @SuppressWarnings("unchecked") @Override public List<Funcionario> buscarFuncionarios(String id, String nombre) throws Exception { return (List<Funcionario>) llamar(Operacion.BUSCAR_FUNCIONARIOS, id, nombre); }
    @SuppressWarnings("unchecked") @Override public List<Funcionario> listarFuncionarios() throws Exception { return (List<Funcionario>) llamar(Operacion.LISTAR_FUNCIONARIOS); }
    @Override public void modificarFuncionario(Funcionario funcionario) throws Exception { llamar(Operacion.MODIFICAR_FUNCIONARIO, funcionario); }
    @Override public void eliminarFuncionario(String id) throws Exception { llamar(Operacion.ELIMINAR_FUNCIONARIO, id); }
    @Override public void crearCategoria(Categoria categoria) throws Exception {
        Categoria actualizado = (Categoria) llamar(Operacion.CREAR_CATEGORIA, categoria);
        if (actualizado != null) categoria.setId(actualizado.getId());
    }
    @Override public Categoria buscarCategoriaPorId(String id) throws Exception { return (Categoria) llamar(Operacion.BUSCAR_CATEGORIA_POR_ID, id); }
    @SuppressWarnings("unchecked") @Override public List<Categoria> buscarCategorias(String descripcion) throws Exception { return (List<Categoria>) llamar(Operacion.BUSCAR_CATEGORIAS, descripcion); }
    @SuppressWarnings("unchecked") @Override public List<Categoria> listarCategorias() throws Exception { return (List<Categoria>) llamar(Operacion.LISTAR_CATEGORIAS); }
    @Override public void modificarCategoria(Categoria categoria) throws Exception { llamar(Operacion.MODIFICAR_CATEGORIA, categoria); }
    @Override public void eliminarCategoria(String id) throws Exception { llamar(Operacion.ELIMINAR_CATEGORIA, id); }
    @Override public void crearRecurso(Recurso recurso) throws Exception {
        Recurso actualizado = (Recurso) llamar(Operacion.CREAR_RECURSO, recurso);
        if (actualizado != null) recurso.setId(actualizado.getId());
    }
    @Override public Recurso buscarRecursoPorId(String id) throws Exception { return (Recurso) llamar(Operacion.BUSCAR_RECURSO_POR_ID, id); }
    @SuppressWarnings("unchecked") @Override public List<Recurso> listarRecursos() throws Exception { return (List<Recurso>) llamar(Operacion.LISTAR_RECURSOS); }
    @SuppressWarnings("unchecked") @Override public List<Recurso> buscarRecursosPorCategoria(Categoria categoria) throws Exception { return (List<Recurso>) llamar(Operacion.BUSCAR_RECURSOS_POR_CATEGORIA, categoria); }
    @Override public void modificarRecurso(Recurso recurso) throws Exception { llamar(Operacion.MODIFICAR_RECURSO, recurso); }
    @Override public void eliminarRecurso(String id) throws Exception { llamar(Operacion.ELIMINAR_RECURSO, id); }
    @Override public ReservaExtraccion extraerReservaConIA(String frase) throws Exception { return (ReservaExtraccion) llamar(Operacion.EXTRAER_RESERVA_CON_IA, frase); }
    @SuppressWarnings("unchecked") @Override public List<Reserva> listarReservas() throws Exception { return (List<Reserva>) llamar(Operacion.LISTAR_RESERVAS); }
    @SuppressWarnings("unchecked") @Override public List<Reserva> listarReservasFuncionario(Funcionario funcionario) throws Exception { return (List<Reserva>) llamar(Operacion.LISTAR_RESERVAS_FUNCIONARIO, funcionario); }
    @Override public void crearReserva(Reserva reserva, List<Categoria> categorias) throws Exception {
        Reserva actualizado = (Reserva) llamar(Operacion.CREAR_RESERVA, reserva, categorias);
        if (actualizado != null) {
            reserva.setId(actualizado.getId());
            reserva.setRecursos(actualizado.getRecursos());
            reserva.setEstado(actualizado.getEstado());
        }
    }
    @Override public void cancelarReserva(Reserva reserva) throws Exception {
        Reserva actualizado = (Reserva) llamar(Operacion.CANCELAR_RESERVA, reserva);
        if (actualizado != null) reserva.setEstado(actualizado.getEstado());
    }
}
