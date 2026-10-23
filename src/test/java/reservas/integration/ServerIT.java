package reservas.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.data.MySqlPersistencia;
import reservas.logic.*;
import reservas.protocol.*;
import reservas.server.Server;
import reservas.support.TestDatabase;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerIT {
    private Service service;
    private Funcionario funcionario;
    private Categoria categoria;
    private Server server;

    @BeforeEach
    void setUp() throws Exception {
        TestDatabase.resetSchema();
        service = Service.crearParaPruebas(new MySqlPersistencia(TestDatabase.database()));
        funcionario = new Funcionario("FUNC1", "clave", "Funcionario Uno", "8888-8888");
        service.crearFuncionario(funcionario);
        categoria = new Categoria("", "Sala");
        service.crearCategoria(categoria);
        service.crearRecurso(new Recurso("REC1", categoria, "Sala principal"));
        server = new Server(0, service);
        server.iniciar();
    }

    @AfterEach
    void tearDown() {
        if (server != null) server.detener();
    }

    @Test
    void loginAdministradorRespondePorSocket() throws Exception {
        try (Client client = connect()) {
            client.send(new Request(Operacion.LOGIN, "admin", "admin"));
            Response response = client.response();
            assertTrue(response.isOk());
            assertInstanceOf(Usuario.class, response.getValor());
            Usuario usuario = (Usuario) response.getValor();
            assertEquals("admin", usuario.getId());
            assertEquals(Usuario.ADMINISTRADOR, usuario.getRol());
        }
    }

    @Test
    void reservaNotificaSoloALosOtrosClientes() throws Exception {
        try (Client a = connect(); Client b = connect()) {
            a.send(new Request(Operacion.CREAR_RESERVA, reserva("Reserva compartida"), List.of(categoria)));
            Response response = a.response();
            assertTrue(response.isOk());
            Reserva creada = (Reserva) response.getValor();
            assertFalse(creada.getId().isEmpty());

            Object recibido = b.read();
            assertInstanceOf(Notification.class, recibido);
            assertEquals(creada.getId(), ((Notification) recibido).getReserva().getId());
            a.socket.setSoTimeout(300);
            assertThrows(SocketTimeoutException.class, a::read);
        }
    }

    @Test
    void errorDeValidacionNoCierraLaConexion() throws Exception {
        try (Client client = connect()) {
            client.send(new Request(Operacion.CREAR_RESERVA, reserva(" "), List.of(categoria)));
            Response error = client.response();
            assertFalse(error.isOk());
            assertEquals("La actividad es requerida", error.getMensajeError());

            client.send(new Request(Operacion.LOGIN, "admin", "admin"));
            assertTrue(client.response().isOk());
        }
    }

    @Test
    void desconexionDeUnClienteNoImpideCrearOtraReserva() throws Exception {
        try (Client a = connect(); Client b = connect()) {
            b.close();
            Thread.sleep(100);
            a.send(new Request(Operacion.CREAR_RESERVA, reserva("Después de desconexión"), List.of(categoria)));
            Response response = a.response();
            assertTrue(response.isOk());
            assertFalse(((Reserva) response.getValor()).getId().isEmpty());
        }
    }

    @Test
    void dosClientesNoPuedenReservarElMismoRecursoAlMismoTiempo() throws Exception {
        try (Client a = connect(); Client b = connect()) {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            CountDownLatch start = new CountDownLatch(1);
            try {
                Future<Response> fa = pool.submit(() -> sendAfter(start, a));
                Future<Response> fb = pool.submit(() -> sendAfter(start, b));
                start.countDown();
                Response ra = fa.get(4, TimeUnit.SECONDS);
                Response rb = fb.get(4, TimeUnit.SECONDS);
                assertNotEquals(ra.isOk(), rb.isOk());
                Response error = ra.isOk() ? rb : ra;
                assertTrue(error.getMensajeError().startsWith("No hay recursos disponibles"));
            } finally {
                pool.shutdownNow();
            }
        }
    }

    private Response sendAfter(CountDownLatch start, Client client) throws Exception {
        start.await();
        client.send(new Request(Operacion.CREAR_RESERVA, reserva("Concurrente"), List.of(categoria)));
        Object value;
        do {
            value = client.read();
        } while (value instanceof Notification);
        assertInstanceOf(Response.class, value);
        return (Response) value;
    }

    private Reserva reserva(String actividad) {
        LocalDate fecha = LocalDate.now().plusDays(5);
        return new Reserva("", funcionario, actividad, fecha, LocalTime.of(9, 0), LocalTime.of(10, 0),
                List.of(), Reserva.ACTIVA);
    }

    private Client connect() throws IOException {
        return new Client(new Socket("127.0.0.1", server.getPuerto()));
    }

    private static final class Client implements AutoCloseable {
        private final Socket socket;
        private final ObjectOutputStream out;
        private final ObjectInputStream in;

        private Client(Socket socket) throws IOException {
            this.socket = socket;
            socket.setSoTimeout(3000);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }

        void send(Request request) throws IOException {
            out.writeObject(request);
            out.flush();
            out.reset();
        }

        Object read() throws IOException, ClassNotFoundException { return in.readObject(); }
        Response response() throws IOException, ClassNotFoundException {
            Object value = read();
            assertInstanceOf(Response.class, value);
            return (Response) value;
        }
        @Override public void close() throws IOException { socket.close(); }
    }
}
