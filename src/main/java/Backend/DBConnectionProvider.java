package Backend;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

    public class DBConnectionProvider {
        private final DataSource dataSource;

        public DBConnectionProvider() {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl("jdbc:mysql://localhost:3306/vortex2");
            basicDataSource.setUsername("root");
            basicDataSource.setPassword("castrito");

            this.dataSource = basicDataSource;
        }

        public JdbcTemplate getConnection() { return new JdbcTemplate(dataSource); }
    }

