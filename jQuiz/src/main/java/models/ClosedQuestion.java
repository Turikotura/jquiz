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
        super(text,correctAnswers,type);
        this.correctAnswers = correctAnswers;
        this.isMulti = isMulti;
    }

    public String getQueryText() {return queryText;}
    public int getScore() {return userScore;}
    public int type() {return questionType;}
    public List<Answer> getAnswers() {return allAnswers;}
    public boolean isMultiAnswer() {return isMulti;}
    public boolean guessAnswer(Answer ans) {return super.guessAnswer(ans);}
}