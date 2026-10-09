public class BusinessClearance extends DocumentRequest implements FeeApplicable {
    private static final double BASE_FEE = 200.0;        // sample fee
    private static final double MICRO_BUSINESS_FEE = 100.0;
    private String businessName;
    private String businessType;
    private String businessAddress;
 
    public BusinessClearance(String requestId, Resident resident, String businessName, String businessType, String businessAddress) {
    super(requestId, resident);
    this.businessName = requireText(businessName, "Business name");
    this.businessType = requireText(businessType, "Business type");
    this.businessAddress = requireText(businessAddress, "Business address");
    setFee(calculateFee()); 
}
 
    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        return value.trim();
    }
 
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
    public String getBusinessAddress() { return businessAddress; }
 
    @Override
    public String getDocumentType() { return "Business Clearance"; }
 
    @Override
    public String getRequirements() { return "Valid ID; business permit or application; proof of business location (lease or ownership)"; }
 
    @Override
    public String getSpecificDetails() {
        return "Business: " + businessName + " (" + businessType + "), " + businessAddress;
    }
 
    @Override
    public void processRequest() {
        super.processRequest();
        System.out.println("  Business check: verifying details of " + businessName + ".");
    }
 
    //  produces different outcomes sari-sari stores get the lower sample rate
    @Override
    public double calculateFee() {
        if (businessType.equalsIgnoreCase("Sari-sari store")) {
            return MICRO_BUSINESS_FEE;
        }
        return BASE_FEE;
    }
}
