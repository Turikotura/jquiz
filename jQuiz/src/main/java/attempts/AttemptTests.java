package attempts;

import junit.framework.TestCase;
import models.*;

import java.util.*;

import static org.junit.Assert.assertNotEquals;

public class AttemptTests extends TestCase {
    private QuestionAttempt getNormalQuestionAttempt(int qId, int quizId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,0));
        Question q = new Question(qId, QuestionTypes.RESPONSE, String.format("question %d",qId),quizId,null,5,null);
        QuestionAttempt qa = new QuestionAttempt(q, answers);
        return qa;
    }
    private QuestionAttempt getMixQuestionAttempt(int qId, int quizId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,0));
        answers.add(new Answer(answers.size(),"another correct answer",true,qId,1));
        answers.add(new Answer(answers.size(),"wrong answer",false,qId,0));
        Question q = new Question(qId, QuestionTypes.RESPONSE, String.format("question %d",qId),quizId,null,10,null);
        QuestionAttempt qa = new QuestionAttempt(q, answers);
        return qa;
    }
    private QuestionAttempt getRightQuestionAttempt(int qId, int quizId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,0));
        answers.add(new Answer(answers.size(),"another correct answer 1",true,qId,1));
        answers.add(new Answer(answers.size(),"another correct answer 2",true,qId,2));
        Question q = new Question(qId, QuestionTypes.RESPONSE, String.format("question %d",qId),quizId,null,15,null);
        QuestionAttempt qa = new QuestionAttempt(q, answers);
        return qa;
    }
    private QuestionAttempt getWrongQuestionAttempt(int qId, int quizId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,0));
        answers.add(new Answer(answers.size(),"wrong answer 1",false,qId,0));
        answers.add(new Answer(answers.size(),"wrong answer 2",false,qId,0));
        Question q = new Question(qId, QuestionTypes.RESPONSE, String.format("question %d",qId),quizId,null,5,null);
        QuestionAttempt qa = new QuestionAttempt(q, answers);
        return qa;
    }
    public Quiz getQuiz(int quizId, int time, boolean shouldMixUp){
        return new Quiz(quizId,String.format("quiz %d",quizId),-1,new Date(),time,null,shouldMixUp,true,true,true,"desc",null,0,0);
    }



    /**
     * Test QuestionAttempt class
     */
    public void testQuestionAttempt(){
        // 9 questions
        List<QuestionAttempt> questionAttemptList = new ArrayList<>();
        questionAttemptList.add(getMixQuestionAttempt(questionAttemptList.size(),0)); // 0
        questionAttemptList.add(getMixQuestionAttempt(questionAttemptList.size(),0)); // 1
        questionAttemptList.add(getMixQuestionAttempt(questionAttemptList.size(),0)); // 2
        questionAttemptList.add(getMixQuestionAttempt(questionAttemptList.size(),0)); // 3
        questionAttemptList.add(getRightQuestionAttempt(questionAttemptList.size(),0)); // 4
        questionAttemptList.add(getRightQuestionAttempt(questionAttemptList.size(),0)); // 5
        questionAttemptList.add(getRightQuestionAttempt(questionAttemptList.size(),0)); // 6
        questionAttemptList.add(getWrongQuestionAttempt(questionAttemptList.size(),0)); // 7
        questionAttemptList.add(getMixQuestionAttempt(questionAttemptList.size(),0)); // 8

        // Check if correct answer amount is right
        assertEquals(2,questionAttemptList.get(0).getCorrectAnswersAmount()); // Mix
        assertEquals(3,questionAttemptList.get(4).getCorrectAnswersAmount()); // Right
        assertEquals(1,questionAttemptList.get(7).getCorrectAnswersAmount()); // Wrong

        // Check if question evaluation yield correct grade

        // Correct answer
        List<String> writtenAnswers = new ArrayList<String>();
        writtenAnswers.add("answer 0");
        questionAttemptList.get(0).setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2,questionAttemptList.get(0).evaluateAnswers());

        // After evaluation, the answer shouldn't change
        writtenAnswers.add("shouldn't change");
        questionAttemptList.get(0).setWrittenAnswers(writtenAnswers);
        for(String answer : questionAttemptList.get(0).getWrittenAnswers()){
            assertNotEquals("shouldn't change", answer);
        }

        // After evaluation, the grade shouldn't change either
        assertEquals(10*1/2,questionAttemptList.get(0).evaluateAnswers());

        // Correct answer with same meaning
        writtenAnswers.clear();
        writtenAnswers.add("answer 2");
        writtenAnswers.add("answer 0");
        questionAttemptList.get(1).setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2,questionAttemptList.get(1).evaluateAnswers());

        // Wrong answer
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        questionAttemptList.get(2).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questionAttemptList.get(2).evaluateAnswers());

        // Random answer
        writtenAnswers.clear();
        writtenAnswers.add("random answer");
        questionAttemptList.get(3).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questionAttemptList.get(3).evaluateAnswers());

        // Almost all correct
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        questionAttemptList.get(4).setWrittenAnswers(writtenAnswers);
        assertEquals(15 * 2 / 3, questionAttemptList.get(4).evaluateAnswers());

        // Almost correct duplicate
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        questionAttemptList.get(5).setWrittenAnswers(writtenAnswers);
        assertEquals(15 * 2 / 3, questionAttemptList.get(5).evaluateAnswers());

        // All correct
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        writtenAnswers.add("answer 2");
        questionAttemptList.get(6).setWrittenAnswers(writtenAnswers);
        assertEquals(15, questionAttemptList.get(6).evaluateAnswers());

        // All wrong
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer 2");
        writtenAnswers.add("wrong answer 1");
        questionAttemptList.get(7).setWrittenAnswers(writtenAnswers);
        assertEquals(0, questionAttemptList.get(7).evaluateAnswers());

        // Combination
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("random answer");
        questionAttemptList.get(8).setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2, questionAttemptList.get(8).evaluateAnswers());
    }

    /**
     * Test QuizAttempt class
     * @throws InterruptedException
     */
    public void testQuizAttempt() throws InterruptedException {
        Quiz onTimeQuiz = getQuiz(0,10,false);
        List<QuestionAttempt> qas = new ArrayList<>();
        qas.add(getMixQuestionAttempt(0,0));
        qas.add(getRightQuestionAttempt(1,0));
        qas.add(getWrongQuestionAttempt(2,0));
        QuizAttempt onTimeQuizAttempt = new QuizAttempt(0, onTimeQuiz, false, qas);

        // Basic tests
        assertEquals(30, onTimeQuizAttempt.getMaxScore());
        assertEquals(onTimeQuiz.getTitle(), onTimeQuizAttempt.getTitle());
        assertEquals(onTimeQuiz.getAuthorId(), onTimeQuizAttempt.getAuthorId());
        assertEquals(onTimeQuiz.getMaxTime(), onTimeQuizAttempt.getTime());
        assertEquals(onTimeQuiz.getThumbnail(), onTimeQuizAttempt.getThumbnail());
        assertEquals(onTimeQuiz.getShowAll(), onTimeQuizAttempt.getShowAll());
        assertEquals(onTimeQuiz.getAutoCorrect(), onTimeQuizAttempt.getAutoCorrect());
        assertFalse(onTimeQuizAttempt.getIsPractice());
        assertEquals(onTimeQuiz.getDescription(), onTimeQuizAttempt.getDescription());
        assertEquals(0, onTimeQuizAttempt.getOnQuestionIndex());
        onTimeQuizAttempt.setOnQuestionIndex(2);
        assertEquals(2, onTimeQuizAttempt.getOnQuestionIndex());

        // Checking when finished on time
        List<String> question1Ans = new ArrayList<>();
        List<String> question2Ans = new ArrayList<>();
        List<String> question3Ans = new ArrayList<>();

        question1Ans.add("answer 2");
        question1Ans.add("random answer");

        question2Ans.add("answer 0");
        question2Ans.add("another correct answer 2");
        question2Ans.add("another correct answer 1");

        question3Ans.add("wrong answer 2");
        question3Ans.add("wrong answer 1");

        onTimeQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        onTimeQuizAttempt.getQuestions().get(1).setWrittenAnswers(question2Ans);
        onTimeQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);

        assertEquals(10 * 1/2 + 15 + 0, onTimeQuizAttempt.evaluateQuiz());

        // Checking when finished dalayed
        Quiz delayQuiz = getQuiz(0,2,false);
        qas.clear();
        qas.add(getMixQuestionAttempt(0,0));
        qas.add(getRightQuestionAttempt(1,0));
        qas.add(getWrongQuestionAttempt(2,0));
        QuizAttempt delayQuizAttempt = new QuizAttempt(0, delayQuiz, false, qas);

        question1Ans.clear();
        question1Ans.add("answer 1");
        question2Ans.clear();
        question2Ans.add("answer 0");
        question3Ans.clear();
        question3Ans.add("answer 2");

        delayQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        delayQuizAttempt.getQuestions().get(1).setWrittenAnswers(question2Ans);
        delayQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);

        Thread.sleep(3000);

        assertEquals(0, delayQuizAttempt.evaluateQuiz());
    }

    /**
     * Test QuizAttemptController class
     */
    public void testQuizAttemptController(){
        QuizAttemptsController qac = new QuizAttemptsController(0);

        Quiz firstQuiz = getQuiz(0,10,false);
        List<QuestionAttempt> firstQas = new ArrayList<>();
        firstQas.add(getMixQuestionAttempt(0,0));
        firstQas.add(getRightQuestionAttempt(1,0));
        firstQas.add(getWrongQuestionAttempt(2,0));

        Quiz secondQuiz = getQuiz(1,10,true);
        List<QuestionAttempt> secondQas = new ArrayList<>();
        secondQas.add(getNormalQuestionAttempt(3,1));
        secondQas.add(getNormalQuestionAttempt(4,1));
        secondQas.add(getNormalQuestionAttempt(5,1));

        Quiz thirdQuiz = getQuiz(2,10,false);
        List<QuestionAttempt> thirdQas = new ArrayList<>();
        thirdQas.add(getNormalQuestionAttempt(6,2));
        thirdQas.add(getNormalQuestionAttempt(7,2));
        thirdQas.add(getNormalQuestionAttempt(8,2));

        // Basic tests
        assertEquals(0, qac.getUserId());

        // Attempting quiz
        int id1 = qac.attemptQuiz(firstQuiz, false, firstQas);
        int id2 = qac.attemptQuiz(secondQuiz, false, secondQas);

        List<Integer> ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id1,id2)), new HashSet<>(ids));

        // Check if correct quiz is saved
        QuizAttempt qa = qac.getQuizAttemptById(id1);
        assertEquals(firstQuiz.getId(), qa.getQuizId());

        // Finished quiz
        List<String> question1Ans = new ArrayList<>();
        List<String> question2Ans = new ArrayList<>();
        List<String> question3Ans = new ArrayList<>();

        question1Ans.add("answer 2");
        question1Ans.add("random answer");

        question2Ans.add("answer 0");
        question2Ans.add("another correct answer 2");
        question2Ans.add("another correct answer 1");

        question3Ans.add("wrong answer 2");
        question3Ans.add("wrong answer 1");

        qa.getQuestions().get(0).setWrittenAnswers(question1Ans);
        qa.getQuestions().get(1).setWrittenAnswers(question2Ans);
        qa.getQuestions().get(2).setWrittenAnswers(question3Ans);

        History qh = qac.finishQuiz(id1);

        assertEquals(10 * 1/2 + 15 + 0, qh.getGrade());
        assertEquals(firstQuiz.getId(), qh.getQuizId());
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id2)), new HashSet<>(ids));

        // Attempting new quiz
        int id3 = qac.attemptQuiz(thirdQuiz,false,thirdQas);
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id3,id2)), new HashSet<>(ids));

        qa = qac.getQuizAttemptById(id3);

        // Finishing new quiz
        qh = qac.finishQuiz(id3);

        assertEquals(thirdQuiz.getId(), qh.getQuizId());
        ids = qac.getAttemptIds();
        assertEquals(new HashSet<>(Arrays.asList(id2)), new HashSet<>(ids));

        // Check shuffled
        int i = 0;
        qa = qac.getQuizAttemptById(id2);
        while (i < 100 && qa.getQuestions().get(0).getQuestion().getText().equals("question 3")){
            qac.finishQuiz(id2);
            id2 = qac.attemptQuiz(secondQuiz,false,secondQas);
            qa = qac.getQuizAttemptById(id2);
            i++;
        }
        assertFalse(qa.getQuestions().get(0).getQuestion().getText().equals("question 3"));
    }
}