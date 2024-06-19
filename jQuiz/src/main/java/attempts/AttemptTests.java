package attempts;

import junit.framework.TestCase;
import models.*;

import java.util.*;

public class AttemptTests extends TestCase {
    private QuizAttemptsController qac;
    private List<Quiz> quizzes;
    private List<List<QuestionAttempt>> questionLists;
    private List<QuizAttempt> quizzAttempts;
    private List<QuestionAttempt> questions;
    private List<Answer> answers;
    private List<Answer> createAnswers(int quizId, int questionId, int n){
        List<Answer> res = new ArrayList<>();
        for(int i = 0; i < n; i++){
            Answer a = new Answer(answers.size(),String.format("answer %d",answers.size()),true,questionId,0);
            res.add(a);
            answers.add(a);
        }
        if(quizId == 0 && questionId == 0){
            Answer a = new Answer(answers.size(),"another correct answer",true,questionId,1);
            res.add(a);
            answers.add(a);
            a = new Answer(answers.size(),"wrong answer",false,questionId,0);
            res.add(a);
            answers.add(a);
        }
        if(quizId == 0 && questionId == 1){
            Answer a = new Answer(answers.size(),"another correct answer 1",true,questionId,1);
            res.add(a);
            answers.add(a);
            a = new Answer(answers.size(),"another correct answer 2",true,questionId,2);
            res.add(a);
            answers.add(a);
        }
        if(quizId == 0 && questionId == 2){
            Answer a = new Answer(answers.size(),"wrong answer 1",false,questionId,0);
            res.add(a);
            answers.add(a);
            a = new Answer(answers.size(),"wrong answer 2",false,questionId,0);
            res.add(a);
            answers.add(a);
        }
        return res;
    }
    private List<QuestionAttempt> createQuestionAttempts(int quizId, int n){
        List<QuestionAttempt> res = new ArrayList<>();
        for(int i = 0; i < n; i++){
            Question q = new Question(questions.size(), QuestionTypes.RESPONSE, String.format("question %d",i),quizId,null,(i+1)*5,null);
            QuestionAttempt qa = new QuestionAttempt(q, createAnswers(quizId, i, 3));
            res.add(qa);
            questions.add(qa);
        }
        return res;
    }
    private List<QuizAttempt> createQuizAttempts(int n){
        List<QuizAttempt> res = new ArrayList<>();
        for(int i = 0; i < n; i++){
            Quiz q = new Quiz(quizzes.size(),String.format("quiz %d",i),0,(new Date()),(i+1),null,false,false,false,false,String.format("Description for quiz %d",i),null,0,0);
            quizzes.add(q);
            List<QuestionAttempt> qas = createQuestionAttempts(i,3);
            questionLists.add(qas);
            QuizAttempt qa = new QuizAttempt(i,q,qas);
            res.add(qa);
            quizzAttempts.add(qa);
        }
        return res;
    }
    /*
    quiz 0
        question 0
            answer 0
            answer 1
            answer 2
            another correct answer
            wrong answer
        question 1
            answer 5
            answer 6
            answer 7
            another correct answer 1
            another correct answer 2
        question 2
            answer 10
            answer 11
            answer 12
            wrong answer 1
            wrong answer 2
    quiz 1
        question 3
            answer 15
            answer 16
            answer 17
        question 4
            answer 18
            answer 19
            answer 20
        question 5
            answer 21
            answer 22
            answer 23
   quiz 2
        question 6
            answer 24
            answer 25
            answer 26
        question 7
            answer 27
            answer 28
            answer 29
        question 8
            answer 30
            answer 31
            answer 32
     */
    protected void setUp(){
        answers = new ArrayList<>();
        questions = new ArrayList<>();
        quizzes = new ArrayList<>();
        quizzAttempts = new ArrayList<>();
        questionLists = new ArrayList<>();

        createQuizAttempts(3);

        qac = new QuizAttemptsController(0);
    }

    /**
     * Test QuestionAttempt class
     */
    public void testQuestionAttempt(){
        // Check if correct answer amount is right
        assertEquals(1,questions.get(3).getCorrectAnswersAmount());
        assertEquals(2,questions.get(0).getCorrectAnswersAmount());
        assertEquals(3,questions.get(1).getCorrectAnswersAmount());

        // Check if question evaluation yield correct grade

        // Correct answer
        List<String> writtenAnswers = new ArrayList<String>();
        writtenAnswers.add("answer 0");
        questions.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(5*1/2,questions.get(0).evaluateAnswers());

        // Correct answer with same meaning
        writtenAnswers.clear();
        writtenAnswers.add("answer 2");
        writtenAnswers.add("answer 0");
        questions.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(5*1/2,questions.get(0).evaluateAnswers());

        // Wrong answer
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        questions.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questions.get(0).evaluateAnswers());

