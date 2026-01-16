package flightDemo.test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(flightDemo.test.PostgresTestResource.class)
class FlightResourceTest {

    @Test
    void shouldCreateFlightAndFetchByFlightId() {
        String payload = """
            {
              "flightId": "TEST-FLT-1",
              "carrierCode": "TK",
              "flightNumber": 123,
              "flightDate": "2026-01-16",
              "traffic": "INTERNATIONAL",
              "departureAirport": "IST",
              "arrivalAirport": "LHR"
            }
            """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/flight")
                .then()
                .statusCode(201)
                .body("flightId", equalTo("TEST-FLT-1"))
                .body("carrierCode", equalTo("TK"));

        given()
                .when().get("/flight/flight-id/TEST-FLT-1")
                .then()
                .statusCode(200)
                .body("flightId", equalTo("TEST-FLT-1"))
                .body("traffic", equalTo("INTERNATIONAL"));
    }

    @Test
    void shouldReturnPageResultFromFindAll() {
        given()
                .when().get("/flight?page=0&size=10")
                .then()
                .statusCode(200)
                .body("currentPage", isA(Integer.class))
                .body("pageSize", isA(Integer.class))
                .body("totalCount", isA(Integer.class))
                .body("totalPages", isA(Integer.class))
                .body("content", isA(java.util.List.class));
    }

    @Test
    void updateShouldFailWhenPathAndBodyFlightIdMismatch() {
        String payload = """
            {
              "flightId": "DIFFERENT-ID",
              "carrierCode": "TK",
              "flightNumber": 999,
              "flightDate": "2026-01-16",
              "traffic": "INTERNATIONAL",
              "departureAirport": "IST",
              "arrivalAirport": "LHR"
            }
            """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().put("/flight/flight-id/TEST-FLT-1")
                .then()
                .statusCode(400);
    }
}