package reservas.data;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Usuario;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private final List<Usuario> usuarios;
    private final List<Categoria> categorias;
    private final List<Recurso> recursos;
    private final List<Reserva> reservas;

    public Data() {
        usuarios = new ArrayList<>();
        categorias = new ArrayList<>();
        recursos = new ArrayList<>();
        reservas = new ArrayList<>();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}