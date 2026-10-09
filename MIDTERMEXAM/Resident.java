public class Resident {
    private final String residentId;   // read only
    private String fullName;
    private String address;
    private String contactNumber;
    private String civilStatus;
 
    public Resident(String residentId, String fullName, String address,
                    String contactNumber, String civilStatus) {
        this.residentId = residentId;
        setFullName(fullName);           // reuse the setters 
        setAddress(address);
        setContactNumber(contactNumber);
        setCivilStatus(civilStatus);
    }
 
    public String getResidentId() { return residentId; }
 
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }
 
    public String getAddress() { return address; }
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address.trim();
    }
 
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) {
        // Philippine mobile numbers: 11 digits
        if (contactNumber == null || !contactNumber.matches("\\d{11}")) {
            throw new IllegalArgumentException("Contact number must be exactly 11 digits.");
        }
        this.contactNumber = contactNumber;
    }
 
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) {
        if (civilStatus == null || civilStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Civil status cannot be empty.");
        }
        this.civilStatus = civilStatus.trim();
    }
 
    @Override
    public String toString() {
        return residentId + " - " + fullName;
    }
}
