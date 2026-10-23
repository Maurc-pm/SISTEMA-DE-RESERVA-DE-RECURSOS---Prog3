package reservas.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReservaExtractorService {

    @SystemMessage("""
            Eres un asistente especializado en extraer información
            de reservas de espacios y recursos.

            Reglas estrictas:

            1. La fecha siempre debe ser en formato ISO yyyy-MM-dd.
               Si el usuario usa una fecha relativa como "mañana",
               usa la fecha de referencia proporcionada.

            2. Las horas deben estar en formato HH:mm de 24 horas.

            3. Para categoriasRecurso SOLO puedes usar nombres
               que aparezcan EXACTAMENTE en la lista de categorías
               disponibles proporcionada.

            4. Si algún campo no está presente en la frase,
               devuelve null para ese campo.
            """)

    @UserMessage("""
            Fecha de referencia (hoy): {{hoy}}

            Categorías de recursos disponibles:
            {{categorias}}

            Frase del usuario:
            "{{frase}}"

            Extrae los datos de la reserva.
            """)

    ReservaExtraccion extraer(
            @V("frase") String frase,
            @V("categorias") String categorias,
            @V("hoy") String hoy
    );
}