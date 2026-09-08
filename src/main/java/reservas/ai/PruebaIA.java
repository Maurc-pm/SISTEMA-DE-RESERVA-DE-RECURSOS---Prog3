package reservas.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class PruebaIA {

    public static void main(String[] args) {

        OpenAiChatModel modelo = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        ReservaExtractorService servicio =
                AiServices.create(
                        ReservaExtractorService.class,
                        modelo
                );

        ReservaExtraccion resultado =
                servicio.extraer(
                        "Reunión de planificación mañana de 10:00 a 12:00 con sala y proyector",
                        "sala, proyector, laptop",
                        java.time.LocalDate.now().toString()
                );

        System.out.println(resultado);
    }
}