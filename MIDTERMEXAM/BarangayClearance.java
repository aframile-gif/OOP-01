public class BarangayClearance extends DocumentRequest implements FeeApplicable {
    private static final double BASE_FEE = 50.0;   // sample fee
    private String purpose;
 
    public BarangayClearance(String requestId, Resident resident, String purpose) {
        super(requestId, resident);                // initialise the inherited fields 
        setPurpose(purpose);
        setFee(calculateFee());                    // store the computed fee in DocumentRequest
    }
 
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) {
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose cannot be empty.");
        }
        this.purpose = purpose.trim();
    }
 
    @Override
    public String getDocumentType() { return "Barangay Clearance"; }
 
    @Override
    public String getRequirements() { return "Valid ID; proof of residency"; }
 
    @Override
    public String getSpecificDetails() { return "Purpose: " + purpose; }
 
    @Override
    public void processRequest() {
        super.processRequest();                    // reuse the common step
        System.out.println("  Clearance check: verifying no pending complaints for "
                + getResident().getFullName() + ".");
    }
 
    @Override
    public double calculateFee() { return BASE_FEE; }
}
