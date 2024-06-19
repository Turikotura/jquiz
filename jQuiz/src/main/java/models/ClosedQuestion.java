package models;

import java.util.List;
import java.util.Set;

public class ClosedQuestion extends Question {
    private int questionType;
    private String queryText;
    private int userScore;
    private List<Integer> answerIds;
    private boolean guessed;
    private boolean isMulti;

    public ClosedQuestion(String text, List<Integer> answerIds, QuestionTypes type, boolean isMulti) {
        super(0,type,text,0,"",0, answerIds);
        this.isMulti = isMulti;
    }

    public String getQueryText() {return queryText;}
    public int getScore() {return userScore;}
    public int type() {return questionType;}
    public List<Integer> getAnswerIds() {return answerIds;}
    public boolean isMultiAnswer() {return isMulti;}
}