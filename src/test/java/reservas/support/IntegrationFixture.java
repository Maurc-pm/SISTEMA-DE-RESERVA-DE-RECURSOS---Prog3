package reservas.support;

import reservas.logic.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Solo datos de prueba; todos se crean mediante las operaciones públicas reales del Service. */
public final class IntegrationFixture {
    public final Service service = Service.instance();
    public final Funcionario ana = new Funcionario("F1", "ignorada", "Ana Pérez", "8888-0000");
    public final Categoria sala = new Categoria("", "Sala & Proyección");
    public final Recurso r1 = new Recurso("R1", sala, "Sala Norte");
    public final Recurso r2 = new Recurso("R2", sala, "Sala Sur");
    public static final LocalDate DATE = LocalDate.of(2026, 9, 17);

    public IntegrationFixture() throws Exception {
        service.crearFuncionario(ana); service.crearCategoria(sala);
        service.crearRecurso(r1); service.crearRecurso(r2);
    }
    public Reserva reservar(String actividad, String inicio, String fin) throws Exception {
        Reserva r = new Reserva("", ana, actividad, DATE, LocalTime.parse(inicio), LocalTime.parse(fin), List.of(), Reserva.ACTIVA);
        service.crearReserva(r, List.of(sala));
        return r;
    }
}
