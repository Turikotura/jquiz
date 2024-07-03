package models;

import java.util.Date;

public class Announcement {
    private int id;
    private int authorId;
    private String title;
    private String text;
    private Date createdAt;

    public Announcement(int id, int authorId, String title, String text, Date createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
    }

    public int getId() { return this.id; }
    public int getAuthorId() { return this.authorId; }
    public String getTitle() { return this.title; }
    public String getText() { return this.text; }
    public Date getCreatedAt() { return this.createdAt; }
}
