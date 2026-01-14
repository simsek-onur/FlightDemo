package flightDemo.repository;

import flightDemo.entity.Flight;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class FlightRepository implements Repository<Flight,Long>{

    public Optional<Flight> findByFlightId(String flightId) {
        return find("flightId", flightId).singleResultOptional();
    }

}
