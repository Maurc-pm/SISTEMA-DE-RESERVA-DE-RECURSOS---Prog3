package reservas.protocol;

import reservas.logic.Reserva;

public final class Notification implements Mensaje {
    private static final long serialVersionUID = 1L;
    private final Reserva reserva;

    public Notification(Reserva reserva) { this.reserva = reserva; }
    public Reserva getReserva() { return reserva; }
}
