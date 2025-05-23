package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.mapper.CommunityEmailMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateData;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class CommunityEmailService {
    private static final String CITY = "city";
    private final PersonService personService;

    public CommunityEmailList getCommunityEmailList(final String city) {
        validateData(city, CITY);
        final Set<String> mailList = personService.getEmailListByCity(city);
        return CommunityEmailMapper.INSTANCE.toCommunityEmailList(mailList);
    }
}
