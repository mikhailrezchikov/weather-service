import com.fasterxml.jackson.databind.JsonNode;

public class Main {

    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();

        JsonNode json = apiClient.getWeatherByPoint(52.3712, 4.89388);
        JsonNode factTemp = json.path("fact").path("temp");

        double avgTemp = apiClient.getAvgTempByPoint(json, 6);

        System.out.println("Полный ответ от API: \n" + json.toPrettyString());
        System.out.println("Текущая температура температура: " + factTemp);
        System.out.println("Средняя температура: " + avgTemp);
    }
}