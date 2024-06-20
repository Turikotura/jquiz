package database;



import org.apache.commons.dbcp2.BasicDataSource;

import javax.persistence.Tuple;
import java.sql.*;
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
        return queryToList("SELECT * FROM " + databaseName, getConnection());
    }
    public T getById(int id) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        ResultSet rs = getResultSet("SELECT * FROM " + databaseName + " WHERE id = " + id,con);
        rs.next();
        T res = getItemFromResultSet(rs);
        con.close();
        return res;
    }
    public boolean removeById(int id) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement statement = getStatement("DELETE FROM " + databaseName + " WHERE id = " + id,con);
        boolean res = statement.executeUpdate() > 0;
        con.close();
        return res;
    }
    public boolean setFieldById(int id, String columnName, String value) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement statement = getStatement("UPDATE " + databaseName + " SET " + columnName + " = " + value + " WHERE id = " + id, con);
        boolean res = statement.executeUpdate() > 0;
        con.close();
        return res;
    }
    public abstract int add(T toAdd) throws SQLException, ClassNotFoundException;
    protected abstract T getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException;
    protected ResultSet getResultSet(String sqlQuery, Connection con) throws ClassNotFoundException, SQLException {
        PreparedStatement statement = getStatement(sqlQuery, con);
        statement.executeQuery();
        return statement.getResultSet();
    }
    protected  PreparedStatement getStatement(String sqlQuery, Connection con) throws ClassNotFoundException, SQLException {
        return con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    }
    protected Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = dataSource.getConnection();
        return con;
    }
    protected List<T> queryToList(String query, Connection con) throws SQLException, ClassNotFoundException {
        List<T> res = new ArrayList<>();
        ResultSet rs = getResultSet(query, con);
        while(rs.next()){
            res.add(getItemFromResultSet(rs));
        }
        con.close();
        return res;
    }
}