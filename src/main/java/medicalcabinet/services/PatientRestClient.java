package medicalcabinet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medicalcabinet.domain.dtos.PatientDTO;

import java.net.URI;
import java.net.http.*;
import java.util.List;

public class PatientRestClient {
    private final String BASE_URL = "http://localhost:8082/api/patients";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<PatientDTO> getAllPatients() {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<PatientDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<PatientDTO> searchPatients(String name) {
        try {

            String url = BASE_URL + "/search?name=" + name;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<PatientDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public void createPatient(PatientDTO patient) {
        try {
            String json = mapper.writeValueAsString(patient);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updatePatient(PatientDTO patient) {
        try {
            String json = mapper.writeValueAsString(patient);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + patient.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) { e.printStackTrace(); }
    }



    public boolean savePatient(PatientDTO patient) {
        try {
            String json = mapper.writeValueAsString(patient);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public PatientDTO getPatientById(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), PatientDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientDTO getPatientByUserId(int userId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/user/" + userId))
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                return mapper.readValue(res.body(), PatientDTO.class);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }


}