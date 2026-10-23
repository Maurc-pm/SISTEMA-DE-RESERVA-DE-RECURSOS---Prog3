package reservas.server;

import reservas.logic.Reserva;
import reservas.protocol.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public final class Worker implements Runnable {
    private final Server server;
    private final Socket socket;
    private final IService service;
    private final ObjectOutputStream out;

    public Worker(Server server, Socket socket, IService service) throws IOException {
        this.server = server;
        this.socket = socket;
        this.service = service;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                Object recibido = in.readObject();
                if (!(recibido instanceof Request request)) {
                    enviar(Response.error("Petición inválida"));
                    continue;
                }
                Response response = Despachador.atender(service, request);
                enviar(response);
                if (request.getOperacion() == Operacion.CREAR_RESERVA && response.isOk()) {
                    server.notificarOtros(this, (Reserva) response.getValor());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!socket.isClosed() && !(e instanceof EOFException)) {
                System.out.println("Error de conexión de cliente: " + e.getClass().getSimpleName());
            }
        } finally {
            server.quitar(this);
            try { socket.close(); } catch (IOException ignored) { }
        }
    }

    public synchronized void enviar(Mensaje mensaje) throws IOException {
        out.writeObject(mensaje);
        out.flush();
        out.reset();
    }

    void cerrar() {
        try { socket.close(); } catch (IOException ignored) { }
    }
}
