package medicalcabinet.domain.entities;

public class Patient {
    private int id;
    private String fullName;
    private String cnp;
    private int age;
    private FisaMedicala fisaMedicala;

    public Patient(int id, String fullName, String cnp, int age) {
        this.id = id;
        this.fullName = fullName;
        this.cnp = cnp;
        this.age = age;
        this.fisaMedicala = new FisaMedicala();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public FisaMedicala getFisaMedicala() { return fisaMedicala; }
    public void setFisaMedicala(FisaMedicala fisaMedicala) { this.fisaMedicala = fisaMedicala; }
}