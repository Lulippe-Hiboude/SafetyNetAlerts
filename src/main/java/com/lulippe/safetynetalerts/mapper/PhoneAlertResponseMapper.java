package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhoneAlertResponseMapper {
    PhoneAlertResponseMapper INSTANCE = Mappers.getMapper(PhoneAlertResponseMapper.class);

    @Mapping(target = "stationNumber", source = "stationNumber")
    @Mapping(target = "phoneNumbers", source = "personsPhone")
    PhoneAlertResponse toPhoneAlertResponse(final int stationNumber, final List<String> personsPhone);
}
