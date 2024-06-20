package attempts;

import models.History;
import models.Quiz;

import java.util.*;

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
    }

    /**
     * Add a quiz the user is currently attempting
     * @param quiz - the quiz the user is attempting
     * @param questions - the quiz questions
     * @return the id of the quiz attempt
     */
    public int attemptQuiz(Quiz quiz, List<QuestionAttempt> questions){
        if(quiz.getShouldMixUp()){
            Collections.shuffle(questions);
        }
        QuizAttempt qa = new QuizAttempt(++lastId, quiz, questions);
        quizAttempts.put(lastId, qa);
        return lastId;
    }

    /**
     * Finish an attempted quiz
     * @param qaId - quiz attempt id
     * @return history the quiz attempt gives
     */
    public History finishQuiz(int qaId){
        QuizAttempt qa = quizAttempts.get(qaId);
        Date done = new Date();
        History h = new History(-1,userId,qa.getQuizId(),qa.evaluateQuiz(),done,(int)(done.getTime()-qa.getStartTime().getTime())/1000);
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
}