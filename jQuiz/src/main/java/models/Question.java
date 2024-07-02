package models;

import java.util.List;
import java.util.Set;

public class Question {
    private int id;
    private QuestionTypes questionType;
    private String text;
    private int quizId;
    private byte[] image;
    private String imageUrl;
    private int score;

    public Question(int id, QuestionTypes questionType, String text, int quizId, byte[] image, String imageUrl, int score) {
        this.id = id;
        this.questionType = questionType;
        this.text = text;
        this.quizId = quizId;
        this.image = image;
        this.imageUrl = imageUrl;
        this.score = score;
    }

    public int getId() {return id;}
    public QuestionTypes getQuestionType() {return questionType;}
    public String getText() {return text;}
    public int getQuizId() {return quizId;}
    public byte[] getImage() {return image;}
    public String getImageUrl() {return imageUrl;}
    public int getScore() {return score;}
}