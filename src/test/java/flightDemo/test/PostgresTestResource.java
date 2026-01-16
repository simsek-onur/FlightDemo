package flightDemo.test;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("flightdemo")
                    .withUsername("app")
                    .withPassword("app");

    @Override
    public Map<String, String> start() {
        POSTGRES.start();

        Map<String, String> props = new HashMap<>();
        props.put("quarkus.datasource.db-kind", "postgresql");
        props.put("quarkus.datasource.jdbc.url", POSTGRES.getJdbcUrl());
        props.put("quarkus.datasource.username", POSTGRES.getUsername());
        props.put("quarkus.datasource.password", POSTGRES.getPassword());

        props.put("quarkus.hibernate-orm.database.generation", "validate");
        props.put("quarkus.liquibase.migrate-at-start", "true");
        props.put("quarkus.liquibase.change-log", "db/changeLog.xml");

        return props;
    }

    @Override
    public void stop() {
        POSTGRES.stop();
    }
}