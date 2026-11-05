package reservas.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.logic.*;
import reservas.presentation.alertas.Controller;
import reservas.presentation.alertas.Model;
import reservas.presentation.alertas.TableModel;
import reservas.presentation.alertas.View;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertasTest {
    private View view;
    private Model model;
    private Controller controller;

    @BeforeEach
    void preparar() {
        view = mock(View.class);
        model = new Model();
        controller = new Controller(view, model);
    }

    private static Reserva reserva(String nombre, String actividad) {
        Funcionario funcionario = new Funcionario("F-" + nombre, "x", nombre, "8888-8888");
        Recurso laptop = new Recurso("R1", new Categoria("CAT-000001", "Laptop"), "Laptop #1");
        return new Reserva("RES-1", funcionario, actividad, LocalDate.of(2026, 7, 31),
                LocalTime.of(8, 0), LocalTime.of(10, 0), List.of(laptop), Reserva.ACTIVA);
    }

    @Test
    void controllerSeRegistraEnLaVista() {
        verify(view).setController(controller);
        verify(view).setModel(model);
    }

    @Test
    void notificacionesSeAgreganEnOrdenYAvisanALaVista() {
        AtomicInteger cambios = new AtomicInteger();
        model.addPropertyChangeListener(e -> cambios.incrementAndGet());
        controller.notificar(reserva("Juan Mora", "Reunion clientes"));
        controller.notificar(reserva("Pedro Arias", "Capacitacion"));
        controller.notificar(null);
        assertEquals(2, model.getAlertas().size());
        assertEquals("Capacitacion", model.getAlerta(1).getActividad());
        assertEquals(2, cambios.get());
    }

    @Test
    void verMuestraElDetalleDeLaAlertaSeleccionada() {
        Reserva segunda = reserva("Pedro Arias", "Capacitacion");
        controller.notificar(reserva("Juan Mora", "Reunion clientes"));
        controller.notificar(segunda);
        controller.ver(1);
        verify(view).mostrarDetalle(segunda);
        controller.ver(5);
        verify(view, times(1)).mostrarDetalle(any());
    }

    @Test
    void descartarConfirmadoQuitaSoloEsaAlerta() {
        Reserva primera = reserva("Juan Mora", "Reunion clientes");
        Reserva segunda = reserva("Pedro Arias", "Capacitacion");
        controller.notificar(primera);
        controller.notificar(segunda);
        when(view.confirmarDescarte(primera)).thenReturn(true);
        controller.descartar(0);
        assertEquals(List.of(segunda), model.getAlertas());
    }

    @Test
    void descartarNoConfirmadoConservaLaAlerta() {
        controller.notificar(reserva("Juan Mora", "Reunion clientes"));
        when(view.confirmarDescarte(any())).thenReturn(false);
        controller.descartar(0);
        assertEquals(1, model.getAlertas().size());
    }

    @Test
    void descartarNoUsaElServicioRemoto() {
        Servicio.set(null);
        controller.notificar(reserva("Juan Mora", "Reunion clientes"));
        when(view.confirmarDescarte(any())).thenReturn(true);
        assertDoesNotThrow(() -> controller.descartar(0));
        assertTrue(model.getAlertas().isEmpty());
    }

    @Test
    void tablaMuestraEncargadoYActividadYNoEsEditable() {
        TableModel tabla = new TableModel(List.of(reserva("Juan Mora", "Reunion clientes")));
        assertEquals(3, tabla.getColumnCount());
        assertEquals("Actividad", tabla.getColumnName(0));
        assertEquals("Ver", tabla.getColumnName(1));
        assertEquals("Desc.", tabla.getColumnName(2));
        assertEquals("Juan Mora - Reunion clientes", tabla.getValueAt(0, TableModel.ACTIVIDAD));
        for (int c = 0; c < 3; c++) assertFalse(tabla.isCellEditable(0, c));
    }

    @Test
    void vistaRealSeRefrescaAlCambiarElModelo() {
        View real = new View();
        Model m = new Model();
        new Controller(real, m).notificar(reserva("Ana Lopez", "Coordinacion viaje"));
        javax.swing.JTable tabla = (javax.swing.JTable)
                ((javax.swing.JScrollPane) real.getPanel().getComponent(0)).getViewport().getView();
        assertEquals(1, tabla.getRowCount());
        assertEquals("Ana Lopez - Coordinacion viaje", tabla.getValueAt(0, 0));
    }
}
