package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.fire.model.FireResident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FireMapper {
    Logger log = LoggerFactory.getLogger(FireMapper.class);
    FireMapper INSTANCE = Mappers.getMapper(FireMapper.class);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "age", source = "personAge")
    @Mapping(target = "medications", source = "medications")
    @Mapping(target = "allergies", source = "allergies")
    FireResident toFireResident(final String firstName, final String lastName, final String phone, final int personAge, final List<String> medications, final List<String> allergies);

    @Mapping(target = "stationNumber", source = "station")
    @Mapping(target = "residents", source = "residents")
    FireAddressResponse toFireAddressResponse(final String station, final List<FireResident> residents);
}

