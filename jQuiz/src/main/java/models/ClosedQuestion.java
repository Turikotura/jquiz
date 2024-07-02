package models;

import java.util.List;
import java.util.Set;

public class ClosedQuestion extends Question {
    private QuestionTypes questionType;
    private String queryText;
    private int userScore;
    private boolean guessed;
    private boolean isMulti;

    public ClosedQuestion(String text, QuestionTypes type, boolean isMulti) {
        super(0,type,text,0,null,null,0);
        this.isMulti = isMulti;
    }

    public String getQueryText() {return queryText;}
    public int getScore() {return userScore;}
    public QuestionTypes type() {return questionType;}
    public boolean isMultiAnswer() {return isMulti;}
}