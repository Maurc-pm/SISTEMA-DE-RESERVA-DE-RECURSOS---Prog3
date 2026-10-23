package reservas.protocol;

public final class Response implements Mensaje {
    private static final long serialVersionUID = 1L;
    private final boolean ok;
    private final Object valor;
    private final String mensajeError;

    private Response(boolean ok, Object valor, String mensajeError) {
        this.ok = ok;
        this.valor = valor;
        this.mensajeError = mensajeError;
    }

    public static Response ok(Object valor) { return new Response(true, valor, null); }
    public static Response error(String msg) { return new Response(false, null, msg); }
    public boolean isOk() { return ok; }
    public Object getValor() { return valor; }
    public String getMensajeError() { return mensajeError; }
}
