package sec.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sec.project.domain.User;
import sec.project.services.SQLcontext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@Service
public class UsersRepository {

    private final SQLcontext context;

    @Autowired
    public UsersRepository(SQLcontext context) {
        this.context = context;
        this.checkDatabase();
    }

    private void checkDatabase() {
        try {
            this.context.executeAndCommit(conn -> {
                return this.context.statement(conn, stm -> {
                    String sql = "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT UNIQUE," +
                            "password TEXT);";
                    stm.execute(sql);
                    return 0;
                });
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> findAll() throws SQLException {
        return this.context.execute(connection -> {
            return this.context.statement(connection, stmt -> {
                String sql = "SELECT * FROM users";
                ResultSet resultset = stmt.executeQuery(sql);
                List<User> users = new LinkedList<>();
                while(resultset.next()){
                    User u = fillUser(resultset);
                    users.add(u);
                }
                return users;
            });
        });
    }

    public User find(long id) throws SQLException {
        return this.context.execute(connection -> {
            return this.context.statement(connection, stmt -> {
                String sql = "SELECT * FROM users WHERE id = " + id + ";";
                ResultSet resultset = stmt.executeQuery(sql);
                if(resultset.next()){
                    User u = fillUser(resultset);
                    return u;
                }
                return null;
            });
        });
    }

    private User fillUser(ResultSet resultset) throws SQLException {
        User u = new User();
        u.setId(resultset.getLong("id"));
        u.setUsername(resultset.getString("username"));
        u.setPassword(resultset.getString("password"));
        return u;
    }

    public void insert(User user) throws SQLException {
        this.context.executeAndCommit(connection -> {
            String sql = "INSERT INTO users(username, password) VALUES (?, ?);";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.execute();
            return 0;
        });
    }

    public User find(String username, String password) throws SQLException {
        return this.context.execute(connection -> {
            return this.context.statement(connection, stmt -> {
                String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "';";
                ResultSet resultset = stmt.executeQuery(sql);
                if(resultset.next()){
                    User u = fillUser(resultset);
                    return u;
                }
                return null;
            });
        });
    }
}
