package reservas.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.client.ServiceProxy;
import reservas.data.MySqlPersistencia;
import reservas.logic.*;
import reservas.presentation.Servicio;
import reservas.server.Server;
import reservas.support.TestDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ServiceProxyIT {
    private Service service;
    private Funcionario funcionario;
    private Categoria categoria;
    private Server server;
    private ServiceProxy proxyA;
    private ServiceProxy proxyB;

    @BeforeEach
    void setUp() throws Exception {
        TestDatabase.resetSchema();
        service = Service.crearParaPruebas(new MySqlPersistencia(TestDatabase.database()));
        Servicio.set(service);
        funcionario = new Funcionario("FUNC1", "clave", "Funcionario Uno", "8888-8888");
        service.crearFuncionario(funcionario);
        categoria = new Categoria("", "Sala");
        service.crearCategoria(categoria);
        service.crearRecurso(new Recurso("REC1", categoria, "Sala principal"));
        server = new Server(0, service);
        server.iniciar();
        proxyA = proxy();
        proxyB = proxy();
    }

    @AfterEach
    void tearDown() {
        if (proxyA != null) proxyA.cerrar();
        if (proxyB != null) proxyB.cerrar();
        if (server != null) server.detener();
        Servicio.set(null);
    }

    @Test
    void loginDevuelveElAdministrador() throws Exception {
        Usuario usuario = proxyA.login("admin", "admin");
        assertEquals("admin", usuario.getId());
        assertEquals(Usuario.ADMINISTRADOR, usuario.getRol());
    }

    @Test
    void crearReservaActualizaObjetoYNotificaSoloAlOtroProxy() throws Exception {
        CountDownLatch notificada = new CountDownLatch(1);
        CountDownLatch notificadaPorA = new CountDownLatch(1);
        AtomicReference<Reserva> recibida = new AtomicReference<>();
        AtomicReference<Reserva> recibidaPorA = new AtomicReference<>();
        proxyB.agregarListenerReservas(reserva -> { recibida.set(reserva); notificada.countDown(); });
        proxyA.agregarListenerReservas(reserva -> { recibidaPorA.set(reserva); notificadaPorA.countDown(); });

        Reserva local = reserva("Reserva proxy");
        proxyA.crearReserva(local, List.of(categoria));

        assertTrue(local.getId().startsWith("RES-"));
        assertEquals(1, local.getRecursos().size());
        assertTrue(notificada.await(5, TimeUnit.SECONDS));
        assertEquals(local.getId(), recibida.get().getId());
        assertFalse(notificadaPorA.await(300, TimeUnit.MILLISECONDS));
        assertNull(recibidaPorA.get());
    }

    @Test
    void propagaErrorDelServiceYElProxySigueDisponible() throws Exception {
        Exception error = assertThrows(Exception.class,
                () -> proxyA.crearReserva(reserva(" "), List.of(categoria)));
        assertEquals("La actividad es requerida", error.getMessage());
        assertEquals("admin", proxyA.login("admin", "admin").getId());
    }

    @Test
    void llamadasConcurrentesEnUnProxyConservanLosTiposDeRetorno() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> categorias = pool.submit(() -> {
                try {
                    for (int i = 0; i < 20; i++) assertInstanceOf(Categoria.class, proxyA.listarCategorias().get(0));
                } catch (Exception e) { throw new CompletionException(e); }
            });
            Future<?> reservas = pool.submit(() -> {
                try {
                    for (int i = 0; i < 20; i++) assertInstanceOf(List.class, proxyA.listarReservas());
                } catch (Exception e) { throw new CompletionException(e); }
            });
            categorias.get(10, TimeUnit.SECONDS);
            reservas.get(10, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void servidorDetenidoNotificaDesconexionYDesbloqueaLlamadas() throws Exception {
        CountDownLatch desconectado = new CountDownLatch(1);
        proxyA.agregarListenerDesconexion(desconectado::countDown);
        server.detener();
        assertTrue(desconectado.await(5, TimeUnit.SECONDS));
        Exception error = assertThrows(Exception.class, () -> proxyA.listarCategorias());
        assertTrue(error.getMessage().contains("conexión") || error.getMessage().contains("respondió"));
    }

    private ServiceProxy proxy() throws Exception {
        return new ServiceProxy("127.0.0.1", server.getPuerto());
    }

    private Reserva reserva(String actividad) {
        return new Reserva("", funcionario, actividad, LocalDate.now().plusDays(5),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), Reserva.ACTIVA);
    }
}
