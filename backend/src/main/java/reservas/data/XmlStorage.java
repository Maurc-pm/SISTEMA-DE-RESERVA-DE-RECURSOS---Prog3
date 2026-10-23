package reservas.data;

import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalTime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Usuario;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;

public class XmlStorage {

    private static final String DIRECTORIO = "data";
    private static final String ARCHIVO = "data/reservas.xml";

    private XmlStorage() {
    }

    public static File getArchivo() {

        File directorio = new File(DIRECTORIO);

        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        return new File(ARCHIVO);
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    public static void guardar(Data data) throws Exception {

        Document documento =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        Element raiz =
                documento.createElement("sistemaReservas");

        documento.appendChild(raiz);

        guardarUsuarios(documento, raiz, data);
        guardarCategorias(documento, raiz, data);
        guardarRecursos(documento, raiz, data);
        guardarReservas(documento, raiz, data);

        Transformer transformer =
                TransformerFactory
                        .newInstance()
                        .newTransformer();

        transformer.setOutputProperty(
                OutputKeys.INDENT,
                "yes"
        );

        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                "4"
        );

        transformer.transform(
                new DOMSource(documento),
                new StreamResult(getArchivo())
        );
    }

    // =========================================================
    // USUARIOS
    // =========================================================

    private static void guardarUsuarios(
            Document documento,
            Element raiz,
            Data data
    ) {

        Element usuarios =
                documento.createElement("usuarios");

        raiz.appendChild(usuarios);

        for (Usuario usuario : data.getUsuarios()) {

            Element elemento;

            if (usuario instanceof Funcionario) {

                Funcionario funcionario =
                        (Funcionario) usuario;

                elemento =
                        documento.createElement("funcionario");

                elemento.setAttribute(
                        "nombre",
                        funcionario.getNombre()
                );

                elemento.setAttribute(
                        "telefono",
                        funcionario.getTelefono()
                );

            } else {

                elemento =
                        documento.createElement("usuario");
            }

            elemento.setAttribute(
                    "id",
                    usuario.getId()
            );

            elemento.setAttribute(
                    "clave",
                    usuario.getClave()
            );

            elemento.setAttribute(
                    "rol",
                    usuario.getRol()
            );

            usuarios.appendChild(elemento);
        }
    }

    // =========================================================
    // CATEGORÍAS
    // =========================================================

    private static void guardarCategorias(
            Document documento,
            Element raiz,
            Data data
    ) {

        Element categorias =
                documento.createElement("categorias");

        raiz.appendChild(categorias);

        for (Categoria categoria : data.getCategorias()) {

            Element elemento =
                    documento.createElement("categoria");

            elemento.setAttribute(
                    "id",
                    categoria.getId()
            );

            elemento.setAttribute(
                    "descripcion",
                    categoria.getDescripcion()
            );

            categorias.appendChild(elemento);
        }
    }

    // =========================================================
    // RECURSOS
    // =========================================================

    private static void guardarRecursos(
            Document documento,
            Element raiz,
            Data data
    ) {

        Element recursos =
                documento.createElement("recursos");

        raiz.appendChild(recursos);

        for (Recurso recurso : data.getRecursos()) {

            Element elemento =
                    documento.createElement("recurso");

            elemento.setAttribute(
                    "id",
                    recurso.getId()
            );

            elemento.setAttribute(
                    "categoriaId",
                    recurso.getCategoria().getId()
            );

            elemento.setAttribute(
                    "descripcion",
                    recurso.getDescripcion()
            );

            recursos.appendChild(elemento);
        }
    }

    // =========================================================
    // RESERVAS
    // =========================================================

    private static void guardarReservas(
            Document documento,
            Element raiz,
            Data data
    ) {

        Element reservas =
                documento.createElement("reservas");

        raiz.appendChild(reservas);

        for (Reserva reserva : data.getReservas()) {

            Element elemento =
                    documento.createElement("reserva");

            elemento.setAttribute(
                    "id",
                    reserva.getId()
            );

            elemento.setAttribute(
                    "funcionarioId",
                    reserva.getFuncionario().getId()
            );

            elemento.setAttribute(
                    "actividad",
                    reserva.getActividad()
            );

            elemento.setAttribute(
                    "fecha",
                    reserva.getFecha().toString()
            );

            elemento.setAttribute(
                    "horaInicio",
                    reserva.getHoraInicio().toString()
            );

            elemento.setAttribute(
                    "horaFin",
                    reserva.getHoraFin().toString()
            );

            elemento.setAttribute(
                    "estado",
                    reserva.getEstado()
            );

            for (Recurso recurso : reserva.getRecursos()) {

                Element recursoElemento =
                        documento.createElement("recurso");

                recursoElemento.setAttribute(
                        "id",
                        recurso.getId()
                );

                elemento.appendChild(recursoElemento);
            }

            reservas.appendChild(elemento);
        }
    }
    // =========================================================
// CARGAR
// =========================================================

    public static Data cargar() throws Exception {

        File archivo = getArchivo();

        if (!archivo.exists()) {
            return null;
        }

        Document documento =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(archivo);

        documento.getDocumentElement().normalize();

        Data data = new Data();

        cargarUsuarios(documento, data);
        cargarCategorias(documento, data);
        cargarRecursos(documento, data);
        cargarReservas(documento, data);

        return data;
    }


// =========================================================
// CARGAR USUARIOS
// =========================================================

