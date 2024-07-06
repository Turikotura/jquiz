package attempts;

import models.Answer;
import models.History;
import models.Question;
import models.Quiz;

import java.util.*;

class IdSorter implements Comparator<QuestionAttempt>{
    @Override
    public int compare(QuestionAttempt o1, QuestionAttempt o2) {
        return o1.getQuestion().getId() - o2.getQuestion().getId();
    }
}

/**
 * Class to store quiz attempts
 */
public class QuizAttemptsController {
    /**
     * Collection to store current quiz attempts
     */
    private Map<Integer,QuizAttempt> quizAttempts;
    /**
     * Id to give to new quiz attempts
     */
    private int lastId;
    /**
     * Id of the user the attempts are made by
     */
    private int userId;

    /**
     * Constructor for QuizAttemptsController
     * @param userId - the user id the controller is made for
     */
    public QuizAttemptsController(int userId){
        quizAttempts = new HashMap<>();
        lastId = -1;
        this.userId = userId;
    }

    /**
     * Add a quiz the user is currently attempting
     * @param quiz - the quiz the user is attempting
     * @param practice - tells if the quiz attempt is a practice attempt
     * @param questionToAnswers - the mapping of the quizzes questions to answers
     * @return the id of the quiz attempt
     */
    public int attemptQuiz(Quiz quiz, boolean practice, Map<Question,List<Answer>> questionToAnswers){
        List<QuestionAttempt> qas = new ArrayList<>();
        for(Map.Entry<Question,List<Answer>> entry : questionToAnswers.entrySet()){
            qas.add(new QuestionAttempt(entry.getKey(),entry.getValue(),practice));
        }
        if(quiz.getShouldMixUp()){
            Collections.shuffle(qas);
        }else{
            qas.sort(new IdSorter());
        }
        QuizAttempt qa = new QuizAttempt(++lastId, quiz, practice, qas);
        quizAttempts.put(lastId, qa);
        return lastId;
    }

    /**
     * Finish an attempted quiz
     * @param qaId - quiz attempt id
     * @return history the quiz attempt gives
     */
    public History finishQuiz(int qaId){
        Date done = new Date();
        QuizAttempt qa = quizAttempts.get(qaId);
        History h = null;
        if(!qa.getIsPractice()){
            h = new History(-1,userId,qa.getQuizId(),qa.evaluateQuiz(),done,(int)(done.getTime()-qa.getStartTime().getTime()),qa.getIsPractice());
        }
        quizAttempts.remove(qaId);
        return h;
    }

    /**
     * Get all the ids of the quiz attempt
     * @return ids of the quiz attempt
     */
    public List<Integer> getAttemptIds(){
        return new ArrayList<>(quizAttempts.keySet());
    }

    /**
     * Get quiz attempt of the given id
     * @param id - the id of the quiz attempt
     * @return quiz attempt object
     */
    public QuizAttempt getQuizAttemptById(int id){
        return quizAttempts.get(id);
    }

    /**
     * Get current user id
     * @return current user id
     */
    public int getUserId() {return userId;}
}