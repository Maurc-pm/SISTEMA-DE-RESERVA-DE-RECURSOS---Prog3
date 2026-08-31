package reservas.logic;

import java.util.Objects;

public class Categoria {

    private String id;
    private String descripcion;

    public Categoria() {
        this.id = "";
        this.descripcion = "";
    }

    public Categoria(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Categoria)) {
            return false;
        }

        Categoria otra = (Categoria) obj;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}