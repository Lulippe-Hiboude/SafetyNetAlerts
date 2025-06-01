package com.lulippe.safetynetalerts.integration;

import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.exception.json_exception.DataLoadingException;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DataManagerIT {
    @Autowired
    private DataManager dataManager;

    @Test
    @DisplayName("should load data successfully when file exist and is valid")
    void shouldLoadDataSuccessfullyWhenFileExistAndIsValid() {
        //when
        DataContainer dataContainer = dataManager.loadData();

        //then
        assertNotNull(dataContainer);
        assertEquals(2, dataContainer.getPersons().size());
        assertEquals("Johnny", dataContainer.getPersons().getFirst().getFirstName());
        assertEquals("Boyd", dataContainer.getPersons().getFirst().getLastName());
        assertEquals("1509 Culver St", dataContainer.getPersons().getFirst().getAddress());

        assertEquals(1, dataContainer.getFireStations().size());
        assertEquals("1509 Culver St", dataContainer.getFireStations().getFirst().getAddress());
        assertEquals("3", dataContainer.getFireStations().getFirst().getStation());

        assertEquals(1, dataContainer.getMedicalRecords().size());
        assertEquals("Johnny", dataContainer.getMedicalRecords().getFirst().getFirstName());
        assertEquals("Boyd", dataContainer.getMedicalRecords().getFirst().getLastName());
        assertEquals("03/06/1984", dataContainer.getMedicalRecords().getFirst().getBirthdate());
        assertEquals(2, dataContainer.getMedicalRecords().getFirst().getMedications().size());
        assertTrue(dataContainer.getMedicalRecords().getFirst().getMedications().contains("aznol:350mg"));
        assertTrue(dataContainer.getMedicalRecords().getFirst().getMedications().contains("hydrapermazol:100mg"));
        assertEquals(1, dataContainer.getMedicalRecords().getFirst().getAllergies().size());
        assertTrue(dataContainer.getMedicalRecords().getFirst().getAllergies().contains("nillacilan"));
    }

    @Test
    @DisplayName("Should throw a DataLoadingException if file does not exist")
    void shouldThrowDataLoadingExceptionIfFileDoesNotExist() throws Exception {
        //given
        Field filePathField = DataManager.class.getDeclaredField("resourceFilePath");
        filePathField.setAccessible(true);
        filePathField.set(dataManager, "json/inexistant.json");

        //when & then
        assertThatThrownBy(() -> dataManager.loadData())
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("Failed to load resource file: json/inexistant.json");
    }

    @Test
    @DisplayName("should initialize dataContainer when PostConstruct is called")
    void shouldInitializeDataContainerWhenPostConstructIsCalled() {
        //when
        dataManager.init();

        //then
        assertNotNull(dataManager);
        assertEquals(2, dataManager.getDataContainer().getPersons().size());
        assertEquals("Johnny", dataManager.getDataContainer().getPersons().getFirst().getFirstName());
        assertEquals("Boyd", dataManager.getDataContainer().getPersons().getFirst().getLastName());
        assertEquals("1509 Culver St", dataManager.getDataContainer().getPersons().getFirst().getAddress());

        assertEquals(1, dataManager.getDataContainer().getFireStations().size());
        assertEquals("1509 Culver St", dataManager.getDataContainer().getFireStations().getFirst().getAddress());
        assertEquals("3", dataManager.getDataContainer().getFireStations().getFirst().getStation());

        assertEquals(1, dataManager.getDataContainer().getMedicalRecords().size());
        assertEquals("Johnny", dataManager.getDataContainer().getMedicalRecords().getFirst().getFirstName());
        assertEquals("Boyd", dataManager.getDataContainer().getMedicalRecords().getFirst().getLastName());
        assertEquals("03/06/1984", dataManager.getDataContainer().getMedicalRecords().getFirst().getBirthdate());
        assertEquals(2, dataManager.getDataContainer().getMedicalRecords().getFirst().getMedications().size());
        assertTrue(dataManager.getDataContainer().getMedicalRecords().getFirst().getMedications().contains("aznol:350mg"));
        assertTrue(dataManager.getDataContainer().getMedicalRecords().getFirst().getMedications().contains("hydrapermazol:100mg"));
        assertEquals(1, dataManager.getDataContainer().getMedicalRecords().getFirst().getAllergies().size());
        assertTrue(dataManager.getDataContainer().getMedicalRecords().getFirst().getAllergies().contains("nillacilan"));
    }
}