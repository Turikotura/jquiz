package models;

import java.util.Date;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private User author;
    private Date created_at;
    private int time;
    private String thumbnail;
    private boolean shouldMixUp;
    private boolean showAll;
    private boolean autoCorrect;
    private boolean allowPractice;
    private String description;
    private List<Question> questions;


    public Quiz(
            int id,
            String title,
            User author,
            Date created_at,
            int time,
            String thumbnail,
            boolean shouldMixUp,
            boolean showAll,
            boolean autoCorrect,
            boolean allowPractice,
            String description,
            List<Question> questions
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.created_at = created_at;
        this.time = time;
        this.thumbnail = thumbnail;
        this.shouldMixUp = shouldMixUp;
        this.showAll = showAll;
        this.autoCorrect = autoCorrect;
        this.allowPractice = allowPractice;
        this.description = description;
        this.questions = questions;
    }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public Date getCreatedAt() {return created_at;}
    public int getMaxTime() {return time;}
    public String getDescription() {return description;}
    public String getThumbnail() {return thumbnail;}
    public boolean getShouldMixUp() {return shouldMixUp;}
    public boolean getShowAll() {return showAll;}
    public boolean getAutoCorrect() {return autoCorrect;}
    public boolean getAllowPractice() {return allowPractice;}
    public User getAuthor() {return author;}
    public List<Question> getQuestions() {return questions;}
}
