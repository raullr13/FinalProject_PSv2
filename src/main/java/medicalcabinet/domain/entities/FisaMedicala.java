package medicalcabinet.domain.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FisaMedicala {
    private List<Consultation> consultations;

    public FisaMedicala() {
        this.consultations = new ArrayList<>();
    }

    public FisaMedicala(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public List<Consultation> getAllConsultations() {
        return consultations;
    }

    public void addConsultation(Consultation consultation) {
        this.consultations.add(consultation);
    }

    public boolean removeConsultation(int consultationId) {
        return this.consultations.removeIf(c -> c.getId() == consultationId);
    }

    public List<Consultation> filterByDiagnosis(String diagnosis) {
        return consultations.stream()
                .filter(c -> c.getDiagnosis().equalsIgnoreCase(diagnosis))
                .collect(Collectors.toList());
    }

    public List<Consultation> filterByDate(LocalDate date) {
        return consultations.stream()
                .filter(c -> c.getConsultationDate().equals(date))
                .collect(Collectors.toList());
    }
}
