package flightDemo.service;

import flightDemo.dto.FlightCreateRequest;
import flightDemo.dto.FlightSearchRequest;
import flightDemo.entity.Flight;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import flightDemo.repository.FlightRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class FlightService {

    private final FlightRepository flightRepository;
    private static final Logger LOG = Logger.getLogger(FlightService.class);

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public void upsertFlight(FlightCreateRequest flightCreateRequest){
        var flightId = flightCreateRequest.getFlightId();
        var flightOptional = flightRepository.findByFlightId(flightId);
        if(flightOptional.isEmpty()){
            createFlight(flightCreateRequest);
        } else {
            var flight = flightOptional.get();
            updateFlight(flightCreateRequest,flight);
        }

        invalidateFlightByIdCache(flightId);
    }

    @Transactional
    public Flight createFlight(FlightCreateRequest flightCreateRequest){
        var flight = Flight.builder()
                .flightId(flightCreateRequest.getFlightId())
                .carrierCode(flightCreateRequest.getCarrierCode())
                .flightNumber(flightCreateRequest.getFlightNumber())
                .flightDate(flightCreateRequest.getFlightDate())
                .traffic(flightCreateRequest.getTraffic())
                .departureAirport(flightCreateRequest.getDepartureAirport())
                .arrivalAirport(flightCreateRequest.getArrivalAirport())
                .build();

        flightRepository.persist(flight);
        invalidateFlightByIdCache(flight.getFlightId());

        return flight;
    }

    @Transactional
    public Flight updateFlight(FlightCreateRequest flightCreateRequest,Flight flight){
        flight.setCarrierCode(flightCreateRequest.getCarrierCode());
        flight.setFlightNumber(flightCreateRequest.getFlightNumber());
        flight.setFlightDate(flightCreateRequest.getFlightDate());
        flight.setTraffic(flightCreateRequest.getTraffic());
        flight.setDepartureAirport(flightCreateRequest.getDepartureAirport());
        flight.setArrivalAirport(flightCreateRequest.getArrivalAirport());

        flightRepository.mergeAndFlush(flight);
        invalidateFlightByIdCache(flight.getFlightId());

        return flight;
    }

    public PageResult<Flight> findAll(FlightSearchRequest request, Pageable pageable) {
        return flightRepository.findAll(request,pageable);
    }

    public Optional<Flight> findByIdOptional(Long id) {
        return flightRepository.findByIdOptional(id);
    }

    public Optional<Flight> findByFlightId(String flightId) {
        return flightRepository.findByFlightId(flightId);
    }

    public Flight updateByFlightId(String flightId,FlightCreateRequest request) {
        var flightOptional = flightRepository.findByFlightId(flightId);
        if(flightOptional.isPresent()){
            return updateFlight(request,flightOptional.get());
        }

        LOG.errorf("Flight not found with id %s",flightId);
        throw new NotFoundException("Flight not found with id %s"+flightId);
    }

    @CacheInvalidate(cacheName = "flight-by-id")
    void invalidateFlightByIdCache(@CacheKey String flightId) {

    }
}
