package dev.engineeringintelligence.api.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {
    static final long ACCOUNT_ID = 9_000_001L;
    static final long INSTALLATION_ID = 9_000_001L;
    static final long REPOSITORY_ID = 9_000_001L;

    private final DataSource dataSource;

    public DemoDataSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        new ResourceDatabasePopulator(new ClassPathResource("db/demo-data.sql")).execute(dataSource);
    }
}
