package flightDemo.dto;

import flightDemo.enums.Traffic;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.QueryParam;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RegisterForReflection
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchRequest {

    @QueryParam("flightId")
    private String flightId;

    @QueryParam("carrierCode")
    private String carrierCode;

    @QueryParam("flightNumber")
    private Long flightNumber;

    @QueryParam("flightDate")
    private LocalDate flightDate;

    @QueryParam("traffic")
    private Traffic traffic;

    @QueryParam("departureAirport")
    private String departureAirport;

    @QueryParam("arrivalAirport")
    private String arrivalAirport;
}
