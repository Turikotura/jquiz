package database;

import models.Question;
import models.QuestionTypes;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDatabase extends Database<Question> {
    // Question Columns
    static final String ID = "id";
    static final String QUESTION_TYPE = "question_type";
    static final String TEXT = "text";
    static final String QUIZ_ID = "quiz_id";
    static final String IMAGE = "image";
    static final String IMAGE_URL = "image_url";
    static final String SCORE = "score";
    AnswerDatabase answerDB;

    public QuestionDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
        answerDB = new AnswerDatabase(dataSource, Database.ANSWER_DB);
    }

    @Override
    public int add(Question question) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO quizzes (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                QUESTION_TYPE, TEXT, QUIZ_ID, IMAGE, IMAGE_URL, SCORE);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setInt(1, question.getQuestionType().ordinal());
        statement.setString(2, question.getText());
        statement.setInt(3, question.getQuizId());
        statement.setBytes(4, question.getImage());
        statement.setString(5, question.getImageUrl());
        statement.setInt(6, question.getScore());
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Creating row failed");
        }
        try(ResultSet keys = statement.getGeneratedKeys()){
            if(keys.next()){
                int res = keys.getInt(1);
                con.close();
                return res;
            }else{
                throw new SQLException("Creating row failed");
            }
        }
    }

    @Override
    protected Question getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        List<Integer> answerIds = answerDB.getAnswerIdsByQuestionId(rs.getInt(ID));
        return new Question(
                rs.getInt(ID),
                QuestionTypes.values()[rs.getInt(QUESTION_TYPE)],
                rs.getString(TEXT),
                rs.getInt(QUIZ_ID),
                rs.getBytes(IMAGE),
                rs.getString(IMAGE_URL),
                rs.getInt(SCORE),
                answerIds
        );
    }

    public List<Integer> getQuestionIdsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT %s FROM %s WHERE %s = %d;", ID,
                this.databaseName, QUIZ_ID, quizId);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        ResultSet rs = statement.executeQuery();

        List<Integer> questionIds = new ArrayList<>();
        while (rs.next()) {
            questionIds.add(rs.getInt(ID));
        }
        con.close();
        return questionIds;
    }
    public List<Question> getQuestionsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName, QUIZ_ID, quizId);
        return queryToList(query);
    }
}
