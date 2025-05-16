package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.flood.model.ResidentFloodInformation;
import com.lulippe.safetynetalerts.flood.model.ResidentsFloodInformations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResidentFloodInformationMapper {
    ResidentFloodInformationMapper INSTANCE = Mappers.getMapper(ResidentFloodInformationMapper.class);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "medications", source = "medications")
    @Mapping(target = "allergies", source = "allergies")
    ResidentFloodInformation toResidentFloodInformation(final String firstName, final String lastName, final String phone, final int age, final List<String> medications, final List<String> allergies);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "residents", source = "residentFloodInformationList")
    ResidentsFloodInformations toResidentsFloodInformations(String address, List<ResidentFloodInformation> residentFloodInformationList);

    @Mapping(target = "stationNumber", source = "stationNumber")
    @Mapping(target = "households", source = "residentsFloodInformationsList")
    PersonInformationByStationNumber toPersonInformationByStationNumber(final Integer stationNumber, final List<ResidentsFloodInformations> residentsFloodInformationsList);

}
