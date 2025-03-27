package com.lulippe.safetynetalerts.api;

import com.lulippe.safetynetalerts.dto.PersonCreateDto;
import com.lulippe.safetynetalerts.model.Person;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/person")
public interface PersonApi {

    //Create Person
    @Operation(
            operationId = "createPerson",
            summary = "Create a new Person",
            tags ={"person"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "person create successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Person.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Person already exist"
                    ),
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<String> createPerson(@RequestBody final PersonCreateDto personCreateDto);
}
