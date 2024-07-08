package database;

import models.Tag;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDatabase extends Database<Tag> {
    // Constants for column names
    static final String ID = "id";
    static final String NAME = "name";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TAG_ID = "tag_id";

    public TagDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new entry to tags table
     * @param tag Tag Object describing new row
     * @return id of the new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Assembles Tag object from ResultSet
     * @param rs ResultSet of tag table rows
     * @return Tag object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Tag getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Tag(
                rs.getInt(ID),
                rs.getString(NAME)
        );
    }

    /**
     * Gets the tag with name specified
     * @param name Name of the tag
     * @return Tag with the name specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get every tag that exists
     * @return List of every tag in the database
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Tag> getAllTags() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s",
                databaseName);
        return queryToList(query, (ps) -> {
            return ps;
        });
    }

    /**
     * Adds new tag to the quiz
     * @param quizId Id of the quiz
     * @param tagId Id of the tag
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void associateTagWithQuiz(int quizId, int tagId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s (quiz_id, tag_id) VALUES (?, ?);",
                Database.TAG_TO_QUIZ_DB);
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

    /**
     * Get all the tags a quiz has
     * @param quizId Id of the quiz
     * @return List of every Tag quiz has
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Tag> getTagsForQuiz(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT t.* FROM %s t JOIN %s tq ON t.id = tq.tag_id WHERE tq.quiz_id = ?;",
                Database.TAG_DB, Database.TAG_TO_QUIZ_DB);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1, quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Get every tag that contains searchString as substring
     * @param searchString String we are searching by
     * @return List of tags that contains searchString as substring
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Remove tags connected to quiz specified
     * @param quizId Id of the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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
