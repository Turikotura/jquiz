package models;

import java.util.Date;

public class History {
    private int id;
    private int userId;
    private int quizId;
    private int grade;
    private Date completedAt;
    private int writingTime;
    public History(int id, int userId, int quizId, int grade, Date completedAt, int writingTime){
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.grade = grade;
        this.completedAt = completedAt;
        this.writingTime = writingTime;
    }
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getQuizId() { return quizId; }
    public int getGrade() { return grade; }
    public Date getCompletedAt() { return completedAt; }
    public int getWritingTime() { return writingTime; }
}
