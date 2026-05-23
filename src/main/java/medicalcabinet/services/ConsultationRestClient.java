package medicalcabinet.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import medicalcabinet.domain.dtos.ConsultationDTO;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.List;

public class ConsultationRestClient {
    private final String BASE_URL = "http://localhost:8082/api/consultations";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public ConsultationRestClient() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Required for LocalDate support
    }

    public List<ConsultationDTO> getAllConsultations() {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<ConsultationDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<ConsultationDTO> getPatientMedicalRecord(int patientId) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/patient/" + patientId)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<ConsultationDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<ConsultationDTO> getDoctorConsultations(int doctorId) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/doctor/" + doctorId)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<ConsultationDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis) {
        try {
            String url = BASE_URL + "/filter?patientId=" + patientId;
            if (date != null) url += "&date=" + date.toString();
            if (diagnosis != null && !diagnosis.isEmpty()) url += "&diagnosis=" + diagnosis;

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<ConsultationDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

    public boolean addConsultation(ConsultationDTO consultation) {
        try {
            String json = mapper.writeValueAsString(consultation);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean updateConsultation(ConsultationDTO consultation) {
        try {
            String json = mapper.writeValueAsString(consultation);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + consultation.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)).build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public boolean deleteConsultation(int id) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/" + id)).DELETE().build();
            return client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) { return false; }
    }

    public List<ConsultationDTO> getConsultationsByPatientId(int patientId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/patient/" + patientId))
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), new TypeReference<List<ConsultationDTO>>() {});
        } catch (Exception e) { e.printStackTrace(); return List.of(); }
    }

}

