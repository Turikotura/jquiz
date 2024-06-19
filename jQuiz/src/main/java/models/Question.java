package models;

import java.util.List;
import java.util.Set;

public class Question {
    private int id;
    private QuestionTypes questionType;
    private String text;
    private int quizId;
    private String imageUrl;
    private int score;
    private List<Integer> answerIds;

    public Question(int id, QuestionTypes questionType, String text, int quizId, String imageUrl, int score, List<Integer> answerIds) {
        this.id = id;
        this.questionType = questionType;
        this.text = text;
        this.quizId = quizId;
        this.imageUrl = imageUrl;
        this.score = score;
        this.answerIds = answerIds;
    }

    public int getId() {return id;}
    public QuestionTypes getQuestionType() {return questionType;}
    public String getText() {return text;}
    public int getQuizId() {return quizId;}
    public String getImageUrl() {return imageUrl;}
    public int getScore() {return score;}
    public List<Integer> getAnswerIds() {return answerIds;}
}