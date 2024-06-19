package servlets;

import database.AnswerDatabase;
import database.Database;
import database.QuestionDatabase;
import database.QuizDatabase;
import models.Answer;
import models.Question;
import models.QuestionTypes;
import models.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;


@MultipartConfig(maxFileSize = 16177215)
public class CreateQuizServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        int time = Integer.parseInt(request.getParameter("time"));
        boolean shouldMixUp = request.getParameter("shouldMixUp") != null;
        boolean showAll = request.getParameter("showAll") != null;
        boolean allowPractice = request.getParameter("allowPractice") != null;
        byte[] thumbnail = null;

        Part filePart = request.getPart("thumbnail");
        if (filePart != null) {
            try (InputStream inputStream = filePart.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                thumbnail = outputStream.toByteArray();
            }
        }

        Quiz quiz = new Quiz(0, title, 1, null, time, thumbnail, shouldMixUp, showAll, false, allowPractice, description, new ArrayList<>(), 0, 0);
        QuizDatabase quizDatabase = (QuizDatabase) getServletContext().getAttribute(Database.QUIZ_DB);
        int quizId = -1;
        try {
            quizId = quizDatabase.add(quiz);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("Message", "Error: " + e.getMessage());
        }

        int questionIndex = 1;
        while (true) {
            String questionType = request.getParameter(questionIndex + "_questionTypeIdentifier");
            if (questionType == null) {
                break; // No more questions
            }
            System.out.println(questionType);

            String questionText = request.getParameter(questionIndex + "_question");
            List<Answer> answers = new ArrayList<>();
            QuestionTypes questionTypeEnum = null;
            Set<String> correctAnswers = new HashSet<>();
            String ansText;
            String[] sameAnswers;
            byte[] picture = null;

            switch (questionType) {
                case "response":
                    questionTypeEnum = QuestionTypes.RESPONSE;
                    ansText = request.getParameter(questionIndex + "_answer");
                    sameAnswers = ansText.split("/");
                    for(String singleAnswer: sameAnswers){
                        Answer answer = new Answer(-1,singleAnswer,true,-1,1);
                        answers.add(answer);
                    }
                    break;
                case "fillBlank":
                    questionTypeEnum = QuestionTypes.FILL_BLANK;
                    ansText = request.getParameter(questionIndex + "_answer");
                    sameAnswers = ansText.split("/");
                    for(String singleAnswer: sameAnswers){
                        Answer answer = new Answer(-1,singleAnswer,true,-1,1);
                        answers.add(answer);
                    }
                    break;
                case "pictureResponse":
                    questionTypeEnum = QuestionTypes.PIC_RESPONSE;
                    ansText = request.getParameter(questionIndex + "_answer");
                    sameAnswers = ansText.split("/");
                    for(String singleAnswer: sameAnswers){
                        Answer answer = new Answer(-1,singleAnswer,true,-1,1);
                        answers.add(answer);
                    }
                    Part picturePart = request.getPart(questionIndex + "_picture");
                    if (picturePart != null) {
                        System.out.println("WTFFFFFF");
                        try (InputStream inputStream = picturePart.getInputStream();
                             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            picture = outputStream.toByteArray();
                        }
                    }
                    break;
                case "multipleChoice":
                    questionTypeEnum = QuestionTypes.MULTIPLE_CHOICE;
                    System.out.println(request.getParameter(questionIndex + "_correct"));
                    int correctChoice = Integer.parseInt(request.getParameter(questionIndex + "_correct"));
                    int answerIndex = 1;
                    while(true){
                        ansText = request.getParameter(questionIndex + "_answer_" + answerIndex);

                        if(ansText != null){
                            sameAnswers = ansText.split("/");
                            boolean isCorrect = answerIndex == correctChoice;
                            for(String singleAnswer: sameAnswers){
                                Answer answer = new Answer(-1,singleAnswer,isCorrect,-1,answerIndex);
                                answers.add(answer);
                            }
                            answerIndex++;
                        }else{
                            break;
                        }
                    }
                    break;
                case "multiChoiceMultiAnswer":
                    questionTypeEnum = QuestionTypes.MULTI_ANS_MULTI_CHOICE;
                    int multiAnswerIndex = 1;
                    while(true){
                        ansText = request.getParameter(questionIndex + "_answer_" + multiAnswerIndex);
                        if(ansText != null){
                            sameAnswers = ansText.split("/");
                            boolean isCorrect = request.getParameter(questionIndex + "_correct_" + multiAnswerIndex)!=null;
                            for(String singleAnswer: sameAnswers){
                                Answer answer = new Answer(-1,singleAnswer,isCorrect,-1,multiAnswerIndex);
                                answers.add(answer);
                            }
                            multiAnswerIndex++;
                        }else{
                            break;
                        }
                    }
                default:
                    break;
            }
            Question newQuestion = new Question(1, questionTypeEnum, questionText, quizId, picture, 1, new ArrayList<>());
            QuestionDatabase questionDB = (QuestionDatabase) getServletContext().getAttribute(Database.QUESTION_DB);
            int questionId = -1;
            try {
                questionId = questionDB.add(newQuestion);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            AnswerDatabase answerDB = (AnswerDatabase) getServletContext().getAttribute(Database.ANSWER_DB);
            for(Answer answer: answers){
                answer.setQuestionId(questionId);
                try {
                    answerDB.add(answer);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            questionIndex++;
        }

        response.sendRedirect("/");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported");
    }
}
