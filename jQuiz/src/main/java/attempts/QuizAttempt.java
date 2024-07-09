package attempts;

import models.Quiz;

import java.util.*;

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
    private String description;
    private int maxScore;
    private List<QuestionAttempt> questions;
    private int onQuestionIndex;

    // Used to not grade the quiz more than once
    private int gottenGrade;
    private boolean wasGraded;

    // Practice mode
    private boolean isPractice;
    private Map<Integer,Integer> qToAnsRow;
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
        this.description = quiz.getDescription();
        this.onQuestionIndex = 0;

        this.questions = new ArrayList<>(questions);
        for(QuestionAttempt qa : questions){
            this.maxScore += qa.getMaxScore();
        }

        this.wasGraded = false;
        this.gottenGrade = 0;

        this.isPractice = isPractice;
        this.qToAnsRow = null;
        if(this.isPractice){
            this.showAll = false;
            this.autoCorrect = false;

            this.qToAnsRow = new HashMap<Integer,Integer>();
            for(int i = 0; i < questions.size(); i++){
                this.qToAnsRow.put(i,0);
            }
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
        if(wasGraded){
            // If was already graded, return the grade
            return gottenGrade;
        }

        if(!isPractice){
            // If it's not in practice mode, then grade the quiz so it doesn't change
            wasGraded = true;
        }
        gottenGrade = 0;

        long timeBetween = ((new Date()).getTime() - startTime.getTime())/1000;
        if(timeBetween <= time){
            // Check if on time
            for(QuestionAttempt qa : questions){
                gottenGrade += qa.evaluateAnswers();
            }
        }
        return gottenGrade;
    }

    /**
     * Set the current question index
     * @param ind - the new current question index
     */
    public void setOnQuestionIndex(int ind) {onQuestionIndex = ind;}

    /**
     * Set OnQuestionIndex to a new random question
     */
    private void setOnQuestionIndexPractice(){
        if(qToAnsRow.isEmpty()){
            // If no more questions, set index to invalid position
            onQuestionIndex = -1;
            return;
        }
        // Else return next random question
        List<Integer> keysAsArray = new ArrayList<>(qToAnsRow.keySet());
        Random r = new Random();
        onQuestionIndex = keysAsArray.get(r.nextInt(keysAsArray.size()));
    }

    /**
     * Evaluate question in practice mode
     * @param qind - the index of the question
     * @return was the answers correct or not
     */
    public boolean evaluateQuestionPractice(int qind){
        if(!isPractice){
            // If not in practice mode, can't use method
            return false;
        }

        // Evaluate practice answer
        int evalAns = questions.get(qind).evaluateAnswers();
        boolean res = (evalAns == questions.get(qind).getMaxScore());
        if(res){
            // If right, increment conseq
            qToAnsRow.put(qind,qToAnsRow.get(qind)+1);
            if(qToAnsRow.get(qind) >= 3){
                // If 3 right in a row, remove question from question pool
                qToAnsRow.remove(qind);
            }
        }else{
            // If wrong, reset
            qToAnsRow.put(qind,0);
        }
        setOnQuestionIndexPractice();
        return res;
    }
}