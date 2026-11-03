package reservas.support;

import org.mockito.MockedStatic;
import reservas.data.MySqlPersistencia;
import reservas.logic.Service;

public final class MySqlScenario implements AutoCloseable {
    public final Service service;
    private final MockedStatic<Service> serviceMock;

    private MySqlScenario(Service service, MockedStatic<Service> serviceMock) {
        this.service = service;
        this.serviceMock = serviceMock;
    }

    public static MySqlScenario start(boolean resetSchema) throws Exception {
        if (resetSchema) TestDatabase.resetSchema();
        Service service = Service.crearParaPruebas(new MySqlPersistencia(TestDatabase.database()));
        MockedStatic<Service> serviceMock = org.mockito.Mockito.mockStatic(Service.class);
        serviceMock.when(Service::instance).thenReturn(service);
        return new MySqlScenario(service, serviceMock);
    }

    @Override
    public void close() {
        serviceMock.close();
    }
}
