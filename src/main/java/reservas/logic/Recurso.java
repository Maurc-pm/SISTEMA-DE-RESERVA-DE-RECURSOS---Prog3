package reservas.logic;

import java.util.Objects;

public class Recurso {

    private String id;
    private Categoria categoria;
    private String descripcion;

    public Recurso() {
        this.id = "";
        this.categoria = new Categoria();
        this.descripcion = "";
    }

    public Recurso(String id, Categoria categoria, String descripcion) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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

        if (!(obj instanceof Recurso)) {
            return false;
        }

        Recurso otro = (Recurso) obj;
        return Objects.equals(id, otro.id);
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