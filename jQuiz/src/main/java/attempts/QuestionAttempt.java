package attempts;

import models.Answer;
import models.Question;
import models.QuestionTypes;

import java.util.*;

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
        Collections.shuffle(this.answers);
        this.maxScore = question.getScore();

        Set<Answer> corAns = new HashSet<>();
        for(Answer answer : this.answers){
            if(answer.getIsCorrect()){
                corAns.add(answer);
            }
        }
        correctAnswersAmount = corAns.size();
    }

    public Question getQuestion() {return question;}
    public List<String> getWrittenAnswers() {return writtenAnswers;}
    public List<Answer> getAnswers() {return answers;}
    public int getMaxScore() {return maxScore;}
    public int getCorrectAnswersAmount() {return correctAnswersAmount;}

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
        int wrongAnswers = 0;
        for(String wrAns : writtenAnswers){
            boolean isRight = false;
            for(Answer ans : answers){
                if(ans.equals(wrAns) && ans.getIsCorrect()){
                    corAns.add(ans);
                    isRight = true;
                }
            }
            if(!isRight){
                wrongAnswers++;
            }
        }
        int correctAnswers = corAns.size();
        if(question.getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
            correctAnswers = Math.max(0, correctAnswers-wrongAnswers);
        }
        return maxScore * correctAnswers / correctAnswersAmount;
    }
}