        // Random answer
        writtenAnswers.clear();
        writtenAnswers.add("random answer");
        questions.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questions.get(0).evaluateAnswers());

        // Almost all correct
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        questions.get(1).setWrittenAnswers(writtenAnswers);
        assertEquals(10 * 2 / 3, questions.get(1).evaluateAnswers());

        // Almost correct duplicate
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        questions.get(1).setWrittenAnswers(writtenAnswers);
        assertEquals(10 * 2 / 3, questions.get(1).evaluateAnswers());

        // All correct
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        writtenAnswers.add("answer 7");
        questions.get(1).setWrittenAnswers(writtenAnswers);
        assertEquals(10, questions.get(1).evaluateAnswers());

        // All wrong
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer 2");
        writtenAnswers.add("wrong answer 1");
        questions.get(2).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questions.get(2).evaluateAnswers());

        // Combination
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("random answer");
        questions.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(5*1/2, questions.get(0).evaluateAnswers());
    }

    public void testQuizAttempt() throws InterruptedException {
        // Basic tests
        assertEquals(30, quizzAttempts.get(0).getMaxScore());
        assertEquals(quizzes.get(0).getTitle(), quizzAttempts.get(0).getTitle());
        assertEquals(quizzes.get(0).getAuthorId(), quizzAttempts.get(0).getAuthorId());
        assertEquals(quizzes.get(0).getMaxTime(), quizzAttempts.get(0).getTime());
        assertEquals(quizzes.get(0).getThumbnail(), quizzAttempts.get(0).getThumbnail());
        assertEquals(quizzes.get(0).getShowAll(), quizzAttempts.get(0).getShowAll());
        assertEquals(quizzes.get(0).getAutoCorrect(), quizzAttempts.get(0).getAutoCorrect());
        assertEquals(quizzes.get(0).getAllowPractice(), quizzAttempts.get(0).getAllowPractice());
        assertEquals(quizzes.get(0).getDescription(), quizzAttempts.get(0).getDescription());
        assertEquals(0, quizzAttempts.get(0).getOnQuestionIndex());
        quizzAttempts.get(0).setOnQuestionIndex(2);
        assertEquals(2, quizzAttempts.get(0).getOnQuestionIndex());

        // Checking when finished on time
        List<String> question1Ans = new ArrayList<>();
        List<String> question2Ans = new ArrayList<>();
        List<String> question3Ans = new ArrayList<>();

        question1Ans.add("answer 2");
        question1Ans.add("random answer");

        question2Ans.add("answer 5");
        question2Ans.add("another correct answer 2");
        question2Ans.add("another correct answer 1");

        question3Ans.add("wrong answer 2");
        question3Ans.add("wrong answer 1");

        questions.get(0).setWrittenAnswers(question1Ans);
        questions.get(1).setWrittenAnswers(question2Ans);
        questions.get(2).setWrittenAnswers(question3Ans);

        assertEquals(5 * 1/2 + 10 + 0, quizzAttempts.get(0).evaluateQuiz());

        Thread.sleep(3000);

        // Checking when finished dalayed
        question1Ans.clear();
        question1Ans.add("answer 15");
        question2Ans.clear();
        question2Ans.add("answer 18");
        question3Ans.clear();
        question3Ans.add("answer 21");

        questions.get(3).setWrittenAnswers(question1Ans);
        questions.get(4).setWrittenAnswers(question2Ans);
        questions.get(5).setWrittenAnswers(question3Ans);

        assertEquals(0, quizzAttempts.get(1).evaluateQuiz());
    }
    public void testQuizAttemptController(){
        // Attempting quiz
        int id1 = qac.attemptQuiz(quizzes.get(0), questionLists.get(0));
        int id2 =  qac.attemptQuiz(quizzes.get(1), questionLists.get(1));

        List<Integer> ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id1,id2)), new HashSet<>(ids));

        // Check if correct quiz is saved
        QuizAttempt qa = qac.getQuizAttemptById(id1);
        assertEquals(quizzes.get(0).getId(), qa.getQuizId());

        // Finished quiz
        List<String> question1Ans = new ArrayList<>();
        List<String> question2Ans = new ArrayList<>();
        List<String> question3Ans = new ArrayList<>();

        question1Ans.add("answer 2");
        question1Ans.add("random answer");

        question2Ans.add("answer 5");
        question2Ans.add("another correct answer 2");
        question2Ans.add("another correct answer 1");

        question3Ans.add("wrong answer 2");
        question3Ans.add("wrong answer 1");

        qa.getQuestions().get(0).setWrittenAnswers(question1Ans);
        qa.getQuestions().get(1).setWrittenAnswers(question2Ans);
        qa.getQuestions().get(2).setWrittenAnswers(question3Ans);

        History qh = qac.finishQuiz(id1);

        assertEquals(5 * 1/2 + 10 + 0, qh.getGrade());
        assertEquals(quizzes.get(0).getId(), qh.getQuizId());
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id2)), new HashSet<>(ids));

        // Attempting new quiz
        int id3 = qac.attemptQuiz(quizzes.get(2),questionLists.get(2));
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id3,id2)), new HashSet<>(ids));

        qa = qac.getQuizAttemptById(id3);

        // Finishing new quiz
        question1Ans.clear();
        question1Ans.add("answer 24");
        question2Ans.clear();
        question2Ans.add("answer 27");
        question3Ans.clear();
        question3Ans.add("random answer");

        qa.getQuestions().get(0).setWrittenAnswers(question1Ans);
        qa.getQuestions().get(1).setWrittenAnswers(question2Ans);
        qa.getQuestions().get(2).setWrittenAnswers(question3Ans);

        qh = qac.finishQuiz(id3);

        assertEquals(5 + 10 + 0, qh.getGrade());
        assertEquals(quizzes.get(2).getId(), qh.getQuizId());
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id2)), new HashSet<>(ids));
    }
}
