package models;

import java.util.Date;

public class Achievement {
    private int id;
    private String name;
    private String description;
    private String image;
    private Date acquireDate;
    public Achievement(int id, String name, String description, String image, Date acquireDate){
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.acquireDate = acquireDate;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public Date getAcquireDate() { return acquireDate; }

    @Override
    public boolean equals(Object o) {
        Achievement other = (Achievement) o;
        return this.id == other.id;
    }
}
