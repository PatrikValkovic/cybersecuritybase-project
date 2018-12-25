package sec.project.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;

@Service
public class SQLiteConnection implements DatabaseConnection {

    private final String driverClassName="org.sqlite.JDBC";
    private final String url="jdbc:sqlite:memory:myDb?cache=shared";
    private final String username="sa";
    private final String password="sa";

    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

}
