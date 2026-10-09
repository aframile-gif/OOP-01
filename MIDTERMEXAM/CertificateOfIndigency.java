public class CertificateOfIndigency extends DocumentRequest {
    private String purpose;
 
    public CertificateOfIndigency(String requestId, Resident resident, String purpose) {
        super(requestId, resident);
        setPurpose(purpose);
    }
 
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) {
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose cannot be empty.");
        }
        this.purpose = purpose.trim();
    }
 
    @Override
    public String getDocumentType() { return "Certificate of Indigency"; }
 
    @Override
    public String getRequirements() { return "Valid ID; household and family income information"; }
 
    @Override
    public String getSpecificDetails() { return "Purpose: " + purpose; }
}
