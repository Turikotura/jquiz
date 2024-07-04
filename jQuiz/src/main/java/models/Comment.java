package models;

import java.util.Date;

public class Comment {
    private int id;
    private String text;
    private Date writtenTime;
    private int userId;
    private int quizId;
    public Comment(int id, String text, Date writtenTime, int userId, int quizId){
        this.id = id;
        this.text = text;
        this.writtenTime = writtenTime;
        this.userId = userId;
        this.quizId = quizId;
    }
    public int getId() {return id;}
    public String getText() {return text;}
    public Date getWrittenTime() {return writtenTime;}
    public int getUserId() {return userId;}
    public int getQuizId() {return quizId;}
}