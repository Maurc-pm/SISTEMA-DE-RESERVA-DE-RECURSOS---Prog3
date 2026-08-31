package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
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

        cargarFuncionarios();
        limpiar();
    }

    // =========================================================
    // BUSCAR
    // =========================================================

    public void buscar() {

        String id = view.getIdBusqueda();
        String nombre = view.getNombreBusqueda();

        List<Funcionario> resultado =
                Service.instance()
                        .buscarFuncionarios(id, nombre);

        model.setFuncionarios(resultado);
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

            Funcionario funcionario =
                    new Funcionario();

            if (editando) {

                // Al modificar mantenemos el ID original.
                funcionario.setId(
                        model.getCurrent().getId()
                );

            } else {

                funcionario.setId(
                        view.getIdFuncionario()
                );
            }

            funcionario.setNombre(
                    view.getNombreFuncionario()
            );

            funcionario.setTelefono(
                    view.getTelefonoFuncionario()
            );

            if (editando) {

                Service.instance()
                        .modificarFuncionario(funcionario);

                view.mostrarMensaje(
                        "Funcionario modificado correctamente"
                );

            } else {

                Service.instance()
                        .crearFuncionario(funcionario);

                view.mostrarMensaje(
                        "Funcionario creado correctamente"
                );
            }

            cargarFuncionarios();
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

            Funcionario actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar un funcionario"
                );
            }

            Service.instance()
                    .eliminarFuncionario(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Funcionario eliminado correctamente"
            );

            cargarFuncionarios();
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
                new Funcionario()
        );

        view.limpiarSeleccionTabla();
    }

    // =========================================================
    // SELECCIONAR FILA DE LA TABLA
    // =========================================================

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getFuncionarios()
                .size()) {

            return;
        }

        Funcionario funcionario =
                model.getFuncionarios()
                        .get(fila);

        model.setCurrent(funcionario);
    }

    // =========================================================
    // CARGAR TODOS LOS FUNCIONARIOS
    // =========================================================

    private void cargarFuncionarios() {

        model.setFuncionarios(
                Service.instance()
                        .listarFuncionarios()
        );
    }
}