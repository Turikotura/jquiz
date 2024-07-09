package models;

import java.util.Date;

public class Activity implements Comparable<Activity>{
    private Date activityTime;
    private int userId;
    private String username;
    private String text;
    private ActivityTypes type;
    private int nextId;
    private String nextText;
    public Activity(Date activityTime, int userId, String username, String text, ActivityTypes type, int nextId, String nextText){
        this.activityTime = activityTime;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.type = type;
        this.nextId = nextId;
        this.nextText = nextText;
    }
    public Date getActivityTime() {return activityTime;}
    public int getUserId() {return userId;}
    public String getUsername() {return username;}
    public String getText() {return text;}
    public ActivityTypes getType() {return type;}
    public int getNextId() {return nextId;}
    public String getNextText() {return nextText;}

    @Override
    public int compareTo(Activity o) {
        return activityTime.compareTo(o.getActivityTime());
    }
}
