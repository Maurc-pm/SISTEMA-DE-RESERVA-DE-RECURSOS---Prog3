package reservas.protocol;

public final class Request implements Mensaje {
    private static final long serialVersionUID = 1L;
    private final Operacion operacion;
    private final Object[] argumentos;

    public Request(Operacion operacion, Object... argumentos) {
        this.operacion = operacion;
        this.argumentos = argumentos;
    }

    public Operacion getOperacion() { return operacion; }
    public Object[] getArgumentos() { return argumentos; }
}
