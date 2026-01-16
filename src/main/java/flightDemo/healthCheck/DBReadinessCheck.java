package flightDemo.healthCheck;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.Connection;

@Readiness
@ApplicationScoped
public class DBReadinessCheck implements HealthCheck {

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (Connection c = dataSource.getConnection()) {
            boolean valid = c.isValid(2);
            return HealthCheckResponse.named("db")
                    .status(valid)
                    .withData("valid", valid)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("db")
                    .down()
                    .withData("error", e.getClass().getSimpleName())
                    .build();
        }
    }

}
