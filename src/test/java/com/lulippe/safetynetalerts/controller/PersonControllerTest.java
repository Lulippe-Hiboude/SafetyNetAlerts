package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dto.PersonCreateDto;
import com.lulippe.safetynetalerts.exception.person_exception.InvalidPersonDataException;
import com.lulippe.safetynetalerts.exception.person_exception.PersonAlreadyExistsException;
import com.lulippe.safetynetalerts.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
    @DisplayName("should return 400 when firstName or lastName is null")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsNull() throws Exception {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName(null)
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doThrow(new InvalidPersonDataException("firstName or lastName is blank ")).when(personService).handlePersonCreationRequest(personCreateDto);

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
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName(null)
                .lastName(null)
                .address(null)
                .city(null)
                .zip(null)
                .phone(null)
                .email(null)
                .build();

        doThrow(new InvalidPersonDataException("personCreateDto is null")).when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("personCreateDto is null"));
    }


    @Test
    @DisplayName("should return 409 when person already exists")
    void shouldReturnConflictWhenPersonAlreadyExists() throws Exception {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName("johnny")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        doThrow(new PersonAlreadyExistsException("Person already exists")).when(personService).handlePersonCreationRequest(personCreateDto);

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Person already exists"));
    }

}