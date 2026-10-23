package reservas.server;

import reservas.logic.Service;
import reservas.protocol.Protocolo;

import java.util.concurrent.CountDownLatch;

public final class ServerApplication {
    private ServerApplication() { }

    public static void main(String[] args) throws Exception {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : Protocolo.PUERTO_POR_DEFECTO;
        Server server = new Server(puerto, Service.instance());
        server.iniciar();
        System.out.println("Servidor escuchando en el puerto " + server.getPuerto());
        new CountDownLatch(1).await();
    }
}
