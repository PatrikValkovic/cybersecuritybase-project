package sec.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class SQLcontext {

    private final DatabaseConnection connection;
    private final DataSource source;

    @Autowired
    public SQLcontext(DatabaseConnection connection) {
        this.connection = connection;
        this.source = this.connection.dataSource();
    }


    public <T> T ExecuteAndCommit(Function<Connection, T> callback) throws SQLException {
        Connection conn = null;
        try {
            conn = this.source.getConnection();
            conn.setAutoCommit(false);
            T res = callback.apply(conn);
            conn.commit();
            return res;
        }
        finally{
            if(conn != null)
                conn.close();
        }
    }

    public <T> T Execute(Function<Connection, T> callback) throws SQLException {
        Connection conn = null;
        try {
            conn = this.source.getConnection();
            conn.setAutoCommit(false);
            T res = callback.apply(conn);
            conn.rollback();
            return res;
        }
        finally{
            if(conn != null)
                conn.close();
        }
    }

}
