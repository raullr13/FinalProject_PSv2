package medicalcabinet.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import medicalcabinet.domain.dtos.UserDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AuthRestClient {

    private static final String BASE_URL = "http://localhost:8081/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthRestClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public UserDTO authenticate(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        String jsonBody = objectMapper.writeValueAsString(credentials);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), UserDTO.class);
        } else {
            throw new Exception("Invalid username or password! (401 Unauthorized)");
        }
    }
}