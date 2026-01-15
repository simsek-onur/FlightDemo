package flightDemo.service;

import flightDemo.dto.FlightCreateRequest;
import flightDemo.dto.FlightSearchRequest;
import flightDemo.entity.Flight;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import flightDemo.repository.FlightRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public void createOrUpdateFlight(FlightCreateRequest flightCreateRequest){
        var flightOptional = flightRepository.findByFlightId(flightCreateRequest.getFlightId());
        if(flightOptional.isEmpty()){
            createFlight(flightCreateRequest);
        } else {
            var flight = flightOptional.get();
            updateFlight(flightCreateRequest,flight);
        }

    }

    @Transactional
    public void createFlight(FlightCreateRequest flightCreateRequest){
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
    }

    @Transactional
    public void updateFlight(FlightCreateRequest flightCreateRequest,Flight flight){
        flight.setCarrierCode(flightCreateRequest.getCarrierCode());
        flight.setFlightNumber(flightCreateRequest.getFlightNumber());
        flight.setFlightDate(flightCreateRequest.getFlightDate());
        flight.setTraffic(flightCreateRequest.getTraffic());
        flight.setDepartureAirport(flightCreateRequest.getDepartureAirport());
        flight.setArrivalAirport(flightCreateRequest.getArrivalAirport());
        flightRepository.mergeAndFlush(flight);
    }

    public PageResult<Flight> findAll(FlightSearchRequest request, Pageable pageable) {
        return flightRepository.findAll(request,pageable);
    }
}
