package models;

public class Rating {
    private int id;
    private int rating;
    private int quizId;
    private int userId;
    public Rating(int id, int rating, int quizId, int userId){
        this.id = id;
        this.rating = rating;
        this.quizId = quizId;
        this.userId = userId;
    }
    public int getId() {return id;}
    public int getRating() {return rating;}
    public int getQuizId() {return quizId;}
    public int getUserId() {return userId;}
}