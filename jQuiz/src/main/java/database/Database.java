package database;



import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Database<T> {
    public static final String USER_DB = "users";
    public static final String QUIZ_DB = "quizzes_view";
    public static final String QUESTION_DB = "questions";
    public static final String ANSWER_DB = "answers";
    public static final String MAIL_DB = "mails";
    public static final String FRIEND_DB = "friends";
    public static final String HISTORY_DB = "history";
    public static final String ACH_TO_USR_DB = "achToUser";
    public static final String ACHIEVEMENT_DB = "achievements";
    protected BasicDataSource dataSource;
    protected String databaseName;

    public Database(BasicDataSource dataSource, String databaseName){
        this.databaseName = databaseName;
        this.dataSource = dataSource;
    }

    public List<T> getAll() throws ClassNotFoundException, SQLException {
        ResultSet rs = getResultSet("SELECT * FROM " + databaseName);
        List<T> res = new ArrayList<T>();
        while (rs.next()){
            res.add(getItemFromResultSet(rs));
        }
        return res;
    }
    public T getById(int id) throws SQLException, ClassNotFoundException {
        ResultSet rs = getResultSet("SELECT * FROM " + databaseName + " WHERE id = " + id);
        rs.next();
        return getItemFromResultSet(rs);
    }
    public boolean removeById(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = getStatement("DELETE FROM " + databaseName + " WHERE id = " + id);
        return statement.executeUpdate() > 0;
    }
    public boolean setFieldById(int id, String columnName, String value) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = getStatement("UPDATE " + databaseName + " SET " + columnName + " = " + value + " WHERE id = " + id);
        return statement.executeUpdate() > 0;
    }
    public abstract boolean add(T toAdd) throws SQLException, ClassNotFoundException;
    protected abstract T getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException;
    protected ResultSet getResultSet(String sqlQuery) throws ClassNotFoundException, SQLException {
        PreparedStatement statement = getStatement(sqlQuery);
        statement.executeQuery();
        return statement.getResultSet();
    }
    protected PreparedStatement getStatement(String sqlQuery) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = dataSource.getConnection();
        return con.prepareStatement(sqlQuery);
    }
    protected List<T> queryToList(String query) throws SQLException, ClassNotFoundException {
        List<T> res = new ArrayList<>();
        ResultSet rs = getResultSet(query);
        while(rs.next()){
            res.add(getItemFromResultSet(rs));
        }
        return res;
    }
}