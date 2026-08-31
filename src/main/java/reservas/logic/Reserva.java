package reservas.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reserva {

    public static final String ACTIVA = "ACTIVA";
    public static final String CANCELADA = "CANCELADA";

    private String id;
    private Funcionario funcionario;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<Recurso> recursos;
    private String estado;

    public Reserva() {
        this.id = "";
        this.funcionario = null;
        this.actividad = "";
        this.fecha = null;
        this.horaInicio = null;
        this.horaFin = null;
        this.recursos = new ArrayList<>();
        this.estado = ACTIVA;
    }

    public Reserva(
            String id,
            Funcionario funcionario,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<Recurso> recursos,
            String estado
    ) {
        this.id = id;
        this.funcionario = funcionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursos = recursos != null
                ? new ArrayList<>(recursos)
                : new ArrayList<>();
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos != null
                ? new ArrayList<>(recursos)
                : new ArrayList<>();
    }

    public void agregarRecurso(Recurso recurso) {
        if (recurso != null && !recursos.contains(recurso)) {
            recursos.add(recurso);
        }
    }

    public void eliminarRecurso(Recurso recurso) {
        recursos.remove(recurso);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean estaActiva() {
        return ACTIVA.equals(estado);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reserva)) {
            return false;
        }

        Reserva otra = (Reserva) obj;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + actividad;
    }
}