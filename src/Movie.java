public class Movie {
    private int movieID;
    private String movieCode; // e.g. M001
    private String title;
    private String type;
    private float lengthHours;
    private int numberOfActors;
    private int producerID;
    private int categoryID;

    public Movie(int movieID, String movieCode, String title, String type,
        float lengthHours, int numberOfActors, int producerID, int categoryID) {
        this.movieID = movieID;
        this.movieCode = movieCode;
        this.title = title;
        this.type = type;
        this.lengthHours = lengthHours;
        this.numberOfActors = numberOfActors;
        this.producerID = producerID;
        this.categoryID = categoryID;
    }

    public int getMovieID() { return movieID; }
    public String getMovieCode() { return movieCode; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public float getLengthHours() { return lengthHours; }
    public int getNumberOfActors() { return numberOfActors; }
    public int getProducerID() { return producerID; }
    public int getCategoryID() { return categoryID; }

    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setLengthHours(float lengthHours) { this.lengthHours = lengthHours; }
    public void setNumberOfActors(int numberOfActors) { this.numberOfActors = numberOfActors; }
}
