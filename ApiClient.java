import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode getWeatherByPoint(double lat, double lon) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s", lat, lon)))
                .header("X-Yandex-Weather-Key", "API_KEY")
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = futureResponse.join();

        try {
            return mapper.readTree(response.body());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public double getAvgTempByPoint(JsonNode body, int limit) {

        if (limit <= 0) {
            throw new IllegalArgumentException("Количество суток должно быть больше 0");
        }

        JsonNode forecasts = body.path("forecasts");

        if (!forecasts.isArray() || forecasts.size() < limit) {
            throw new IllegalStateException(
                    "API вернул недостаточно данных для расчёта средней температуры"
            );
        }

        double sum = 0;

        for (int i = 0; i < limit; i++) {
            JsonNode tempNode = forecasts
                    .get(i)
                    .path("parts")
                    .path("day")
                    .path("temp_avg");

            if (!tempNode.isNumber()) {
                throw new IllegalStateException(
                        "Нет корректной температуры за день " + (i + 1)
                );
            }

            sum += tempNode.asDouble();
        }

        return sum / limit;

    }
}
