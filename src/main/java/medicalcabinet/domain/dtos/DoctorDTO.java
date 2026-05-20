package medicalcabinet.domain.dtos;

public class DoctorDTO {
    private int id;
    private String fullName;
    private String specialization;
    private String cvText;
    private String photoPath;
    private String schedule;

    public DoctorDTO(int id, String fullName, String specialization, String cvText, String photoPath, String schedule) {
        this.id = id;
        this.fullName = fullName;
        this.specialization = specialization;
        this.cvText = cvText;
        this.photoPath = photoPath;
        this.schedule = schedule;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSpecialization() { return specialization; }
    public String getCvText() { return cvText; }
    public String getPhotoPath() { return photoPath; }
    public String getSchedule() { return schedule; }

    @Override
    public String toString() {
        return fullName + " - " + specialization; // For easy display in JList
    }
}