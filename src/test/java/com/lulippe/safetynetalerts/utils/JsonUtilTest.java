package com.lulippe.safetynetalerts.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lulippe.safetynetalerts.dto.DataContainer;
import com.lulippe.safetynetalerts.exception.DataLoadingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JsonUtilTest {

    @Test
    @DisplayName("should convert Json to Object")
    void shouldConvertJsonToObject() throws IOException {
        //given
        String json = new String(Files.readAllBytes(Paths.get("src/test/java/resources/json/testData.json")));
        TypeReference<DataContainer> typeReference = new TypeReference<>() {
        };

        //when
        DataContainer dataContainer = JsonUtil.fromJson(json, typeReference);

        //then
        assertNotNull(dataContainer);
        assertEquals(1, dataContainer.getPersons().size());
        assertEquals("John", dataContainer.getPersons().getFirst().getFirstName());
        assertEquals(1, dataContainer.getFireStations().size());
        assertEquals("1509 Culver St", dataContainer.getFireStations().getFirst().getAddress());
        assertEquals(1, dataContainer.getMedicalRecords().size());
        assertEquals("03/06/1984", dataContainer.getMedicalRecords().getFirst().getBirthdate());
        assertEquals("aznol:350mg", dataContainer.getMedicalRecords().getFirst().getMedications().getFirst());
    }

    @Test
    @DisplayName("should throw an error if the json file is malformed")
    void shouldThrowAnErrorIfJsonFileIsMalformed() throws IOException {
        //given
        String malformedJson = new String(Files.readAllBytes(Paths.get("src/test/java/resources/json/malformedData.json")));
        TypeReference<DataContainer> typeReference = new TypeReference<>() {
        };

        //when & then
        assertThatThrownBy(() -> JsonUtil.fromJson(malformedJson, typeReference))
                .isInstanceOf(DataLoadingException.class)
                .hasMessageContaining("The file is malformed");
    }
}