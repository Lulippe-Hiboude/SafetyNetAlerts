package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResidentInformationMapper {
    ResidentInformationMapper INSTANCE = Mappers.getMapper(ResidentInformationMapper.class);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "medications", source = "medications")
    @Mapping(target = "allergies", source = "allergies")
    ResidentInformation toResidentInformation(final String firstName, final String address, final int age, final String email, final List<String> medications, final List<String> allergies);
}
