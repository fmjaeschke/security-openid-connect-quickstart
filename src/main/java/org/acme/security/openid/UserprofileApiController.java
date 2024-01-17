package org.acme.security.openid;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("api")
@Authenticated
@RequestScoped
public class UserprofileApiController {

    @Context
    private SecurityContext securityContext;

    private final EmployeeRepository employeeRepository;

    public UserprofileApiController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response me() {
        return employeeRepository.findById(securityContext.getUserPrincipal().getName())
                .map(employeeEntity -> Response.ok(employeeEntity).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());

    }
}
