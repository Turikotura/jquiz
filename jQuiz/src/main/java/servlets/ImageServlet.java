package servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.Database;
import database.QuestionDatabase;
import database.QuizDatabase;
import database.UserDatabase;
import models.Question;
import models.Quiz;
import models.User;

import static listeners.ContextListener.getDatabase;

public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        byte[] imageBytes = null;
        String imagePath = null;

        try{
            if(type.equals("quiz")){
                QuizDatabase quizDB = getDatabase(Database.QUIZ_DB, request);
                int quizId = Integer.parseInt(request.getParameter("quizId"));
                Quiz quiz = quizDB.getById(quizId);
                if(quiz != null) {
                    imagePath = quiz.getThumbnailUrl();
                    if (imagePath != null) {
                        java.nio.file.Path path = java.nio.file.Paths.get(imagePath);
                        if (java.nio.file.Files.exists(path)) {
                            imageBytes = java.nio.file.Files.readAllBytes(path);
                        }
                    }
                    if (imageBytes == null) {
                        imageBytes = quiz.getThumbnail();
                    }
                }
            }else if(type.equals("user")){
                UserDatabase userDB = getDatabase(Database.USER_DB, request);
                int userId = Integer.parseInt(request.getParameter("userId"));
                User user = userDB.getById(userId);
                if(user != null) {
                    imagePath = user.getImageUrl();
                    if (imagePath != null) {
                        java.nio.file.Path path = java.nio.file.Paths.get(imagePath);
                        if (java.nio.file.Files.exists(path)) {
                            imageBytes = java.nio.file.Files.readAllBytes(path);
                        }
                    }
                    if (imageBytes == null) {
                        imageBytes = user.getImage();
                    }
                    if (imageBytes == null) {
                        java.nio.file.Path path = java.nio.file.Paths.get("./database_structure/example_images/default_user.png");
                        imageBytes = java.nio.file.Files.readAllBytes(path);
                    }
                }
            }else if(type.equals("question")){
                QuestionDatabase questionDB = getDatabase(Database.QUESTION_DB, request);
                int questionId = Integer.parseInt(request.getParameter("questionId"));
                Question question = questionDB.getById(questionId);
                if(question != null) {
                    imagePath = question.getImageUrl();
                    if (imagePath != null) {
                        java.nio.file.Path path = java.nio.file.Paths.get(imagePath);
                        if (java.nio.file.Files.exists(path)) {
                            imageBytes = java.nio.file.Files.readAllBytes(path);
                        }
                    }
                    if (imageBytes == null) {
                        imageBytes = question.getImage();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(imageBytes != null){
            response.setContentType("image/jpeg");
            try(OutputStream os = response.getOutputStream()){
                os.write(imageBytes);
                os.flush();
            }
        }else{
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}