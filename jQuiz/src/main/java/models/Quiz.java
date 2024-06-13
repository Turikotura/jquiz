package models;

import java.util.List;

public class Quiz {
    private int id;
    private int maxTime;
    private String title;
    private String description;
    private String thumbnailLink;
    private List<Question> questions;
    private boolean shouldMixUp;
    private boolean showAll;
    private boolean autoCorrect;
    private boolean allowPractice;
    private User author;


    public Quiz(int id, int maxTime, String title, String description, String thumbnailLink, boolean shouldMixUp, boolean showAll, boolean autoCorrect, boolean allowPractice, User author) {
        this.id = id;
        this.maxTime = maxTime;
        this.title = title;
        this.description = description;
        this.thumbnailLink = thumbnailLink;
        this.shouldMixUp = shouldMixUp;
        this.showAll = showAll;
        this.autoCorrect = autoCorrect;
        this.allowPractice = allowPractice;
        this.author = author;
    }

    public int getId() {return id;}
    public int getMaxTime() {return maxTime;}
    public String getTitle() {return title;}
    public String getDesc() {return description;}
    public String getThumbnail() {return thumbnailLink;}
    public boolean shouldRandomize() {return shouldMixUp;}
    public boolean loadAll() {return showAll;}
    public boolean computerGrades() {return autoCorrect;}
    public boolean practiceAllowed() {return allowPractice;}
    public User getAuthor() {return author;}
}
