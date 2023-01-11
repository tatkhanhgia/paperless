package RestfulFactoryl.Response;

import restful.sdk.API.BaseCertificateInfo;
import java.util.List;

public class CredentialListResponse extends Response {

    private List<BaseCertificateInfo> certs;

    public List<BaseCertificateInfo> getCerts() {
        return certs;
    }

    public void setCerts(List<BaseCertificateInfo> certs) {
        this.certs = certs;
    }
}
