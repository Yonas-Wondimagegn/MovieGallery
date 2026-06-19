public class Member extends Person {
    private int memberID;
    private String cellPhone;
    private boolean subscribed;

    public Member(int memberID, String fullName, String cellPhone, boolean subscribed) {
        super(fullName);
        this.memberID = memberID;
        this.cellPhone = cellPhone;
        this.subscribed = subscribed;
    }

    public int getMemberID() { return memberID; }
    public String getCellPhone() { return cellPhone; }
    public boolean isSubscribed() { return subscribed; }
    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone; }
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }
}
