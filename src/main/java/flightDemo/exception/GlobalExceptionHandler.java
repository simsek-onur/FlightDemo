package flightDemo.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof NotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
        }

        if (exception instanceof BadRequestException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof ConstraintViolationException cve) {
            var fieldErrors = cve.getConstraintViolations().stream()
                    .map(v -> new ErrorResponse.FieldError(
                            extractFieldName(v.getPropertyPath().toString()),
                            v.getMessage()))
                    .toList();
            var body = new ErrorResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    "Validation Failed",
                    "One or more fields failed validation",
                    fieldErrors);
            return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
        }

        LOG.errorf(exception, "Unhandled exception: %s", exception.getMessage());
        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private Response buildResponse(Response.Status status, String message) {
        var body = new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), message);
        return Response.status(status).entity(body).build();
    }

    private String extractFieldName(String propertyPath) {
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }
}
