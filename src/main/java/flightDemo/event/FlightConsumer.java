package flightDemo.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import flightDemo.dto.FlightCreateRequest;
import flightDemo.enums.Traffic;
import flightDemo.service.FlightService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class FlightConsumer {

    private static final Logger LOG = Logger.getLogger(FlightConsumer.class);

    private final ObjectMapper objectMapper;
    private final FlightService flightService;

    public FlightConsumer(ObjectMapper objectMapper, FlightService flightService) {
        this.objectMapper = objectMapper;
        this.flightService = flightService;
    }

    @Incoming("flight-context")
    @Blocking
    @Transactional
    public CompletionStage<Void> consume(Message<String> message) {
        try {
            FlightPayload payload = objectMapper.readValue(message.getPayload(), FlightPayload.class);

            FlightCreateRequest flightCreateRequest = FlightCreateRequest.builder()
                    .flightId(payload.getFlightId())
                    .carrierCode(payload.getCarrierCode())
                    .flightNumber(payload.getFlightNumber())
                    .flightDate(payload.getFlightDate())
                    .departureAirport(payload.getDepartureAirport())
                    .arrivalAirport(payload.getArrivalAirport())
                    .build();

            flightCreateRequest.setTraffic(Traffic.valueOf(payload.getTraffic()));

            flightService.upsertFlight(flightCreateRequest);
            LOG.infof("Consumed flightId=%s", payload.getFlightId());

            return message.ack();
        } catch (Exception e) {
            LOG.errorf(e, "Failed to process flight message: %s", e.getMessage());
            return message.nack(e);
        }
    }
}
