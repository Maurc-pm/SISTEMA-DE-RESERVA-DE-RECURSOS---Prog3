package reservas.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Celda genérica de una matriz Hora x Columna, usada por las
 * pantallas de calendarización (recursos y actividades).
 */
public class Celda {

    private boolean ocupada;
    private final List<String> textos;

    public Celda() {
        this.ocupada = false;
        this.textos = new ArrayList<>();
    }

    public void agregar(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return;
        }

        this.ocupada = true;
        this.textos.add(texto);
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public List<String> getTextos() {
        return textos;
    }

    public String getTexto() {
        return String.join(" | ", textos);
    }
}
