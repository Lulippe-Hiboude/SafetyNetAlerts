package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.childAlert.model.ChildInformation;
import com.lulippe.safetynetalerts.childAlert.model.HouseholdMember;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.service.ChildAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChildAlertController.class)
@AutoConfigureMockMvc
class ChildAlertControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChildAlertService childAlertService;

    @Test
    @DisplayName("should Return ChildAlertResponse")
    void shouldReturnChildAlertResponse() throws Exception {
        final String address = "123 main street";
        final HouseholdMember householdMember = new HouseholdMember();
        householdMember.setFirstName("Alan");
        householdMember.setLastName("Davis");
        final List<HouseholdMember> householdMembers = Collections.singletonList(householdMember);
        final ChildInformation childInformation = new ChildInformation();
        childInformation.setFirstName("John");
        childInformation.setLastName("Smith");
        childInformation.setAge(7);
        childInformation.setHouseholdMembers(householdMembers);
        final List<ChildInformation> childInformationList = Collections.singletonList(childInformation);
        final ChildAlertResponse childAlertResponse = new ChildAlertResponse();
        childAlertResponse.setChildren(childInformationList);
        given(childAlertService.getChildAlertResponse(address)).willReturn(childAlertResponse);

        //when & then

        mockMvc.perform(get("/childAlert")
                .param("address", address))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children",hasSize(1)))
                .andExpect(jsonPath("$.children[0]").value(childInformation));
    }

    @Test
    @DisplayName("should throw InvalidDataException when address is empty")
    void shouldThrowInvalidDataExceptionWhenAddressIsEmpty() throws Exception {
        //given
        final String address = "";
        given(childAlertService.getChildAlertResponse(address)).willThrow(new InvalidDataException("address is empty"));

        //when & then
        mockMvc.perform(get("/childAlert")
                .param("address", address))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should throw InexistantObjectException when no person found by address")
    void shouldThrowInexistantObjectExceptionWhenNoPersonFoundByAddress() throws Exception {
        //given
        final String address = "123 main street";
        given(childAlertService.getChildAlertResponse(address)).willThrow(new InexistantObjectException("no person found by address"));

        //when & then
        mockMvc.perform(get("/childAlert")
                .param("address", address))
                .andExpect(status().isNotFound());
    }
}