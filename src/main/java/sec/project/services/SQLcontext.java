package sec.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    @FunctionalInterface
    public interface FunctionSQLException<P, R> {
        R apply(P p) throws SQLException;
    }


    public <T> T executeAndCommit(FunctionSQLException<Connection, T> callback) throws SQLException {
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

    public <T> T execute(FunctionSQLException<Connection, T> callback) throws SQLException {
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

    public <T> T statement(Connection conn, FunctionSQLException<Statement, T> callback) throws SQLException {
        Statement stm = null;
        try{
            stm = conn.createStatement();
            T res = callback.apply(stm);
            return res;
        }
        finally{
            if(stm != null)
                stm.close();
        }
    }

}
