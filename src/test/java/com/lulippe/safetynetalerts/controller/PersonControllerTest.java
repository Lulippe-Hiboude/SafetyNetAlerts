package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonController.class)
@AutoConfigureMockMvc
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService personService;

    @Test
    @DisplayName("should create a person")
    void shouldCreatePerson() throws Exception {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doNothing().when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person create successfully : " + personCreateDto));
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null for CreatePersonDto")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsNullForCreatePersonDto() throws Exception {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName(null)
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doThrow(new InvalidDataException("firstName or lastName is blank ")).when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }

    @Test
    @DisplayName("should return 400 when personCreateDto has null values")
    void shouldReturnBadRequestWhenPersonCreateDtoHasNullValues() throws Exception {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName(null)
                .lastName(null)
                .address(null)
                .city(null)
                .zip(null)
                .phone(null)
                .email(null)
                .build();

        doThrow(new InvalidDataException("personDto is null")).when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("personDto is null"));
    }


    @Test
    @DisplayName("should return 409 when person already exists")
    void shouldReturnConflictWhenPersonAlreadyExists() throws Exception {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("johnny")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doThrow(new ObjectAlreadyExistsException("Person already exists")).when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Person already exists"));
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null for UpdatePersonDto")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsNullForUpdatePersonDto() throws Exception {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName(null)
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doThrow(new InvalidDataException("firstName or lastName is blank ")).when(personService).handlePersonUpdateRequest(personUpdateDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }

    @Test
    @DisplayName("should return 400 when personUpdateDto has null values")
    void shouldReturnBadRequestWhenPersonUpdateDtoHasNullValues() throws Exception {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName(null)
                .lastName(null)
                .address(null)
                .city(null)
                .zip(null)
                .phone(null)
                .email(null)
                .build();

        doThrow(new InvalidDataException("personDto is null")).when(personService).handlePersonUpdateRequest(personUpdateDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("personDto is null"));
    }

    @Test
    @DisplayName("should return 404 when person does not exist")
    void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();
        doThrow(new InexistantObjectException("the person with firstName:" + personUpdateDto.getFirstName() + " and lastName: " + personUpdateDto.getLastName() +
                " does not exist")).when(personService).handlePersonUpdateRequest(personUpdateDto);


        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("the person with firstName:" + personUpdateDto.getFirstName() + " and lastName: "
                        + personUpdateDto.getLastName() + " does not exist"));
    }

    @Test
    @DisplayName("should update a Person")
    void shouldUpdatePerson() throws Exception {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();
        doNothing().when(personService).handlePersonUpdateRequest(personUpdateDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person update successfully : " + personUpdateDto));
    }

    @Test
    @DisplayName("should delete a Person and return 200 status")
    void shouldDeletePerson() throws Exception {
        //given
        String firstName = "John";
        String lastName = "Doe";
        doNothing().when(personService).handlePersonDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Person with firstName " + firstName + " and lastName " + lastName + ", delete successfully."));
    }

    @Test
    @DisplayName("should return 404 when person does not exist for deletion")
    void shouldReturnNotFoundWhenPersonDoesNotExistForDeletion() throws Exception {
        //given
        String firstName = "John";
        String lastName = "Doe";
        doThrow(new InexistantObjectException("Person with firstName " + firstName + " and lastName " + lastName + " does not exist"))
                .when(personService).handlePersonDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person with firstName " + firstName + " and lastName " + lastName + " does not exist"));
    }

    @Test
    @DisplayName("should return 400 status when firstName or lastName is null for delete person")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsBlankForDeletePerson() throws Exception {
        //given
        String firstName = "John";
        String lastName = "  ";
        doThrow(new InvalidDataException("firstName or lastName is blank "))
                .when(personService).handlePersonDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }
}