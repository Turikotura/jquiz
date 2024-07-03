package database;



import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Database<T> {
    public static final String USER_DB = "users";
    public static final String QUIZ_DB = "quizzes";
    public static final String QUIZ_VIEW = "quizzes_view";
    public static final String QUESTION_DB = "questions";
    public static final String ANSWER_DB = "answers";
    public static final String MAIL_DB = "mails";
    public static final String FRIEND_DB = "friends";
    public static final String BANNED_DB = "banned_users";
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
        String query = String.format("SELECT * FROM %s;", databaseName);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query, con);
        List<T> res = statementToList(ps);
        con.close();
        return res;
    }
    public T getById(int id) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE id = ?;", databaseName);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query, con);
        ps.setInt(1,id);
        T res = statementToElement(ps);
        con.close();
        return res;
    }
    public boolean removeById(int id) throws SQLException, ClassNotFoundException {
        String query = String.format("DELETE FROM %s WHERE id = ?", databaseName);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query, con);
        ps.setInt(1,id);
        boolean res = ps.executeUpdate() > 0;
        con.close();
        return res;
    }
    public boolean setFieldById(int id, String columnName, Object value) throws SQLException, ClassNotFoundException {
        String query = String.format("UPDATE %s SET %s = ? WHERE id = ?", databaseName, columnName);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query, con);

        if(value instanceof String){
            ps.setString(1,(String) value);
        }else if(value instanceof Integer){
            ps.setInt(1, (int) value);
        }else if(value instanceof Boolean){
            ps.setBoolean(1, (boolean) value);
        }else if(value instanceof Timestamp){
            ps.setTimestamp(1, (Timestamp) value);
        }else if(value instanceof byte[]){
            ps.setBytes(1, (byte[]) value);
        }else{
            System.out.println("Invalid format");
            return false;
        }

        ps.setInt(2,id);
        boolean res = ps.executeUpdate() > 0;
        con.close();
        return res;
    }
    public abstract int add(T toAdd) throws SQLException, ClassNotFoundException;
    protected abstract T getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException;
    protected  PreparedStatement getStatement(String query, Connection con) throws ClassNotFoundException, SQLException {
        return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }
    protected Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = dataSource.getConnection();
        return con;
    }
    protected List<T> statementToList(PreparedStatement ps) throws SQLException, ClassNotFoundException {
        List<T> res = new ArrayList<>();
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            res.add(getItemFromResultSet(rs));
        }
        return res;
    }
    protected T statementToElement(PreparedStatement ps) throws SQLException, ClassNotFoundException {
        T res = null;
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        if(rs.next()){
            res = getItemFromResultSet(rs);
        }
        return res;
    }

    protected List<T> queryToList(String query, Function<PreparedStatement,PreparedStatement> statementBindFunc) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps = statementBindFunc.apply(ps);
        List<T> res = statementToList(ps);
        con.close();
        return res;
    }
    protected T queryToElement(String query, Function<PreparedStatement,PreparedStatement> statementBindFunc) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps = statementBindFunc.apply(ps);
        T res = statementToElement(ps);
        con.close();
        return res;
    }
}