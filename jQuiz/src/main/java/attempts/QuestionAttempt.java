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

    // Used to not grade a question more than once
    private int gottenGrade;
    private boolean wasGraded;
    // Practice mode
    private boolean isPractice;

    /**
     * Constructor for QuestionAttempt
     * @param question - question object
     * @param answers - answer list
     */
    public QuestionAttempt(Question question, List<Answer> answers, boolean isPractice){
        this.question = question;
        this.writtenAnswers = new ArrayList<>();
        this.answers = new ArrayList<>(answers);
        this.maxScore = question.getScore();

        Set<Answer> corAns = new HashSet<>();
        for(Answer answer : this.answers){
            if(answer.getIsCorrect()){
                corAns.add(answer);
            }
        }
        correctAnswersAmount = corAns.size();

        this.wasGraded = false;
        this.gottenGrade = 0;
        this.isPractice = isPractice;
    }

    public Question getQuestion() {return question;}
    public List<String> getWrittenAnswers() {return writtenAnswers;}
    public List<Answer> getAnswers() {return answers;}
    public int getMaxScore() {return maxScore;}
    public int getCorrectAnswersAmount() {return correctAnswersAmount;}
    public boolean getWasGraded() {return wasGraded;}
    public boolean getIsPractice() {return isPractice;}

    /**
     * Sets the answers written by user
     * @param answers - answers written by user
     */
    public void setWrittenAnswers(List<String> answers) {
        if(wasGraded) {
            // If was already graded, don't change the answers
            return;
        }
        writtenAnswers = new ArrayList<>(answers);
    }

    /**
     * Get score of question given the answers
     * @return the score of the question
     */
    public int evaluateAnswers(){
        if(wasGraded){
            // If was already graded, return grade
            return gottenGrade;
        }
        // Evaluate answers
        Set<Answer> corAns = new HashSet<>();
        int wrongAnswers = 0;

        for(int i = 0; i < writtenAnswers.size(); i++){
            String wrAns = writtenAnswers.get(i);
            boolean isRight = false;
            for(int j = 0; j < answers.size(); j++){
                Answer ans = answers.get(j);
                if(ans.equals(wrAns) && ans.getIsCorrect() && ((question.getQuestionType() != QuestionTypes.FILL_BLANK) || (i+1) == ans.getUniquenessId())){
                    // If answer matches and answer is correct and if it's for FILL_BLANK and it's order is correct, add to answers
                    corAns.add(ans);
                    isRight = true;
                }
            }
            if(!isRight){
                // If not, increment wrong answers
                wrongAnswers++;
            }
        }

        int correctAnswers = corAns.size();
        if(question.getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
            // For MAMC, Check the difference between right and wrong answers
            correctAnswers = Math.max(0, correctAnswers-wrongAnswers);
        }
        if(isPractice){
            // If it's practice mode, clear answers
            writtenAnswers.clear();
        }else{
            // If not, set graded to true
            wasGraded = true;
        }
        gottenGrade = maxScore * correctAnswers / correctAnswersAmount;
        return gottenGrade;
    }
}