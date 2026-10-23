package reservas.presentation;

import reservas.protocol.IService;

public final class Servicio {
    private static volatile IService actual;

    private Servicio() { }

    public static IService get() {
        IService servicio = actual;
        if (servicio == null) throw new IllegalStateException("Servicio no inicializado");
        return servicio;
    }

    public static void set(IService servicio) {
        actual = servicio;
    }
}
