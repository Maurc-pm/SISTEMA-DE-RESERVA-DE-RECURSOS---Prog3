package reservas.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import reservas.ai.ReservaExtractorService;
import reservas.data.Data;
import reservas.data.XmlStorage;
import reservas.logic.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.mockito.Mockito.*;

/** XML se intercepta ANTES de inicializar el singleton. Cada prueba recibe otro Data/Service. */
public abstract class ServiceTestSupport {
    protected Data data;
    protected Service service;
    protected MockedStatic<XmlStorage> storage;
    private MockedStatic<Service> singleton;
    private FixedTime time;

    @BeforeEach
    final void aislarServicio() throws Exception {
        data = new Data();
        storage = mockStatic(XmlStorage.class);
        storage.when(XmlStorage::cargar).thenReturn(data);
        // Única reflexión sobre Service: construcción e inyección del adaptador externo.
        // No se prueban métodos privados ni se cambian campos final del singleton.
        Constructor<Service> constructor = Service.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        service = constructor.newInstance();
        singleton = mockStatic(Service.class);
        singleton.when(Service::instance).thenReturn(service);
        time = new FixedTime();
        Sesion.logout();
        storage.clearInvocations();
    }

    @AfterEach
    final void liberarAislamiento() {
        Sesion.logout();
        if (time != null) time.close();
        if (singleton != null) singleton.close();
        if (storage != null) storage.close();
    }

    protected void extractor(ReservaExtractorService extractor) throws Exception {
        Field field = Service.class.getDeclaredField("reservaExtractorService");
        field.setAccessible(true);
        field.set(service, extractor);
    }

    protected Funcionario funcionario(String id, String nombre) throws Exception {
        Funcionario f = new Funcionario(id, "ignorada", nombre, "8888-0000");
        service.crearFuncionario(f);
        return f;
    }

    protected Categoria categoria(String descripcion) throws Exception {
        Categoria c = new Categoria("", descripcion);
        service.crearCategoria(c);
        return c;
    }

    protected Recurso recurso(String id, Categoria categoria) throws Exception {
        Recurso r = new Recurso(id, categoria, "Recurso " + id);
        service.crearRecurso(r);
        return r;
    }

    protected Reserva reserva(Funcionario f, LocalDate fecha, String inicio, String fin) {
        return new Reserva("", f, "Reunión", fecha, LocalTime.parse(inicio),
                LocalTime.parse(fin), List.of(), Reserva.ACTIVA);
    }
}
