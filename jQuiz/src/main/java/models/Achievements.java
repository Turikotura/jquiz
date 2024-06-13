package models;

public class Achievements {
    private int id;
    private String name;
    private String description;
    private String image;
    public Achievements(int id, String name, String description, String image){
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
}
