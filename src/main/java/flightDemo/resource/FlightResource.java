package flightDemo.resource;

import flightDemo.dto.FlightCreateRequest;
import flightDemo.dto.FlightDTO;
import flightDemo.dto.FlightSearchRequest;
import flightDemo.mapper.FlightMapper;
import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import flightDemo.service.FlightService;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;

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
    public PageResult<FlightDTO> findAll(@BeanParam @Valid FlightSearchRequest request, @BeanParam @Valid Pageable pageable) {
        return flightService.findAll(request, pageable).map(flightMapper::mapToDTO);
    }

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByIdOptional(@PathParam("id") Long id) {
        var dto = flightService.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + id));

        return Response.ok(dto).build();
    }

    @GET
    @Path("/flight-id/{flightId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "flight-by-id")
    public FlightDTO findByFlightId(@PathParam("flightId") @CacheKey String flightId) {

        var entity = flightService.findByFlightId(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));

        return flightMapper.mapToDTO(entity);
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
    public Response update(@PathParam("flightId") String flightId,@Valid FlightCreateRequest request) {
        if (Objects.nonNull(request.getFlightId()) && !flightId.equals(request.getFlightId())) {
            throw new BadRequestException("Path flightId must match body flightId");
        }

        var updated = flightService.updateByFlightId(flightId, request);
        return Response.ok(flightMapper.mapToDTO(updated)).build();
    }


}
