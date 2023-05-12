package RestfulFactory.Model;

import restful.sdk.API.Types.HashAlgorithmOID;
import java.util.List;

public class DocumentDigests {

    private List<byte[]> hashes;
    private HashAlgorithmOID hashAlgorithmOID;

    public List<byte[]> getHashes() {
        return hashes;
    }
    public void setHashes(List<byte[]> hashes) {
        this.hashes = hashes;
    }

    public HashAlgorithmOID getHashAlgorithmOID() {
        return hashAlgorithmOID;
    }
    public void setHashAlgorithmOID(HashAlgorithmOID hashAlgorithmOID) {
        this.hashAlgorithmOID = hashAlgorithmOID;
    }
}
