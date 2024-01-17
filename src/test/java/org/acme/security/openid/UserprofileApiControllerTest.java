package org.acme.security.openid;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.DockerClientFactory;

import java.util.Optional;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(UserprofileApiController.class)
@QuarkusTestResource(OidcWiremockTestResource.class)
@EnabledIf(value = "isDockerAvailable", disabledReason = "Docker daemon not reachable")
class UserprofileApiControllerTest extends AbstractApiIntegrationTest {

    @InjectMock
    EmployeeRepository repository;

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    void verifyErrorIsReturnedWhenAnonymous() {
        get()
                .then()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void verifyErrorIsReturnedWhenBearerTokenIsSend() {
        given()
                .header("Authorization", "Bearer " + getAccessToken("jqconsultant"))
                .when()
                .get()
                .then()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void verifyErrorIsReturnedWhenExpired() {
        given()
                .header("X-Auth-Token", getExpiredAccessToken("expired"))
                .when()
                .get()
                .then()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void verifyErrorIsReturnedWithUnknownAuthenticatedUser() {
        String mockUserName = "jqconsultant";
        when(repository.findById(matches(mockUserName))).thenReturn(Optional.empty());

        given()
                .header("X-Auth-Token", getAccessToken("jqconsultant"))
                .when()
                .get()
                .then()
                .statusCode(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void verifyUserIsReturnedWhenAuthenticated() {
        String mockUserName = "jqconsultant";
        EmployeeEntity mockEmployee = EmployeeEntity.builder().email("jqconsultant@redhat.com")
                .id("jqconsultant").role("Senior Consultant").name("John Q. Consultant").build();

        when(repository.findById(matches(mockUserName))).thenReturn(Optional.of(mockEmployee));

        given()
                .header("X-Auth-Token", getAccessToken("jqconsultant"))
                .when()
                .get()
                .then()
                .statusCode(HttpServletResponse.SC_OK)
                .body("email", Matchers.equalTo("jqconsultant@redhat.com"))
                .body("id", Matchers.equalTo("jqconsultant"))
                .body("role", Matchers.equalTo("Senior Consultant"))
                .body("name", Matchers.equalTo("John Q. Consultant"));
    }

    static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
