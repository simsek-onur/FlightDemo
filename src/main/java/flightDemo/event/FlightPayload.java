package flightDemo.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@RegisterForReflection
public class FlightPayload {
    private String flightId;
    private String carrierCode;
    private Long flightNumber;
    private LocalDate flightDate;
    private String traffic;
    private String departureAirport;
    private String arrivalAirport;
}
