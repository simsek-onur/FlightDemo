package flightDemo.repository;

import flightDemo.dto.FlightSearchRequest;
import flightDemo.entity.Flight;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class FlightRepository implements Repository<Flight,Long>{

    public Optional<Flight> findByFlightId(String flightId) {
        return find("flightId", flightId).singleResultOptional();
    }

    public PageResult<Flight> findAll(FlightSearchRequest request, Pageable pageable) {
        StringBuilder queryString = new StringBuilder("select f from Flight f where 1=1");

        Parameters parameters = new Parameters();
        if(Objects.nonNull(request.getFlightId())){
            queryString.append(" and f.flightId = :flightId");
            parameters = parameters.and("flightId",request.getFlightId());
        }
        if(Objects.nonNull(request.getCarrierCode())){
            queryString.append(" and f.carrierCode = :carrierCode");
            parameters = parameters.and("carrierCode",request.getCarrierCode());
        }
        if(Objects.nonNull(request.getFlightNumber())){
            queryString.append(" and f.flightNumber = :flightNumber");
            parameters = parameters.and("flightNumber",request.getFlightNumber());
        }
        if(Objects.nonNull(request.getFlightDate())){
            queryString.append(" and f.flightDate = :flightDate");
            parameters = parameters.and("flightDate",request.getFlightDate());
        }
        if(Objects.nonNull(request.getTraffic())){
            queryString.append(" and f.traffic = :traffic");
            parameters = parameters.and("traffic",request.getTraffic());
        }
        if(Objects.nonNull(request.getDepartureAirport())){
            queryString.append(" and f.departureAirport = :departureAirport");
            parameters = parameters.and("departureAirport",request.getDepartureAirport());
        }
        if(Objects.nonNull(request.getArrivalAirport())){
            queryString.append(" and f.arrivalAirport = :arrivalAirport");
            parameters = parameters.and("arrivalAirport",request.getArrivalAirport());
        }
        var query = find(queryString.toString(),parameters);
        return createPageQuery(query,pageable);
    }
}
