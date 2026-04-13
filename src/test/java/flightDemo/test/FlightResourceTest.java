package flightDemo.test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(flightDemo.test.PostgresTestResource.class)
class FlightResourceTest {

    // ── CREATE ───────────────────────────────────────────────────────────

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
                .body("carrierCode", equalTo("TK"))
                .body("flightNumber", equalTo(123))
                .body("flightDate", equalTo("2026-01-16"))
                .body("traffic", equalTo("INTERNATIONAL"))
                .body("departureAirport", equalTo("IST"))
                .body("arrivalAirport", equalTo("LHR"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue());

        given()
                .when().get("/flight/flight-id/TEST-FLT-1")
                .then()
                .statusCode(200)
                .body("flightId", equalTo("TEST-FLT-1"))
                .body("traffic", equalTo("INTERNATIONAL"));
    }

    @Test
    void createShouldFailWhenFlightIdIsNull() {
        String payload = """
            {
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
                .statusCode(400);
    }

    @Test
    void createShouldFailWhenCarrierCodeIsNull() {
        String payload = """
            {
              "flightId": "MISSING-CARRIER",
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
                .statusCode(400);
    }

    @Test
    void createShouldFailWhenBodyIsEmpty() {
        given()
                .contentType("application/json")
                .body("{}")
                .when().post("/flight")
                .then()
                .statusCode(400);
    }

    // ── FIND BY ID ───────────────────────────────────────────────────────

    @Test
    void findByIdShouldReturn404WhenNotFound() {
        given()
                .when().get("/flight/id/999999")
                .then()
                .statusCode(404)
                .body("message", containsString("999999"));
    }

    @Test
    void findByFlightIdShouldReturn404WhenNotFound() {
        given()
                .when().get("/flight/flight-id/NON-EXISTENT")
                .then()
                .statusCode(404)
                .body("message", containsString("NON-EXISTENT"));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────

    @Test
    void shouldUpdateFlightSuccessfully() {
        String createPayload = """
            {
              "flightId": "UPDATE-FLT-1",
              "carrierCode": "TK",
              "flightNumber": 100,
              "flightDate": "2026-01-16",
              "traffic": "DOMESTIC",
              "departureAirport": "IST",
              "arrivalAirport": "ANK"
            }
            """;

        given()
                .contentType("application/json")
                .body(createPayload)
                .when().post("/flight")
                .then()
                .statusCode(201);

        String updatePayload = """
            {
              "flightId": "UPDATE-FLT-1",
              "carrierCode": "OA",
              "flightNumber": 200,
              "flightDate": "2026-02-20",
              "traffic": "INTERNATIONAL",
              "departureAirport": "IST",
              "arrivalAirport": "LHR"
            }
            """;

        given()
                .contentType("application/json")
                .body(updatePayload)
                .when().put("/flight/flight-id/UPDATE-FLT-1")
                .then()
                .statusCode(200)
                .body("carrierCode", equalTo("OA"))
                .body("flightNumber", equalTo(200))
                .body("flightDate", equalTo("2026-02-20"))
                .body("traffic", equalTo("INTERNATIONAL"))
                .body("arrivalAirport", equalTo("LHR"));
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
                .statusCode(400)
                .body("message", containsString("Path flightId must match body flightId"));
    }

    @Test
    void updateShouldReturn404WhenFlightDoesNotExist() {
        String payload = """
            {
              "flightId": "GHOST-FLT",
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
                .when().put("/flight/flight-id/GHOST-FLT")
                .then()
                .statusCode(404);
    }

    // ── LIST / PAGINATION ────────────────────────────────────────────────

    @Test
    void shouldReturnPageResultFromFindAll() {
        given()
                .when().get("/flight?page=0&size=10")
                .then()
                .statusCode(200)
                .body("currentPage", equalTo(0))
                .body("pageSize", equalTo(10))
                .body("totalCount", isA(Integer.class))
                .body("totalPages", isA(Integer.class))
                .body("content", isA(java.util.List.class))
                .body("hasNext", isA(Boolean.class));
    }

    @Test
    void shouldFilterByCarrierCode() {
        // Create a flight with specific carrier code
        String payload = """
            {
              "flightId": "FILTER-CARRIER-1",
              "carrierCode": "ZZ",
              "flightNumber": 777,
              "flightDate": "2026-03-01",
              "traffic": "DOMESTIC",
              "departureAirport": "IST",
              "arrivalAirport": "ANK"
            }
            """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/flight")
                .then()
                .statusCode(201);

        given()
                .when().get("/flight?carrierCode=ZZ")
                .then()
                .statusCode(200)
                .body("totalCount", greaterThanOrEqualTo(1))
                .body("content.carrierCode", everyItem(equalTo("ZZ")));
    }

    @Test
    void shouldFilterByTraffic() {
        String payload = """
            {
              "flightId": "FILTER-TRAFFIC-1",
              "carrierCode": "TK",
              "flightNumber": 500,
              "flightDate": "2026-03-01",
              "traffic": "DOMESTIC",
              "departureAirport": "IST",
              "arrivalAirport": "ANK"
            }
            """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/flight")
                .then()
                .statusCode(201);

        given()
                .when().get("/flight?traffic=DOMESTIC")
                .then()
                .statusCode(200)
                .body("totalCount", greaterThanOrEqualTo(1))
                .body("content.traffic", everyItem(equalTo("DOMESTIC")));
    }

    @Test
    void shouldFilterByDepartureAirport() {
        given()
                .when().get("/flight?departureAirport=IST")
                .then()
                .statusCode(200)
                .body("content.departureAirport", everyItem(equalTo("IST")));
    }

    @Test
    void shouldReturnEmptyContentWhenNoResultsMatchFilter() {
        given()
                .when().get("/flight?carrierCode=NONEXISTENT")
                .then()
                .statusCode(200)
                .body("totalCount", equalTo(0))
                .body("content", hasSize(0));
    }

    // ── PAGINATION VALIDATION ────────────────────────────────────────────

    @Test
    void shouldRejectPageSizeOverMax() {
        given()
                .when().get("/flight?page=0&size=101")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectNegativePageSize() {
        given()
                .when().get("/flight?page=0&size=0")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectNegativePageNumber() {
        given()
                .when().get("/flight?page=-1&size=10")
                .then()
                .statusCode(400);
    }
}
