package models;

import java.util.Date;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private int authorId;
    private Date createdAt;
    private int time;
    private byte[] thumbnail;
    private String thumbnail_url;
    private boolean shouldMixUp;
    private boolean showAll;
    private boolean autoCorrect;
    private boolean allowPractice;
    private String description;
    private String category;
    private List<Integer> questionIds;
    private int totalPlayCount;
    private int lastMonthPlayCount;


    public Quiz(
            int id,
            String title,
            int authorId,
            Date createdAt,
            int time,
            byte[] thumbnail,
            String thumbnail_url,
            boolean shouldMixUp,
            boolean showAll,
            boolean autoCorrect,
            boolean allowPractice,
            String description,
            String category,
            List<Integer> questionIds,
            int totalPlayCount,
            int lastMonthPlayCount
    ) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.time = time;
        this.thumbnail = thumbnail;
        this.thumbnail_url = thumbnail_url;
        this.shouldMixUp = shouldMixUp;
        this.showAll = showAll;
        this.autoCorrect = autoCorrect;
        this.allowPractice = allowPractice;
        this.description = description;
        this.category = category;
        this.questionIds = questionIds;
        this.totalPlayCount = totalPlayCount;
        this.lastMonthPlayCount = lastMonthPlayCount;
    }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public Date getCreatedAt() {return createdAt;}
    public int getMaxTime() {return time;}
    public String getDescription() {return description;}
    public byte[] getThumbnail() {return thumbnail;}
    public String getThumbnailUrl() {return thumbnail_url;}
    public boolean getShouldMixUp() {return shouldMixUp;}
    public boolean getShowAll() {return showAll;}
    public boolean getAutoCorrect() {return autoCorrect;}
    public boolean getAllowPractice() {return allowPractice;}
    public int getAuthorId() {return authorId;}
    public String getCategory() {return category;}
    public int getTotalPlayCount() {return totalPlayCount;}
    public int getLastMonthPlayCount() {return lastMonthPlayCount;}
    public List<Integer> getQuestions() {return questionIds;}
}
