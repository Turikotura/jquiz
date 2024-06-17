package database;

import models.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    static final String TOTAL_PLAY_COUNT = "total_play_count";
    static final String LAST_MONTH_PLAY_COUNT = "last_month_play_count";

    public QuizDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public boolean add(Quiz quiz) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO quizzes (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                        "VALUES ('%s', %d, %s, %d, '%s', %b, %b, %b, %b, '%s');",
                TITLE, AUTHOR_ID, CREATED_AT, TIME, THUMBNAIL, SHOULD_MIX_UP, SHOW_ALL, AUTO_CORRECT, ALLOW_PRACTICE, DESCRIPTION,
                quiz.getTitle(), quiz.getAuthorId(), quiz.getCreatedAt().toString(), quiz.getMaxTime(), quiz.getThumbnail(),
                quiz.getShouldMixUp(), quiz.getShowAll(), quiz.getAutoCorrect(), quiz.getAllowPractice(), quiz.getDescription());
        PreparedStatement statement = this.getStatement(query);
        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
    }

    @Override
    protected Quiz getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        List<Integer> questionIds = new ArrayList<>();
        return new Quiz(
                rs.getInt(ID),
                rs.getString(TITLE),
                rs.getInt(AUTHOR_ID),
                rs.getDate(CREATED_AT),
                rs.getInt(TIME),
                rs.getString(THUMBNAIL),
                rs.getBoolean(SHOULD_MIX_UP),
                rs.getBoolean(SHOW_ALL),
                rs.getBoolean(AUTO_CORRECT),
                rs.getBoolean(ALLOW_PRACTICE),
                rs.getString(DESCRIPTION),
                questionIds,
                rs.getInt(TOTAL_PLAY_COUNT),
                rs.getInt(LAST_MONTH_PLAY_COUNT)
        );
    }
    public List<Quiz> getQuizzesByAuthorId(int authorId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName, AUTHOR_ID, authorId);
        return queryToList(query);
    }
    
    public Quiz getQuizById(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName,ID,quizId);
        return queryToList(query).get(0);
    }

    public List<Quiz> getPopularQuizzes(int k, String totalOrLastMonth) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY " + (totalOrLastMonth == "LAST_MONTH" ? LAST_MONTH_PLAY_COUNT : TOTAL_PLAY_COUNT) + " DESC LIMIT %d;",
                Database.QUIZ_DB,k);
        return queryToList(query);
    }
    public List<Quiz> getRecentlyCreatedQuizzes(int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                databaseName, CREATED_AT, k);
        return queryToList(query);
    }
}
