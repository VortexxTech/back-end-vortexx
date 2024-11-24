package Backend;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

    public class DBConnectionProvider {
        private final DataSource dataSource;

        public DBConnectionProvider() {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl("jdbc:mysql://%s:3307/vortex".formatted(System.getenv("IP_EC2")));
            basicDataSource.setUsername(System.getenv("MYSQL_USER"));
            basicDataSource.setPassword(System.getenv("MYSQL_PASSWORD"));

            this.dataSource = basicDataSource;
        }

        public JdbcTemplate getConnection() { return new JdbcTemplate(dataSource); }
    }

