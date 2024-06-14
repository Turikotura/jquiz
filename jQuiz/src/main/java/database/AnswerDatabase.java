package database;

import models.Answer;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDatabase extends Database<Answer> {
    static final String ID = "answer_id";
    static final String TEXT = "answer_text";
    static final String IS_CORRECT = "is_correct";
    static final String QUESTION_ID = "question_id";
    static final String UNIQ_ID = "uniq_id";

    public AnswerDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public boolean add(Answer answer) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO answers (%s, %s, %s, %s, %s) VALUES (%d, '%s', %b, %d, %d);",
                ID, TEXT, IS_CORRECT, QUESTION_ID, UNIQ_ID,
                answer.getId(), answer.getText(), answer.getIsCorrect(), answer.getQuestionId(), answer.getUniquenessId());
        PreparedStatement statement = this.getStatement(query);
        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
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
        String query = String.format("SELECT %s FROM %s WHERE %s = %d;", ID, this.databaseName, QUESTION_ID, questionId);
        PreparedStatement statement = this.getStatement(query);
        ResultSet rs = statement.executeQuery();

        List<Integer> answerIds = new ArrayList<>();
        while (rs.next()) {
            answerIds.add(rs.getInt(ID));
        }
        return answerIds;
    }
}
