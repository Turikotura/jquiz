package database;

import models.Tag;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDatabase extends Database<Tag> {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TAG_ID = "tag_id";

    public TagDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public int add(Tag tag) throws SQLException, ClassNotFoundException {
        Tag existingTag = getTagByName(tag.getName());
        if (existingTag != null) {
            return existingTag.getId();
        }

        String query = String.format(
                "INSERT INTO %s ( %s ) VALUES ( ? );",
                databaseName, NAME
        );
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query, con);
        statement.setString(1, tag.getName());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating row failed");
        }
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                int res = keys.getInt(1);
                con.close();
                return res;
            } else {
                throw new SQLException("Creating row failed");
            }
        }
    }

    @Override
    protected Tag getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Tag(
                rs.getInt(ID),
                rs.getString(NAME)
        );
    }

    public Tag getTagByName(String name) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, NAME);
        return queryToElement(query, (ps) -> {
            try {
                ps.setString(1, name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Tag> getAllTags() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s",
                databaseName);
        return queryToList(query, (ps) -> {
            return ps;
        });
    }

    public void associateTagWithQuiz(int quizId, int tagId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s ) VALUES (?, ?);",
                Database.TAG_TO_QUIZ_DB, QUIZ_ID, TAG_ID);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query, con);
        statement.setInt(1, quizId);
        statement.setInt(2, tagId);

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating row failed");
        }
        con.close();
    }

    public List<Tag> getTagsForQuiz(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT t.* FROM %s t JOIN %s tq ON t.id = tq.%s WHERE tq.%s = ?;",
                Database.TAG_DB, Database.TAG_TO_QUIZ_DB, TAG_ID, QUIZ_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1, quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Tag> searchTags(String searchString) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s LIKE ?;",
                Database.TAG_DB, NAME);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, "%" + searchString + "%");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public void clearTagsConnectionByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TAG_TO_QUIZ_DB, QUIZ_ID
        );
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setInt(1,quizId);
        ps.execute();
        con.close();
    }
}
