package medicalcabinet.domain.dtos;

import java.time.LocalDate;

public class PatientDTO {
    private int id;
    private String fullName;
    private String cnp;
    private int age;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}