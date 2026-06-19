public class Producer extends Person {
    private int producerID;
    private String phoneNumber;

    public Producer(int producerID, String fullName, String phoneNumber) {
        super(fullName);
        this.producerID = producerID;
        this.phoneNumber = phoneNumber;
    }

    public int getProducerID() { return producerID; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
