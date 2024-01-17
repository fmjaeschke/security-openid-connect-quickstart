package org.acme.security.openid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmployeeEntity {

    private String email;
    private String id;
    private String role;
    private String name;
}
