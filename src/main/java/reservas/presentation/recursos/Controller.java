package reservas.presentation.recursos;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Service;

import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();
        cargarRecursos();
        limpiar();
    }

    // =========================================================
    // BUSCAR / FILTRAR
    // =========================================================

    public void buscar() {

        Categoria categoria =
                view.getCategoriaFiltro();

        /*
         * Si no hay categoría o el ID está vacío,
         * significa que se seleccionó "Todas".
         */
        if (categoria == null
                || categoria.getId() == null
                || categoria.getId().trim().isEmpty()) {

            cargarRecursos();
            return;
        }

        List<Recurso> resultado =
                Service.instance()
                        .buscarRecursosPorCategoria(categoria);

        model.setRecursos(resultado);
    }

    // =========================================================
    // GUARDAR / MODIFICAR
    // =========================================================

    public void guardar() {

        try {

            boolean editando =
                    model.getCurrent() != null
                            && model.getCurrent().getId() != null
                            && !model.getCurrent()
                            .getId()
                            .trim()
                            .isEmpty();

            Categoria categoria =
                    view.getCategoriaSeleccionada();

            if (categoria == null) {
                throw new Exception(
                        "Debe seleccionar una categoría"
                );
            }

            Recurso recurso = new Recurso();

            if (editando) {

                /*
                 * Al modificar conservamos el ID original.
                 */
                recurso.setId(
                        model.getCurrent().getId()
                );

            } else {

                recurso.setId(
                        view.getIdRecurso()
                );
            }

            recurso.setCategoria(categoria);

            recurso.setDescripcion(
                    view.getDescripcionRecurso()
            );

            if (editando) {

                Service.instance()
                        .modificarRecurso(recurso);

                view.mostrarMensaje(
                        "Recurso modificado correctamente"
                );

            } else {

                Service.instance()
                        .crearRecurso(recurso);

                view.mostrarMensaje(
                        "Recurso creado correctamente"
                );
            }

            cargarRecursos();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    // =========================================================
    // BORRAR
    // =========================================================

    public void borrar() {

        try {

            Recurso actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar un recurso"
                );
            }

            Service.instance()
                    .eliminarRecurso(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Recurso eliminado correctamente"
            );

            cargarRecursos();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    // =========================================================
    // LIMPIAR
    // =========================================================

    public void limpiar() {

        model.setCurrent(
                new Recurso()
        );

        view.limpiarSeleccionTabla();
    }

    // =========================================================
    // SELECCIONAR RECURSO DE LA TABLA
    // =========================================================

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getRecursos()
                .size()) {

            return;
        }

        Recurso recurso =
                model.getRecursos()
                        .get(fila);

        model.setCurrent(recurso);
    }

    // =========================================================
    // CARGAR CATEGORÍAS
    // =========================================================

    private void cargarCategorias() {

        model.setCategorias(
                Service.instance()
                        .listarCategorias()
        );
    }

    // =========================================================
    // CARGAR RECURSOS
    // =========================================================

    private void cargarRecursos() {

        model.setRecursos(
                Service.instance()
                        .listarRecursos()
        );
    }
}