package RestfulFactory.Request;

import RestfulFactory.Request.CertificateRequest;
import RestfulFactory.Model.SearchConditions;

public class CredentialListRequest extends CertificateRequest {

    private SearchConditions searchConditions;

    public SearchConditions getSearchConditions() {
        return searchConditions;
    }

    public void setSearchConditions(SearchConditions searchConditions) {
        this.searchConditions = searchConditions;
    }
    
}
