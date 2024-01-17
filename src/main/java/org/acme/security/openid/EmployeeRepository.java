package org.acme.security.openid;

import java.util.Optional;

public interface EmployeeRepository {
    Optional<EmployeeEntity> findById(String employeeId);
}
