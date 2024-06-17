package attempts;

import models.Answer;
import models.Question;
import models.QuestionTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionAttempt {
    private Question question;
    private List<String> writtenAnswers;
    private List<Answer> answers;
    private int correctAnswersAmount;
    private int maxScore;

    /**
     * Constructor for QuestionAttempt
     * @param question - question object
     * @param answers - answer list
     */
    public QuestionAttempt(Question question, List<Answer> answers){
        this.question = question;
        this.writtenAnswers = new ArrayList<>();
        this.answers = answers;
        this.maxScore = question.getScore();

        Set<Answer> corAns = new HashSet<>(answers);
        correctAnswersAmount = corAns.size();
    }

    public Question getQuestion() {return question;}
    public List<String> getWrittenAnswers() {return writtenAnswers;}
    public List<Answer> getAnswers() {return answers;}
    public int getMaxScore() {return maxScore;}

    /**
     * Sets the answers written by user
     * @param answers - answers written by user
     */
    public void setWrittenAnswers(List<String> answers) {writtenAnswers = answers;}

    /**
     * Get score of question given the answers
     * @return the score of the question
     */
    public int evaluateAnswers(){
        Set<Answer> corAns = new HashSet<>();
        for(String wrAns : writtenAnswers){
            for(Answer ans : answers){
                if(ans.equals(wrAns) && ans.getIsCorrect()){
                    corAns.add(ans);
                }
            }
        }
        return maxScore * corAns.size() / correctAnswersAmount;
    }
}
