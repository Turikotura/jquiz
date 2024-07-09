package database;

import models.Answer;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDatabase extends Database<Answer> {
    // Answer Columns
    static final String ID = "id";
    static final String TEXT = "text";
    static final String IS_CORRECT = "is_correct";
    static final String QUESTION_ID = "question_id";
    static final String UNIQ_ID = "uniq_id";

    public AnswerDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new answer to answers table
     * @param answer new Answer object
     * @return id of new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    public int add(Answer answer) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?,  ?  );",
                databaseName, TEXT, IS_CORRECT, QUESTION_ID, UNIQ_ID);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setString(1, answer.getText());
        statement.setBoolean(2, answer.getIsCorrect());
        statement.setInt(3, answer.getQuestionId());
        statement.setInt(4, answer.getUniquenessId());

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
     * Assembles Answer object from ResultSet
     * @param rs ResultSet of answers table rows
     * @return Answer object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Answer getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Answer(
                rs.getInt(ID),
                rs.getString(TEXT),
                rs.getBoolean(IS_CORRECT),
                rs.getInt(QUESTION_ID),
                rs.getInt(UNIQ_ID)
        );
    }

    /**
     * Get answers which belong to question passed
     * @param questionId Id of the question
     * @return List of Answers to the question
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?;",
                databaseName, QUESTION_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,questionId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Deletes answers which belong to quiz passed
     * @param quizId Id of the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void deleteAnswersByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String curStatement = String.format("DELETE a FROM %s a JOIN %s q ON a.%s = q.%s WHERE q.%s = ?;",
                ANSWER_DB, QUESTION_DB, QUESTION_ID, QuestionDatabase.ID, QuestionDatabase.QUIZ_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(curStatement,con);
        ps.setInt(1,quizId);
        ps.execute();
        con.close();
    }
}