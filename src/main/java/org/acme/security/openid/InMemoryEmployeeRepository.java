package org.acme.security.openid;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class InMemoryEmployeeRepository implements EmployeeRepository, PanacheRepository<EmployeeEntity>
{
    @Override
    public Optional<EmployeeEntity> findById(String employeeId)
    {
        Uni<EmployeeEntity> employee = find("id", employeeId).firstResult();
        return employee.await().asOptional()
                .indefinitely();
    }
}
