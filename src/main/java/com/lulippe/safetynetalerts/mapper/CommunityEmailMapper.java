package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.communityEmail.model.EmailEntry;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommunityEmailMapper {
    Logger log = LoggerFactory.getLogger(CommunityEmailMapper.class);
    CommunityEmailMapper INSTANCE = Mappers.getMapper(CommunityEmailMapper.class);

    default CommunityEmailList toCommunityEmailList(final Set<String> emails) {
        final CommunityEmailList communityEmailList = new CommunityEmailList();
        List<EmailEntry> emailEntries = emails.stream()
                .map(email -> new EmailEntry().email(email))
                .toList();
        communityEmailList.setEmails(emailEntries);
        return communityEmailList;
    }
}
