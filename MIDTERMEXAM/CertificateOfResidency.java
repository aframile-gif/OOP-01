public class CertificateOfResidency extends DocumentRequest implements FeeApplicable {
    private static final double BASE_FEE = 30.0;   // sample fee
    private int yearsOfResidency;
 
    public CertificateOfResidency(String requestId, Resident resident, int yearsOfResidency) {
        super(requestId, resident);
        setYearsOfResidency(yearsOfResidency);
        setFee(calculateFee());
    }
 
    public int getYearsOfResidency() { return yearsOfResidency; }
    public void setYearsOfResidency(int years) {
        if (years < 1 || years > 120) {
            throw new IllegalArgumentException("Years of residency must be between 1 and 120.");
        }
        this.yearsOfResidency = years;
    }
 
    @Override
    public String getDocumentType() { return "Certificate of Residency"; }
 
    @Override
    public String getRequirements() { return "Valid ID; proof of residency (for example, a utility bill)"; }
 
    @Override
    public String getSpecificDetails() { return "Years of residency: " + yearsOfResidency; }
 
    @Override
    public void processRequest() {
        super.processRequest();
        System.out.println("  Residency check: confirming " + yearsOfResidency
                + " year(s) in the barangay records.");
    }
 
    @Override
    public double calculateFee() { return BASE_FEE; }
}
