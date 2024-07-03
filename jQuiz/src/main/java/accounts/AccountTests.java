package accounts;

import database.DBInfo;
import database.Database;
import database.UserDatabase;
import junit.framework.TestCase;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

public class AccountTests extends TestCase {
    BasicDataSource basicDataSource = new BasicDataSource();
    UserDatabase userdb = null;
    public AccountTests() {
        basicDataSource.setUrl("jdbc:mysql://" + DBInfo.SERVER + "/" + DBInfo.NAME);
        basicDataSource.setUsername(DBInfo.USERNAME);
        basicDataSource.setPassword(DBInfo.PASSWORD);
        userdb = new UserDatabase(basicDataSource, Database.USER_DB);
    }

    public void testHashing() {
        String hash1 = Security.getHash("password");
        String hash2 = Security.getHash("password");
        assertEquals(hash1,hash2);

        String hash3 = Security.getHash("PaSSword");
        assert(!hash1.equals(hash3));

        String hash4 = Security.getHash("password1");
        assert(!hash4.equals(hash2));
        assert(hash3.equals(Security.getHash("PaSSword")));
    }

    public void testPasswordStrength() {
        assert(Security.isStrong("StrongPassword101"));
        assert(!Security.isStrong("short"));
        assert(!Security.isStrong("weakpassword"));
        assert(!Security.isStrong("notStrongEnough"));
    }

    public void testRegister() throws SQLException, ClassNotFoundException {
        int result = Register.createNew("luka","mail@mail.com","pass","pass",null,userdb);
        assertEquals(Register.USERNAME_EXISTS,result);

        result = Register.createNew("newluka","luka@example.com","pass","pass",null,userdb);
        assertEquals(Register.EMAIL_EXISTS,result);

        result = Register.createNew("newluka","newluka@example.com","pass1","pass2",null,userdb);
        assertEquals(Register.DIFF_PASSWORDS,result);

        result = Register.createNew("newluka","newluka@example.com","pass1","pass1",null,userdb);
        assertEquals(Register.WEAK_PASSWORD,result);

        result = Register.createNew("newluka","newluka@example.com","Strongpass1","Strongpass1",null,userdb);
        assertEquals(Register.SUCCESS,result);
    }

    public void testLogin() throws SQLException, ClassNotFoundException {
        int result = Login.login("ProbablyDoesNotExist","password",userdb);
        assertEquals(Login.NO_USER,result);

        result = Login.login("luka","LetMeIIIn",userdb);
        assertEquals(Login.WRONG_PASSWORD,result);
    }
}
