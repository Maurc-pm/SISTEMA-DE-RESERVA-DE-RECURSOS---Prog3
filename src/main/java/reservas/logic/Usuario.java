package reservas.logic;

import java.util.Objects;

public class Usuario {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String FUNCIONARIO = "FUNCIONARIO";

    private String id;
    private String clave;
    private String rol;

    public Usuario() {
        this.id = "";
        this.clave = "";
        this.rol = "";
    }

    public Usuario(String id, String clave, String rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean esAdministrador() {
        return ADMINISTRADOR.equals(rol);
    }

    public boolean esFuncionario() {
        return FUNCIONARIO.equals(rol);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Usuario)) {
            return false;
        }

        Usuario otro = (Usuario) obj;
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}