    private static void cargarUsuarios(
            Document documento,
            Data data
    ) {

        NodeList usuarios =
                documento.getElementsByTagName("usuario");

        for (int i = 0; i < usuarios.getLength(); i++) {

            Element elemento =
                    (Element) usuarios.item(i);

            Usuario usuario =
                    new Usuario(
                            elemento.getAttribute("id"),
                            elemento.getAttribute("clave"),
                            elemento.getAttribute("rol")
                    );

            data.getUsuarios().add(usuario);
        }


        NodeList funcionarios =
                documento.getElementsByTagName("funcionario");

        for (int i = 0; i < funcionarios.getLength(); i++) {

            Element elemento =
                    (Element) funcionarios.item(i);

            Funcionario funcionario =
                    new Funcionario(
                            elemento.getAttribute("id"),
                            elemento.getAttribute("clave"),
                            elemento.getAttribute("nombre"),
                            elemento.getAttribute("telefono")
                    );

            data.getUsuarios().add(funcionario);
        }
    }


// =========================================================
// CARGAR CATEGORÍAS
// =========================================================

    private static void cargarCategorias(
            Document documento,
            Data data
    ) {

        NodeList categorias =
                documento.getElementsByTagName("categoria");

        for (int i = 0; i < categorias.getLength(); i++) {

            Element elemento =
                    (Element) categorias.item(i);

            Categoria categoria =
                    new Categoria();

            categoria.setId(
                    elemento.getAttribute("id")
            );

            categoria.setDescripcion(
                    elemento.getAttribute("descripcion")
            );

            data.getCategorias().add(categoria);
        }
    }


// =========================================================
// CARGAR RECURSOS
// =========================================================

    private static void cargarRecursos(
            Document documento,
            Data data
    ) {

        NodeList bloques =
                documento.getElementsByTagName("recursos");

        if (bloques.getLength() == 0) {
            return;
        }

        Element bloqueRecursos =
                (Element) bloques.item(0);

        NodeList recursos =
                bloqueRecursos.getElementsByTagName("recurso");

        for (int i = 0; i < recursos.getLength(); i++) {

            Element elemento =
                    (Element) recursos.item(i);

            String categoriaId =
                    elemento.getAttribute("categoriaId");

            Categoria categoria =
                    buscarCategoria(data, categoriaId);

            if (categoria == null) {
                continue;
            }

            Recurso recurso =
                    new Recurso();

            recurso.setId(
                    elemento.getAttribute("id")
            );

            recurso.setCategoria(categoria);

            recurso.setDescripcion(
                    elemento.getAttribute("descripcion")
            );

            data.getRecursos().add(recurso);
        }
    }


// =========================================================
// CARGAR RESERVAS
// =========================================================

    private static void cargarReservas(
            Document documento,
            Data data
    ) {

        NodeList reservas =
                documento.getElementsByTagName("reserva");

        for (int i = 0; i < reservas.getLength(); i++) {

            Element elemento =
                    (Element) reservas.item(i);

            Funcionario funcionario =
                    buscarFuncionario(
                            data,
                            elemento.getAttribute("funcionarioId")
                    );

            if (funcionario == null) {
                continue;
            }

            Reserva reserva =
                    new Reserva();

            reserva.setId(
                    elemento.getAttribute("id")
            );

            reserva.setFuncionario(funcionario);

            reserva.setActividad(
                    elemento.getAttribute("actividad")
            );

            reserva.setFecha(
                    LocalDate.parse(
                            elemento.getAttribute("fecha")
                    )
            );

            reserva.setHoraInicio(
                    LocalTime.parse(
                            elemento.getAttribute("horaInicio")
                    )
            );

            reserva.setHoraFin(
                    LocalTime.parse(
                            elemento.getAttribute("horaFin")
                    )
            );

            reserva.setEstado(
                    elemento.getAttribute("estado")
            );


            NodeList recursos =
                    elemento.getElementsByTagName("recurso");

            for (int j = 0; j < recursos.getLength(); j++) {

                Element recursoElemento =
                        (Element) recursos.item(j);

                Recurso recurso =
                        buscarRecurso(
                                data,
                                recursoElemento.getAttribute("id")
                        );

                if (recurso != null) {
                    reserva.agregarRecurso(recurso);
                }
            }

            data.getReservas().add(reserva);
        }
    }


// =========================================================
// BÚSQUEDAS AUXILIARES
// =========================================================

    private static Categoria buscarCategoria(
            Data data,
            String id
    ) {

        for (Categoria categoria : data.getCategorias()) {

            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }

        return null;
    }


    private static Funcionario buscarFuncionario(
            Data data,
            String id
    ) {

        for (Usuario usuario : data.getUsuarios()) {

            if (usuario instanceof Funcionario
                    && usuario.getId().equals(id)) {

                return (Funcionario) usuario;
            }
        }

        return null;
    }


    private static Recurso buscarRecurso(
            Data data,
            String id
    ) {

        for (Recurso recurso : data.getRecursos()) {

            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }

        return null;
    }
}