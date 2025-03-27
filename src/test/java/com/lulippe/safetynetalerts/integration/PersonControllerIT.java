package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.dto.PersonCreateDto;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PersonControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataManager dataManager;

    @AfterEach
    void resetDataContainer() {
        dataManager.init();
    }

    @Test
    @DisplayName("should create a new person and update the dataContainer")
    void shouldCreateNewPersonAndUpdateDataContainer() throws Exception {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("10 rue des Lilas")
                .city("Paris")
                .zip("75005")
                .phone("0987654321")
                .email("jane@doe.com")
                .build();

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertFalse(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Jane") && person.getLastName().equals("Doe")));

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Person create successfully : " + personCreateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(2, updatedData.getPersons().size());
        assertTrue(updatedData.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Jane") && person.getLastName().equals("Doe")));
    }

    @Test
    @DisplayName("should return 409 when person already exists")
    void shouldReturnConflictWhenPersonAlreadyExists() throws Exception {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName("Johnny")
                .lastName("Boyd")
                .address("1509 Culver St")
                .city("Culver")
                .zip("97451")
                .phone("841-874-6512")
                .email("jaboyd@email.com")
                .build();
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Person already exists"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getPersons().size());
        assertTrue(updatedData.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null")
    void shouldReturn400WhenFirstNameIsNull() throws Exception {
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("firstName or lastName is blank"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getPersons().size());
        assertTrue(updatedData.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("should return 400 when personCreateDto is null")
    void shouldReturnBadRequestWhenPersonCreateDtoHasNullValues() throws Exception {
        //given
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }


}