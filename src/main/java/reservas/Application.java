package reservas;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Service;
import reservas.logic.Usuario;

public class Application {

    public static void main(String[] args) {

        try {

            // 1. LOGIN
            Usuario usuario = Service.instance().login(
                    "admin",
                    "admin"
            );

            System.out.println(
                    "Login correcto: "
                            + usuario.getId()
                            + " - "
                            + usuario.getRol()
            );


            // 2. CREAR FUNCIONARIO
            Funcionario funcionario = new Funcionario();

            funcionario.setId("111");
            funcionario.setNombre("Juan Pérez");
            funcionario.setTelefono("8888-8888");

            Service.instance()
                    .crearFuncionario(funcionario);

            System.out.println(
                    "Funcionario creado: "
                            + funcionario.getNombre()
            );

            System.out.println(
                    "Clave inicial: "
                            + funcionario.getClave()
            );


            // 3. CREAR CATEGORÍA
            Categoria categoria = new Categoria();

            categoria.setDescripcion(
                    "Laptop Windows 11"
            );

            Service.instance()
                    .crearCategoria(categoria);

            System.out.println(
                    "Categoría creada: "
                            + categoria.getId()
                            + " - "
                            + categoria.getDescripcion()
            );


            // 4. CREAR RECURSO
            Recurso recurso = new Recurso();

            recurso.setId("238715");
            recurso.setCategoria(categoria);
            recurso.setDescripcion(
                    "Laptop #238715"
            );

            Service.instance()
                    .crearRecurso(recurso);

            System.out.println(
                    "Recurso creado: "
                            + recurso.getId()
                            + " - "
                            + recurso.getDescripcion()
            );


            // 5. MOSTRAR TOTALES
            System.out.println();
            System.out.println(
                    "Funcionarios: "
                            + Service.instance()
                            .listarFuncionarios()
                            .size()
            );

            System.out.println(
                    "Categorías: "
                            + Service.instance()
                            .listarCategorias()
                            .size()
            );

            System.out.println(
                    "Recursos: "
                            + Service.instance()
                            .listarRecursos()
                            .size()
            );

        } catch (Exception ex) {

            System.out.println(
                    "ERROR: " + ex.getMessage()
            );
        }
    }
}