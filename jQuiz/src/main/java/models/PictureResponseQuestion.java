package models;

import java.util.Set;

public class PictureResponseQuestion extends Question {
    private int questionType;
    private String queryText;
    private String pictureLink;
    private int userScore;
    private Set<String> correctAnswers;
    private boolean guessed;


    public PictureResponseQuestion(String text, Set<Answer> answers, String link, int type) {
        super(text,answers,type);
        this.pictureLink = link;
    }

    public String getQueryText() {return queryText;}
    public int type() {return questionType;}
    public String getPictureLink() {return pictureLink;}
    public int getScore() {return userScore;}
    public boolean guessAnswer(Answer ans) { return super.guessAnswer(ans); }
}