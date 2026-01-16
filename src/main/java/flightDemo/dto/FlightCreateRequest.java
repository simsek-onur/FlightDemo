package flightDemo.dto;


import flightDemo.enums.Traffic;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@RegisterForReflection
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightCreateRequest {

    @NotNull(message = "Flight ID cannot be null.")
    private String flightId;

    @NotNull(message = "Carrier Code cannot be null.")
    private String carrierCode;

    @NotNull(message = "Flight Number cannot be null.")
    private Long flightNumber;

    @NotNull(message = "Flight Date cannot be null.")
    private LocalDate flightDate;

    @NotNull(message = "Traffic cannot be null.")
    private Traffic traffic;

    @NotNull(message = "Departure Airport cannot be null.")
    private String departureAirport;

    @NotNull(message = "Arrival Airport cannot be null.")
    private String arrivalAirport;
}
