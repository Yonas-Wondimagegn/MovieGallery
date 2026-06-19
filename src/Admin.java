public class Admin extends Person {
    private int adminID;
    private String userAccount;
    private String password;

    public Admin(int adminID, String fullName, String userAccount, String password) {
        super(fullName);
        this.adminID = adminID;
        this.userAccount = userAccount;
        this.password = password;
    }

    public int getAdminID() { return adminID; }
    public String getUserAccount() { return userAccount; }
    public String getPassword() { return password; }
}
