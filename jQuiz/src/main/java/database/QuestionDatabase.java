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

    public QuestionDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new entry to questions table
     * @param question Question Object describing new row
     * @return Id of the new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    public int add(Question question) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s ) " +
                        "VALUES (?,  ?,  ?,  ?,  ?,  ?  );",
        databaseName, QUESTION_TYPE, TEXT, QUIZ_ID, IMAGE, IMAGE_URL, SCORE);
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

    /**
     * Assembles Question object from ResultSet
     * @param rs ResultSet of questions table rows
     * @return Question object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Question getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Question(
                rs.getInt(ID),
                QuestionTypes.values()[rs.getInt(QUESTION_TYPE)],
                rs.getString(TEXT),
                rs.getInt(QUIZ_ID),
                rs.getBytes(IMAGE),
                rs.getString(IMAGE_URL),
                rs.getInt(SCORE)
        );
    }

    /**
     * Get all the questions belonging to the quiz specified
     * @param quizId Id of the quiz
     * @return List of Questions belonging to the quiz specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Question> getQuestionsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, QUIZ_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Delete all the questions belonging to the quiz specified
     * @param quizId Id of the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void deleteQuestionsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String curStatement = String.format("DELETE FROM %s WHERE %s = ?;",
                QUESTION_DB,QUIZ_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(curStatement,con);
        ps.setInt(1,quizId);
        ps.execute();
        con.close();
    }
}