package com.lulippe.safetynetalerts.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PersonDto {
    @Valid
    private String firstName;
    @Valid
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}
