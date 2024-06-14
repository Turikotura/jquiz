package database;

import models.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase extends Database<Question> {
    static final String ID = "id";
    static final String QUESTION_TYPE_ID = "question_type_id";
    static final String TEXT = "text";
    static final String QUIZ_ID = "quiz_id";
    static final String IMAGE_URL = "image_url";
    static final String SCORE = "score";
    AnswerDatabase answerDB;

    public QuestionDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
        answerDB = new AnswerDatabase(dataSource, Database.ANSWER_DB);
    }

    @Override
    public boolean add(Question question) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO questions (%s, %s, %s, %s, %s, %s) VALUES (%d, %d, '%s', %d, '%s', %d);",
                QUESTION_TYPE_ID, TEXT, QUIZ_ID, IMAGE_URL, SCORE,
                question.getQuestionTypeId(), question.getText(), question.getQuizId(), question.getImageUrl(), question.getScore());
        PreparedStatement statement = this.getStatement(query);
        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
    }

    @Override
    protected Question getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        List<Integer> answerIds = answerDB.getAnswerIdsByQuestionId(rs.getInt(ID));
        return new Question(
                rs.getInt(ID),
                rs.getInt(QUESTION_TYPE_ID),
                rs.getString(TEXT),
                rs.getInt(QUIZ_ID),
                rs.getString(IMAGE_URL),
                rs.getInt(SCORE),
                answerIds
        );
    }

    public List<Integer> getQuestionIdsByQuiz(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT %s FROM questions WHERE %s = %d;", ID, QUIZ_ID, quizId);
        PreparedStatement statement = this.getStatement(query);
        ResultSet rs = statement.executeQuery();

        List<Integer> questionIds = new ArrayList<>();
        while (rs.next()) {
            questionIds.add(rs.getInt(ID));
        }
        return questionIds;
    }
}
