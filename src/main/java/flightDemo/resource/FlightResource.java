package flightDemo.resource;

import flightDemo.dto.FlightDTO;
import flightDemo.dto.FlightSearchRequest;
import flightDemo.mapper.FlightMapper;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import flightDemo.service.FlightService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/flight")
public class FlightResource {

    private final FlightService flightService;
    private final FlightMapper flightMapper;

    public FlightResource(FlightService flightService, FlightMapper flightMapper) {
        this.flightService = flightService;
        this.flightMapper = flightMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PageResult<FlightDTO> findAll(@BeanParam FlightSearchRequest request, @BeanParam Pageable pageable) {
        return flightService.findAll(request, pageable).map(flightMapper::mapToDTO);
    }

}
