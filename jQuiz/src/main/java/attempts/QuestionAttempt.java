package attempts;

import models.Answer;
import models.Question;
import models.QuestionTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionAttempt {
    private int id;
    private QuestionTypes questionType;
    private String text;
    String imageUrl;
    private List<String> writtenAnswers;
    private List<Answer> answers;
    private int correctAnswersAmount;
    private int maxScore;

    public QuestionAttempt(Question question, List<Answer> answers){
        this.id = question.getId();
        this.questionType = question.getQuestionType();
        this.text = question.getText();
        this.imageUrl = question.getImageUrl();
        this.writtenAnswers = new ArrayList<>();
        this.answers = answers;
        this.maxScore = question.getScore();

        Set<Answer> corAns = new HashSet<>(answers);
        correctAnswersAmount = corAns.size();
    }

    public int getId() {return id;}
    public QuestionTypes getQuestionType() {return questionType;}
    public String getText() {return text;}
    public String getImageUrl() {return imageUrl;}
    public List<String> getWrittenAnswers() {return writtenAnswers;}
    public List<Answer> getAnswers() {return answers;}
    public int getMaxScore() {return maxScore;}

    public void setWrittenAnswers(List<String> answers) {writtenAnswers = answers;}
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
