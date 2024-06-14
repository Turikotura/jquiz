package models;

import java.util.Set;

public class Question {
    private int id;
    private int questionType;
    private String queryText;

    private int userScore;
    private Set<Integer> correctAnswerIds;
    private boolean guessed;


    public Question(String text, Set<Answer> answers, int type) {
        this.queryText = text;
        this.correctAnswers = answers;
        this.userScore = 0;
        this.guessed = false;
        this.questionType = type;
    }

    public String getQueryText() {return queryText;}
    public int type() {return questionType;}
    public int getScore() {return userScore;}

    public boolean guessAnswer(Answer ans) {
        if(!guessed) {
            guessed = true;
            if(correctAnswers.contains(ans)) {
                userScore = 1;
                return true;
            } else return false;
        }
        return false;
    }
}