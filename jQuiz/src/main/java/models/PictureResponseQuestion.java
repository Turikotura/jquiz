package models;

import java.util.Set;

public class PictureResponseQuestion extends Question {
    private QuestionTypes questionType;
    private String queryText;
    private String pictureLink;
    private int userScore;
    private Set<String> correctAnswers;
    private boolean guessed;


    public PictureResponseQuestion(String text, Set<Answer> answers, String link, QuestionTypes type) {
        super(0,type,text,0,null, null,0, null);
        this.pictureLink = link;
    }

    public String getQueryText() {return queryText;}
    public QuestionTypes type() {return questionType;}
    public String getPictureLink() {return pictureLink;}
    public int getScore() {return userScore;}
}