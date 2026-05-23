package medicalcabinet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medicalcabinet.domain.dtos.UserDTO;
import java.net.URI;
import java.net.http.*;
import java.util.List;

public class UserRestClient {
    private final String BASE_URL = "http://localhost:8081/api/users";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<UserDTO> getAllUsers() {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<UserDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public boolean saveUser(UserDTO user) {
        try {
            String json = mapper.writeValueAsString(user);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean updateUser(UserDTO user) {
        try {
            String json = mapper.writeValueAsString(user);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + user.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean deleteUser(int id) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/" + id)).DELETE().build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean triggerNotification(int id) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/notify/" + id)).POST(HttpRequest.BodyPublishers.noBody()).build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }
}