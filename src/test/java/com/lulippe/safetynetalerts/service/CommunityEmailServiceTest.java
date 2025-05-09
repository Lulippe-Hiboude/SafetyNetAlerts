package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.communityEmail.model.EmailEntry;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class CommunityEmailServiceTest {
    @InjectMocks
    private CommunityEmailService communityEmailService;

    @Mock
    private PersonService personService;

    @Test
    @DisplayName("should return a CommunityEmailList")
    void shouldReturnACommunityEmailList() {
        //given
        final String city = "city";
        final String email1 = "john@doe.com";
        final String email2 = "j@doe.com";
        final String email3 = "";
        final Set<String> emailList = new HashSet<>(Arrays.asList(email1, email2, email3));

        given(personService.getEmailListByCity(city)).willReturn(emailList);

        //when
        final CommunityEmailList expectedResult = communityEmailService.getCommunityEmailList(city);

        //then
        assertEquals(3, expectedResult.getEmails().size());
        List<String> actualEmails = expectedResult.getEmails().stream()
                .map(EmailEntry::getEmail)
                .toList();
        assertThat(actualEmails, containsInAnyOrder(email1, email2, email3));
    }

    @Test
    @DisplayName("should throw InvalidDataException if city is empty")
    void shouldThrowInvalidDataExceptionIfCityIsEmpty() {
        final String city = "";

        //when & then
        assertThrows(InvalidDataException.class, () -> communityEmailService.getCommunityEmailList(city));
    }
}