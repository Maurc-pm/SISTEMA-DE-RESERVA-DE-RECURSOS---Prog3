package reservas.presentation.categorias;

import reservas.logic.Categoria;
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
        limpiar();
    }

    // =========================================================
    // BUSCAR
    // =========================================================

    public void buscar() {

        String descripcion =
                view.getDescripcionBusqueda();

        List<Categoria> resultado =
                Service.instance()
                        .buscarCategorias(descripcion);

        model.setCategorias(resultado);
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
                    new Categoria();

            categoria.setDescripcion(
                    view.getDescripcionCategoria()
            );

            if (editando) {

                /*
                 * El ID no cambia cuando modificamos.
                 */
                categoria.setId(
                        model.getCurrent().getId()
                );

                Service.instance()
                        .modificarCategoria(categoria);

                view.mostrarMensaje(
                        "Categoría modificada correctamente"
                );

            } else {

                /*
                 * No asignamos ID.
                 * Service lo genera automáticamente.
                 */
                Service.instance()
                        .crearCategoria(categoria);

                view.mostrarMensaje(
                        "Categoría creada correctamente"
                );
            }

            cargarCategorias();
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

            Categoria actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar una categoría"
                );
            }

            Service.instance()
                    .eliminarCategoria(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Categoría eliminada correctamente"
            );

            cargarCategorias();
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
                new Categoria()
        );

        view.limpiarSeleccionTabla();
    }

    // =========================================================
    // SELECCIONAR CATEGORÍA DE LA TABLA
    // =========================================================

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getCategorias()
                .size()) {

            return;
        }

        Categoria categoria =
                model.getCategorias()
                        .get(fila);

        model.setCurrent(categoria);
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
}