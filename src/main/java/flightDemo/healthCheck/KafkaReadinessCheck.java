package flightDemo.healthCheck;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.util.Properties;

@Readiness
@ApplicationScoped
public class KafkaReadinessCheck implements HealthCheck {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @Override
    public HealthCheckResponse call() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "1500");
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "1500");

        try (AdminClient admin = AdminClient.create(props)) {
            admin.describeCluster().nodes().get();
            return HealthCheckResponse.named("kafka")
                    .up()
                    .withData("bootstrapServers", bootstrapServers)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("kafka")
                    .down()
                    .withData("bootstrapServers", bootstrapServers)
                    .withData("error", e.getClass().getSimpleName() + ": " + e.getMessage())
                    .build();
        }
    }
}
