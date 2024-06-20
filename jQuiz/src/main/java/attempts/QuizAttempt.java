package attempts;

import models.Quiz;

import java.util.Date;
import java.util.List;

public class QuizAttempt {
    private int id;
    private int quizId;
    private String title;
    private int authorId;
    private int time;
    private Date startTime;
    private byte[] thumbnail;
    private boolean showAll;
    private boolean autoCorrect;
    private boolean isPractice;
    private String description;
    private int maxScore;
    private List<QuestionAttempt> questions;
    private int onQuestionIndex;

    /**
     * Constructor for QuizAttempt
     * @param id - id of the quiz attempt
     * @param quiz - quiz information
     * @param questions - quiz questions
     */
    public QuizAttempt(int id, Quiz quiz, boolean isPractice, List<QuestionAttempt> questions){
        this.id = id;
        this.quizId = quiz.getId();
        this.title = quiz.getTitle();
        this.authorId = quiz.getAuthorId();
        this.time = quiz.getMaxTime();
        this.startTime = new Date();
        this.thumbnail = quiz.getThumbnail();
        this.showAll = quiz.getShowAll();
        this.autoCorrect = quiz.getAutoCorrect();
        this.isPractice = isPractice;
        this.description = quiz.getDescription();
        this.onQuestionIndex = 0;

        this.questions = questions;
        for(QuestionAttempt qa : questions){
            this.maxScore += qa.getMaxScore();
        }
    }

    public int getId() {return id;}
    public int getQuizId() {return quizId;}
    public String getTitle() {return title;}
    public int getAuthorId() {return authorId;}
    public int getTime() {return time;}
    public Date getStartTime() {return startTime;}
    public byte[] getThumbnail() {return thumbnail;}
    public boolean getShowAll() {return showAll;}
    public boolean getAutoCorrect() {return autoCorrect;}
    public boolean getIsPractice() {return isPractice;}
    public String getDescription() {return description;}
    public int getOnQuestionIndex() {return onQuestionIndex;}
    public List<QuestionAttempt> getQuestions() {return questions;}
    public int getMaxScore() {return maxScore;}

    /**
     * Get the quiz score
     * @return quiz score
     */
    public int evaluateQuiz(){
        long timeBetween = ((new Date()).getTime() - startTime.getTime())/1000;
        if(timeBetween > time){
            return 0;
        }
        int res = 0;
        for(QuestionAttempt qa : questions){
            res += qa.evaluateAnswers();
        }
        return res;
    }

    /**
     * Set the current question index
     * @param ind - the new current question index
     */
    public void setOnQuestionIndex(int ind) {onQuestionIndex = ind;}
}