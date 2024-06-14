package models;

import java.util.List;
import java.util.Set;

public class ClosedQuestion extends Question {
    private int questionType;
    private String queryText;
    private int userScore;
    private Set<Answer> correctAnswers;
    private List<Answer> allAnswers;
    private boolean guessed;
    private boolean isMulti;

    public ClosedQuestion(String text, List<Answer> answers, Set<Answer> correctAnswers, int type, boolean isMulti) {
        super(0,type,text,0,"",0, null);
        this.correctAnswers = correctAnswers;
        this.isMulti = isMulti;
    }

    public String getQueryText() {return queryText;}
    public int getScore() {return userScore;}
    public int type() {return questionType;}
    public List<Answer> getAnswers() {return allAnswers;}
    public boolean isMultiAnswer() {return isMulti;}
}