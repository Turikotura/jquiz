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

    public List<Integer> getAnswerIdsByQuestionId(int questionId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT %s FROM %s WHERE %s = ?;",
                ID, databaseName, QUESTION_ID);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setInt(1,questionId);
        ResultSet rs = statement.executeQuery();

        List<Integer> answerIds = new ArrayList<>();
        while (rs.next()) {
            answerIds.add(rs.getInt(ID));
        }
        con.close();
        return answerIds;
    }
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
}