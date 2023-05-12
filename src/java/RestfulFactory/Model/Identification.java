package RestfulFactory.Model;

import restful.sdk.API.Types.IdentificationType;

public class Identification {

    private IdentificationType type;
    private String value;

    public IdentificationType getType() {
        return type;
    }

    public void setType(IdentificationType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Identification(IdentificationType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Identification() {
    }
}
