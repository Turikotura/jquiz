package models;

import java.util.Date;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private User author;
    private Date createdAt;
    private int time;
    private String thumbnail;
    private boolean shouldMixUp;
    private boolean showAll;
    private boolean autoCorrect;
    private boolean allowPractice;
    private String description;
    private List<Integer> questionIds;


    public Quiz(
            int id,
            String title,
            User author,
            Date createdAt,
            int time,
            String thumbnail,
            boolean shouldMixUp,
            boolean showAll,
            boolean autoCorrect,
            boolean allowPractice,
            String description,
            List<Integer> questionIds
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.time = time;
        this.thumbnail = thumbnail;
        this.shouldMixUp = shouldMixUp;
        this.showAll = showAll;
        this.autoCorrect = autoCorrect;
        this.allowPractice = allowPractice;
        this.description = description;
        this.questionIds = questionIds;
    }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public Date getCreatedAt() {return createdAt;}
    public int getMaxTime() {return time;}
    public String getDescription() {return description;}
    public String getThumbnail() {return thumbnail;}
    public boolean getShouldMixUp() {return shouldMixUp;}
    public boolean getShowAll() {return showAll;}
    public boolean getAutoCorrect() {return autoCorrect;}
    public boolean getAllowPractice() {return allowPractice;}
    public User getAuthor() {return author;}
    public List<Integer> getQuestions() {return questionIds;}
}
