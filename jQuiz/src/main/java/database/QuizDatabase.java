package database;

import models.Question;
import models.Quiz;
import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuizDatabase extends Database<Quiz>{

    static final String ID = "id";
    static final String TITLE = "title";
    static final String AUTHOR_ID = "author_id";
    static final String CREATED_AT = "created_at";
    static final String TIME = "time";
    static final String THUMBNAIL = "thumbnail";
    static final String SHOULD_MIX_UP = "should_mix_up";
    static final String SHOW_ALL = "show_all";
    static final String AUTO_CORRECT = "auto_correct";
    static final String ALLOW_PRACTICE = "allow_practice";
    static final String DESCRIPTION = "description";

    public QuizDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public boolean add(Quiz quiz) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO quizzes (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                        "VALUES ('%s', %d, '%s', %d, '%s', %d, %d, %d, %d, '%s');",
                TITLE, AUTHOR_ID, CREATED_AT, TIME, THUMBNAIL, SHOULD_MIX_UP, SHOW_ALL, AUTO_CORRECT, ALLOW_PRACTICE, DESCRIPTION,
                quiz.getTitle(), quiz.getAuthor().getId(), quiz.getCreatedAt().toString(), quiz.getMaxTime(), quiz.getThumbnail(),
                quiz.getShouldMixUp(), quiz.getShowAll(), quiz.getAutoCorrect(), quiz.getAllowPractice(), quiz.getDescription());
        PreparedStatement statement = this.getStatement(query);
        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
    }

    @Override
    protected Quiz getItemFromResultSet(ResultSet rs) throws SQLException {
        User author = UserDatabase.getById(rs.getString(AUTHOR_ID));
        List<Integer> questionIds = QuestionDatabase.getQuestionIdsByQuiz(rs.getInt(ID));
        return new Quiz(
                rs.getInt(ID),
                rs.getString(TITLE),
                author,
                rs.getDate(CREATED_AT),
                rs.getInt(TIME),
                rs.getString(THUMBNAIL),
                rs.getBoolean(SHOULD_MIX_UP),
                rs.getBoolean(SHOW_ALL),
                rs.getBoolean(AUTO_CORRECT),
                rs.getBoolean(ALLOW_PRACTICE),
                rs.getString(DESCRIPTION),
                questionIds
        );
    }
}
