package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.model.Person;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        PersonDto personCreateDto = PersonDto.builder()
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
        PersonDto personCreateDto = PersonDto.builder()
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
        PersonDto personCreateDto = PersonDto.builder()
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

    @Test
    @DisplayName("should update a person and update dataContainer")
    void shouldUpdatePersonAndUpdateDataContainer() throws Exception {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("10 rue des Lilas")
                .city("Paris")
                .zip("75005")
                .phone("0987654321")
                .email("jane@doe.com")
                .build();

        PersonDto personUpdateDto =
                PersonDto.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .city("Lyon")
                        .zip("69498")
                        .build();

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertFalse(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Jane") && person.getLastName().equals("Doe")));

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personCreateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Person create successfully : " + personCreateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(2, updatedData.getPersons().size());
        assertTrue(updatedData.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Jane") && person.getLastName().equals("Doe")));

        //when & then
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Person update successfully : " + personUpdateDto));
        DataContainer updatedData2 = dataManager.getDataContainer();
        assertEquals(2, updatedData2.getPersons().size());

        Person updatedPerson = updatedData2.getPersons().stream()
                .filter(person -> person.getFirstName().equals("Jane") && person.getLastName().equals("Doe"))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedPerson);
        assertEquals(personUpdateDto.getZip(), updatedPerson.getZip());
        assertEquals(personUpdateDto.getCity(), updatedPerson.getCity());
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null for updatePersonDto")
    void shouldReturn400WhenFirstNameIsNullForUpdatePersonDto() throws Exception {
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("firstName or lastName is blank"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getPersons().size());
        assertTrue(updatedData.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("Should return 404 when person does not exist")
    void shouldReturn404WhenPersonDoesNotExist() throws Exception {
        PersonDto personUpdateDto =
                PersonDto.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .city("Lyon")
                        .zip("69498")
                        .build();
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals("Johnny") && person.getLastName().equals("Boyd")));

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("the person with firstName: " + personUpdateDto.getFirstName()
                        + " and lastName: " + personUpdateDto.getLastName() + " does not exist"));
    }

    @Test
    @DisplayName("should delete a person and update dataContainer")
    void shouldDeletePersonAndUpdateDataContainer() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertTrue(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Person with firstName " + firstName
                        + " and lastName " + lastName + ", delete successfully."));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(0, updatedData.getPersons().size());
    }

    @Test
    @DisplayName("should return 404 when person does not exist for deletion")
    void shouldReturn404WhenPersonDoesNotExistForDeletion() throws Exception {
        //given
        final String firstName = "Jane";
        final String lastName = "Doe";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertFalse(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Person with firstName " + firstName + " and lastName " + lastName + " does not exist"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getPersons().size());
    }

    @Test
    @DisplayName("should return 400 when lastName or firstName is blank for deletion")
    void shouldReturn400WhenLastNameIsBlankForDeletion() throws Exception {
        //given
        final String firstName = "Jane";
        final String lastName = " ";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getPersons().size());
        assertFalse(initialDataContainer.getPersons().stream()
                .anyMatch(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("firstName or lastName is blank"));
    }
}