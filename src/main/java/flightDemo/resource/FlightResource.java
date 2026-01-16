package flightDemo.resource;

import flightDemo.dto.FlightCreateRequest;
import flightDemo.dto.FlightDTO;
import flightDemo.dto.FlightSearchRequest;
import flightDemo.mapper.FlightMapper;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import flightDemo.service.FlightService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;
import java.util.Optional;

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

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FlightDTO> findByIdOptional(@PathParam("id") Long id) {
        return flightService.findByIdOptional(id).map(flightMapper::mapToDTO);
    }

    @GET
    @Path("/flight-id/{flightId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FlightDTO> findByFlightId(@PathParam("flightId") String flightId) {
        return flightService.findByFlightId(flightId).map(flightMapper::mapToDTO);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(FlightCreateRequest request) {
        var created = flightService.createFlight(request);
        return Response.status(Response.Status.CREATED)
                .entity(flightMapper.mapToDTO(created))
                .build();
    }

    @PUT
    @Path("/flight-id/{flightId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("flightId") String flightId, FlightCreateRequest request) {
        if (Objects.nonNull(request.getFlightId()) && !flightId.equals(request.getFlightId())) {
            throw new BadRequestException("Path flightId must match body flightId");
        }

        var updated = flightService.updateByFlightId(flightId, request);
        return Response.ok(flightMapper.mapToDTO(updated)).build();
    }


}
