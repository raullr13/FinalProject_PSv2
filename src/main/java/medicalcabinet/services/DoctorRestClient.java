package medicalcabinet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medicalcabinet.domain.dtos.DoctorDTO;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DoctorRestClient {
    private final String BASE_URL = "http://localhost:8082/api/doctors";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<DoctorDTO> getAllDoctors() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                return mapper.readValue(res.body(), new TypeReference<List<DoctorDTO>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public DoctorDTO getDoctorById(int id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + id))
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                return mapper.readValue(res.body(), DoctorDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllSpecializations() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/specializations"))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<String>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        try {
            String encodedSpec = URLEncoder.encode(specialization, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/specialization?name=" + encodedSpec))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<DoctorDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<DoctorDTO> searchDoctorsByName(String name) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/search?name=" + encodedName))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<DoctorDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }
}