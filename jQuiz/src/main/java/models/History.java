package models;

import java.util.Date;

public class History {
    private int id;
    private User user;
    private String quiz;
    private int grade;
    private Date completed_at;
    private int writing_time;
    public History(int id, User user, String quiz, int grade, Date completed_at, int writing_time){
        this.id = id;
        this.user = user;
        this.quiz = quiz;
        this.grade = grade;
        this.completed_at = completed_at;
        this.writing_time = writing_time;
    }
    public int getId() { return id; }
    public User getUser() { return user; }
    public String getQuiz() { return quiz; }
    public int getGrade() { return grade; }
    public Date getCompleted_at() { return completed_at; }
    public int getWriting_time() { return writing_time; }
}
