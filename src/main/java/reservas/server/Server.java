package reservas.server;

import reservas.logic.Reserva;
import reservas.protocol.IService;
import reservas.protocol.Notification;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Server {
    private final int puertoSolicitado;
    private final IService service;
    private final CopyOnWriteArrayList<Worker> workers = new CopyOnWriteArrayList<>();
    private volatile ServerSocket serverSocket;
    private volatile Thread acceptThread;

    public Server(int puerto, IService service) {
        this.puertoSolicitado = puerto;
        this.service = service;
    }

    public synchronized void iniciar() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) return;
        serverSocket = new ServerSocket(puertoSolicitado);
        acceptThread = new Thread(this::aceptarClientes, "reservas-server-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void aceptarClientes() {
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                Worker worker = new Worker(this, socket, service);
                workers.add(worker);
                System.out.println("Cliente conectado");
                Thread hilo = new Thread(worker, "reservas-client-" + socket.getPort());
                hilo.setDaemon(true);
                hilo.start();
            } catch (IOException e) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    System.out.println("Error aceptando cliente: " + e.getClass().getSimpleName());
                }
                break;
            }
        }
    }

    public int getPuerto() {
        ServerSocket socket = serverSocket;
        return socket == null ? puertoSolicitado : socket.getLocalPort();
    }

    public void detener() {
        ServerSocket socket = serverSocket;
        serverSocket = null;
        if (socket != null) {
            try { socket.close(); } catch (IOException ignored) { }
        }
        for (Worker worker : workers) {
            quitar(worker);
            worker.cerrar();
        }
    }

    public void notificarOtros(Worker origen, Reserva reserva) {
        Notification notification = new Notification(reserva);
        for (Worker worker : workers) {
            if (worker != origen) {
                try {
                    worker.enviar(notification);
                } catch (IOException e) {
                    System.out.println("Error notificando cliente: " + e.getClass().getSimpleName());
                    quitar(worker);
                    worker.cerrar();
                }
            }
        }
    }

    public void quitar(Worker worker) {
        if (workers.remove(worker)) System.out.println("Cliente desconectado");
    }
}
