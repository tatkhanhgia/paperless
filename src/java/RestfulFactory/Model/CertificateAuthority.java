package RestfulFactory.Model;

public class CertificateAuthority {

    private String name;
    private String description;
//    public String notBefore;
//    public String notAfter;
//    public String[] certificates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
