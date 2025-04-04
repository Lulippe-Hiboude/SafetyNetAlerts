package com.lulippe.safetynetalerts.api;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.model.Person;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/**
 * API for managing Person-related operations.
 *
 * <p>This interface provides endpoints for creating, updating, deleting Person resources.</p>
 *
 **/

 @RequestMapping("/person")
public interface PersonApi {

/**
 * <h3>Endpoint: Create a new Person</h3>
 *  <p>
 *  This method handles the creation of a new Person resource. The client provides a {@link PersonDto}
 *  containing the person's details. If the person is successfully created, the server responds with
 *  a 201 Created status and the created person's information. If the input data is invalid or the
 *  person already exists, appropriate error responses are returned.
 *  </p>
 *
 *  @param personCreateDto the details of the person to create (must not be null)
 *  @return {@link ResponseEntity} with an appropriate status code:
 *          <ul>
 *            <li> 201: If the person is created successfully </li>
 *            <li> 400: If the input data is invalid </li>
 *            <li> 409: If a person with the same details already exists </li>
 *          </ul>
 *  @throws IllegalArgumentException if the provided {@code personCreateDto} is null
 **/
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
    ResponseEntity<String> createPerson(@RequestBody final PersonDto personCreateDto);

    @Operation(
            operationId = "updatePerson",
            summary = "Update an existing Person",
            tags ={"person"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "person update successfully",
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
                            responseCode = "404",
                            description = "Person does not exist"
                    ),
            }
    )
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<String> updatePerson(@RequestBody final PersonDto personUpdateDto);
}
