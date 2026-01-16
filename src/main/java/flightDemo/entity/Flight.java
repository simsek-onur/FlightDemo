package flightDemo.entity;

import flightDemo.enums.Traffic;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.envers.Audited;
import org.hibernate.generator.EventType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Audited
@Table(name = "FLIGHT",
        uniqueConstraints = {
                @UniqueConstraint(name = "UNQ_FLIGHT_FLIGHT_ID", columnNames = {"FLIGHT_ID"})
        })
@RegisterForReflection
public class Flight {

    @Column(name = "ID")
    @GeneratedValue(generator = "SEQ_FLIGHT_LEG")
    @SequenceGenerator(name = "SEQ_FLIGHT_LEG", sequenceName = "SEQ_FLIGHT_LEG", allocationSize = 1)
    @Id
    private Long id;

    @Column(name = "FLIGHT_ID",nullable = false)
    private String flightId;

    @Column(name = "CARRIER_CODE",nullable = false)
    private String carrierCode;

    @Column(name = "FLIGHT_NUMBER",nullable = false)
    private Long flightNumber;

    @Column(name = "FLIGHT_DATE",nullable = false)
    private LocalDate flightDate;

    @Column(name = "TRAFFIC",nullable = false)
    @Enumerated(EnumType.STRING)
    private Traffic traffic;

    @Column(name = "DEPARTURE_AIRPORT",nullable = false)
    private String departureAirport;

    @Column(name = "ARRIVAL_AIRPORT",nullable = false)
    private String arrivalAirport;

    @CurrentTimestamp(event = EventType.INSERT, source = SourceType.VM)
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @CurrentTimestamp(event = {EventType.INSERT, EventType.UPDATE}, source = SourceType.VM)
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
