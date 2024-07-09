package attempts;

import junit.framework.TestCase;
import models.*;

import java.util.*;

import static org.junit.Assert.assertNotEquals;

public class AttemptTests extends TestCase {
    private List<Answer> getDefaultAnswers(int qId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,0));
        return answers;
    }
    private List<Answer> getMixedAnswers(int qId){
        List<Answer> answers = getDefaultAnswers(qId);
        answers.add(new Answer(answers.size(),"another correct answer",true,qId,1));
        answers.add(new Answer(answers.size(),"wrong answer",false,qId,0));
        return answers;
    }
    private List<Answer> getRightAnswers(int qId){
        List<Answer> answers = getDefaultAnswers(qId);
        answers.add(new Answer(answers.size(),"another correct answer 1",true,qId,1));
        answers.add(new Answer(answers.size(),"another correct answer 2",true,qId,2));
        return answers;
    }
    private List<Answer> getWrongAnswers(int qId){
        List<Answer> answers = getDefaultAnswers(qId);
        answers.add(new Answer(answers.size(),"wrong answer 1",false,qId,0));
        answers.add(new Answer(answers.size(),"wrong answer 2",false,qId,0));
        return answers;
    }
    private Question getResponseQuestion(int qId, int quizId, int points){
        return new Question(qId, QuestionTypes.RESPONSE, String.format("question %d",qId),quizId,null, null,points);
    }
    private QuestionAttempt getMAMCQuestionAttempt(int qId, int quizId){
        List<Answer> answers = getDefaultAnswers(qId);
        answers.add(new Answer(answers.size(),"answer 1",true,qId,0));
        answers.add(new Answer(answers.size(),"wrong answer 1",false,qId,1));
        answers.add(new Answer(answers.size(),"answer 2",true,qId,2));
        answers.add(new Answer(answers.size(),"wrong answer 2",false,qId,3));
        Question q = new Question(qId, QuestionTypes.MULTI_ANS_MULTI_CHOICE, String.format("question %d",qId),quizId,null,null,10);
        return new QuestionAttempt(q, answers,false);
    }
    private QuestionAttempt getFBQuestionAttempt(int qId, int quizId){
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(answers.size(),"answer 0",true,qId,1));
        answers.add(new Answer(answers.size(),"answer 1",true,qId,2));
        Question q = new Question(qId, QuestionTypes.FILL_BLANK, String.format("question %d",qId),quizId,null,null,10);
        return new QuestionAttempt(q, answers, false);
    }
    public Quiz getQuiz(int quizId, int time, boolean shouldMixUp){
        return new Quiz(quizId,String.format("quiz %d",quizId),-1,new Date(),time,null,null,shouldMixUp,true,true,true,"desc","category",null,0,0);
    }



    /**
     * Test QuestionAttempt class
     */
    public void testQuestionAttempt(){
        QuestionAttempt qa = null;
        List<String> writtenAnswers = new ArrayList<String>();

        // Check if correct answer amount is right
        qa = new QuestionAttempt(getResponseQuestion(0,0,5),getDefaultAnswers(0),false);
        assertEquals(1,qa.getCorrectAnswersAmount()); // Default
        qa = new QuestionAttempt(getResponseQuestion(0,0,5),getMixedAnswers(0),false);
        assertEquals(2,qa.getCorrectAnswersAmount()); // Mix
        qa = new QuestionAttempt(getResponseQuestion(0,0,5),getRightAnswers(0),false);
        assertEquals(3,qa.getCorrectAnswersAmount()); // Right
        qa = new QuestionAttempt(getResponseQuestion(0,0,5),getWrongAnswers(0),false);
        assertEquals(1,qa.getCorrectAnswersAmount()); // Wrong

        // Check if question evaluation yield correct grade

        // Correct answer
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false);
        writtenAnswers.add("answer 0");
        qa.setWrittenAnswers(writtenAnswers);
        assertFalse(qa.getWasGraded());
        assertEquals(10*1/2,qa.evaluateAnswers());
        assertTrue(qa.getWasGraded());

        // After evaluation, the answer shouldn't change
        writtenAnswers.add("shouldn't change");
        qa.setWrittenAnswers(writtenAnswers);
        for(String answer : qa.getWrittenAnswers()){
            assertNotEquals("shouldn't change", answer);
        }

        // After evaluation, the grade shouldn't change either
        assertEquals(10*1/2,qa.evaluateAnswers());

        // Correct answer with same meaning
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("answer 2");
        writtenAnswers.add("answer 0");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2,qa.evaluateAnswers());

        // Wrong answer
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(0, qa.evaluateAnswers());

        // Random answer
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("random answer");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(0, qa.evaluateAnswers());

        // Almost all correct
        qa = new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(15 * 2 / 3, qa.evaluateAnswers());

        // Almost correct duplicate
        qa = new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(15 * 2 / 3, qa.evaluateAnswers());

        // All correct
        qa = new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("another correct answer 1");
        writtenAnswers.add("another correct answer 2");
        writtenAnswers.add("answer 2");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(15, qa.evaluateAnswers());

        // All wrong
        qa = new QuestionAttempt(getResponseQuestion(0,0,5),getWrongAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer 2");
        writtenAnswers.add("wrong answer 1");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(0, qa.evaluateAnswers());

        // Combination
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false);
        writtenAnswers.clear();
        writtenAnswers.add("wrong answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("another correct answer");
        writtenAnswers.add("random answer");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2, qa.evaluateAnswers());

        // MAMC All right
        qa = getMAMCQuestionAttempt(0,0);
        writtenAnswers.clear();
        writtenAnswers.add("answer 1");
        writtenAnswers.add("answer 2");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10, qa.evaluateAnswers());

        // MAMC 2 right / 1 wrong
        qa = getMAMCQuestionAttempt(0,0);
        writtenAnswers.clear();
        writtenAnswers.add("answer 1");
        writtenAnswers.add("answer 2");
        writtenAnswers.add("wrong answer 1");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10 * 1/2, qa.evaluateAnswers());

        // MAMC All answers
        qa = getMAMCQuestionAttempt(0,0);
        writtenAnswers.clear();
        writtenAnswers.add("answer 1");
        writtenAnswers.add("answer 2");
        writtenAnswers.add("wrong answer 1");
        writtenAnswers.add("wrong answer 2");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(0, qa.evaluateAnswers());

        // FB correct order
        qa = getFBQuestionAttempt(0,0);
        writtenAnswers.clear();
        writtenAnswers.add("answer 0");
        writtenAnswers.add("answer 1");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10, qa.evaluateAnswers());

        // FB wrong order
        qa = getFBQuestionAttempt(0,0);
        writtenAnswers.clear();
        writtenAnswers.add("answer 1");
        writtenAnswers.add("answer 0");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(0, qa.evaluateAnswers());

        // Practice question change after evaluation
        qa = new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),true);
        writtenAnswers.clear();
        writtenAnswers.add("answer 2");
        writtenAnswers.add("wrong answer");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(10*1/2, qa.evaluateAnswers());

        writtenAnswers.clear();
        writtenAnswers.add("answer 1");
        writtenAnswers.add("another correct answer");
        qa.setWrittenAnswers(writtenAnswers);
        assertEquals(new HashSet<>(writtenAnswers),new HashSet<>(qa.getWrittenAnswers()));
        assertEquals(10, qa.evaluateAnswers());
    }

    /**
     * Test QuizAttempt class
     * @throws InterruptedException
     */
    public void testQuizAttempt() throws InterruptedException {
        Quiz onTimeQuiz = getQuiz(0,10,false);
        List<QuestionAttempt> qas = new ArrayList<>();
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),false));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,5),getWrongAnswers(0),false));
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
        assertFalse(onTimeQuizAttempt.evaluateQuestionPractice(0));

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
        assertEquals(10 * 1/2 + 15 + 0, onTimeQuizAttempt.evaluateQuiz());

        // Checking when finished dalayed
        Quiz delayQuiz = getQuiz(0,2,false);
        qas.clear();
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),false));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),false));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,5),getWrongAnswers(0),false));
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

        Thread.sleep(4000);

        assertEquals(0, delayQuizAttempt.evaluateQuiz());

        // Checking practice quiz
        Quiz practiceQuiz = getQuiz(0,2,false);
        qas.clear();
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,10),getMixedAnswers(0),true));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,15),getRightAnswers(0),true));
        qas.add(new QuestionAttempt(getResponseQuestion(0,0,5),getWrongAnswers(0),true));
        QuizAttempt practiceQuizAttempt = new QuizAttempt(0, practiceQuiz, true, qas);

        question1Ans.clear();
        question1Ans.add("answer 1");
        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);

        int maxIterations = 100;
        int i = 0;
        while(i < maxIterations && practiceQuizAttempt.getOnQuestionIndex() == 0){
            assertFalse(practiceQuizAttempt.evaluateQuestionPractice(0));
            i++;
        }
        // Check if on question index moves after question answer
        assertTrue(practiceQuizAttempt.getOnQuestionIndex() != 0);

        question1Ans.clear();
        question1Ans.add("answer 1");
        question2Ans.clear();
        question2Ans.add("answer 0");
        question3Ans.clear();
        question3Ans.add("answer 2");

        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        practiceQuizAttempt.getQuestions().get(1).setWrittenAnswers(question2Ans);
        practiceQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);

        // First evaluation
        assertEquals(10 * 1/2 + 15 * 1/3 + 5 * 1/1, practiceQuizAttempt.evaluateQuiz());

        question1Ans.clear();
        question1Ans.add("answer 1");
        question1Ans.add("another correct answer");
        question2Ans.clear();
        question2Ans.add("answer 0");
        question2Ans.add("another correct answer 1");
        question2Ans.add("another correct answer 2");
        question3Ans.clear();
        question3Ans.add("wrong answer");

        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        practiceQuizAttempt.getQuestions().get(1).setWrittenAnswers(question2Ans);
        practiceQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);

        assertTrue(practiceQuizAttempt.evaluateQuestionPractice(0));
        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        assertTrue(practiceQuizAttempt.evaluateQuestionPractice(0));
        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        assertTrue(practiceQuizAttempt.evaluateQuestionPractice(0));
        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);

        i = 0;
        while (i < maxIterations && practiceQuizAttempt.getOnQuestionIndex() != 0){
            assertFalse(practiceQuizAttempt.evaluateQuestionPractice(2));
            practiceQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);
            i++;
        }
        // Check that question 0 is not visited after 3 conseq right answers
        assertEquals(i, maxIterations);

        practiceQuizAttempt.getQuestions().get(0).setWrittenAnswers(question1Ans);
        practiceQuizAttempt.getQuestions().get(1).setWrittenAnswers(question2Ans);
        practiceQuizAttempt.getQuestions().get(2).setWrittenAnswers(question3Ans);

        // Evaluate again
        assertEquals(10 * 2/2 + 15 * 3/3 + 5 * 0/1, practiceQuizAttempt.evaluateQuiz());
    }

    /**
     * Test QuizAttemptController class
     */
    public void testQuizAttemptController(){
        QuizAttemptsController qac = new QuizAttemptsController(0);

        Quiz firstQuiz = getQuiz(0,10,false);
        Map<Question,List<Answer>> firstQas = new HashMap<>();
        firstQas.put(getResponseQuestion(0,0,10),getMixedAnswers(0));
        firstQas.put(getResponseQuestion(1,0,15),getRightAnswers(1));
        firstQas.put(getResponseQuestion(2,0,5),getWrongAnswers(2));

        Quiz secondQuiz = getQuiz(1,10,true);
        Map<Question,List<Answer>> secondQas = new HashMap<>();
        secondQas.put(getResponseQuestion(3,1,10),getDefaultAnswers(3));
        secondQas.put(getResponseQuestion(4,1,15),getDefaultAnswers(4));
        secondQas.put(getResponseQuestion(5,1,5),getDefaultAnswers(5));

        Quiz thirdQuiz = getQuiz(2,10,false);
        Map<Question,List<Answer>> thirdQas = new HashMap<>();
        thirdQas.put(getResponseQuestion(6,2,10),getDefaultAnswers(6));
        thirdQas.put(getResponseQuestion(7,2,15),getDefaultAnswers(7));
        thirdQas.put(getResponseQuestion(8,2,5),getDefaultAnswers(8));

        Quiz fourthQuiz = getQuiz(2,10,false);
        Map<Question,List<Answer>> fourthQas = new HashMap<>();
        fourthQas.put(getResponseQuestion(9,3,10),getDefaultAnswers(9));
        fourthQas.put(getResponseQuestion(10,3,15),getDefaultAnswers(10));
        fourthQas.put(getResponseQuestion(11,3,5),getDefaultAnswers(11));

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

        // Practice quiz
        int id4 = qac.attemptQuiz(fourthQuiz,true, fourthQas);
        assertEquals(null,qac.finishQuiz(id4));
    }
}