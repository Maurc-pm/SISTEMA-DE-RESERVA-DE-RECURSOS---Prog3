package reservas.protocol;

import reservas.logic.*;

public final class Despachador {
    private Despachador() { }

    public static Response atender(IService service, Request request) {
        Object[] a = request.getArgumentos();
        try {
            return switch (request.getOperacion()) {
                case LOGIN -> Response.ok(service.login((String) a[0], (String) a[1]));
                case BUSCAR_USUARIO_POR_ID -> Response.ok(service.buscarUsuarioPorId((String) a[0]));
                case CAMBIAR_CLAVE -> { service.cambiarClave((Usuario) a[0], (String) a[1], (String) a[2], (String) a[3]); yield voidResult(a); }
                case CREAR_FUNCIONARIO -> { service.crearFuncionario((Funcionario) a[0]); yield voidResult(a); }
                case BUSCAR_FUNCIONARIO_POR_ID -> Response.ok(service.buscarFuncionarioPorId((String) a[0]));
                case BUSCAR_FUNCIONARIOS -> Response.ok(service.buscarFuncionarios((String) a[0], (String) a[1]));
                case LISTAR_FUNCIONARIOS -> Response.ok(service.listarFuncionarios());
                case MODIFICAR_FUNCIONARIO -> { service.modificarFuncionario((Funcionario) a[0]); yield voidResult(a); }
                case ELIMINAR_FUNCIONARIO -> { service.eliminarFuncionario((String) a[0]); yield voidResult(a); }
                case CREAR_CATEGORIA -> { service.crearCategoria((Categoria) a[0]); yield voidResult(a); }
                case BUSCAR_CATEGORIA_POR_ID -> Response.ok(service.buscarCategoriaPorId((String) a[0]));
                case BUSCAR_CATEGORIAS -> Response.ok(service.buscarCategorias((String) a[0]));
                case LISTAR_CATEGORIAS -> Response.ok(service.listarCategorias());
                case MODIFICAR_CATEGORIA -> { service.modificarCategoria((Categoria) a[0]); yield voidResult(a); }
                case ELIMINAR_CATEGORIA -> { service.eliminarCategoria((String) a[0]); yield voidResult(a); }
                case CREAR_RECURSO -> { service.crearRecurso((Recurso) a[0]); yield voidResult(a); }
                case BUSCAR_RECURSO_POR_ID -> Response.ok(service.buscarRecursoPorId((String) a[0]));
                case LISTAR_RECURSOS -> Response.ok(service.listarRecursos());
                case BUSCAR_RECURSOS_POR_CATEGORIA -> Response.ok(service.buscarRecursosPorCategoria((Categoria) a[0]));
                case MODIFICAR_RECURSO -> { service.modificarRecurso((Recurso) a[0]); yield voidResult(a); }
                case ELIMINAR_RECURSO -> { service.eliminarRecurso((String) a[0]); yield voidResult(a); }
                case EXTRAER_RESERVA_CON_IA -> Response.ok(service.extraerReservaConIA((String) a[0]));
                case LISTAR_RESERVAS -> Response.ok(service.listarReservas());
                case LISTAR_RESERVAS_FUNCIONARIO -> Response.ok(service.listarReservasFuncionario((Funcionario) a[0]));
                case CREAR_RESERVA -> { service.crearReserva((Reserva) a[0], (java.util.List<Categoria>) a[1]); yield voidResult(a); }
                case CANCELAR_RESERVA -> { service.cancelarReserva((Reserva) a[0]); yield voidResult(a); }
            };
        } catch (Exception e) {
            return Response.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private static Response voidResult(Object[] argumentos) {
        return Response.ok(argumentos == null || argumentos.length == 0 ? null : argumentos[0]);
    }
}
