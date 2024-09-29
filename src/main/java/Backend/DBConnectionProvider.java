package Backend;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

    public class DBConnectionProvider {

        private final DataSource dataSource;

        public DBConnectionProvider() {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl("jdbc:mysql://localhost:3306/teste");
            basicDataSource.setUsername("root");
            basicDataSource.setPassword("Pedro1205*");

            this.dataSource = basicDataSource;
        }

        public JdbcTemplate getConnection() {
            return new JdbcTemplate(dataSource);
        }
    }

