package com.lulippe.safetynetalerts.mapper;


import com.lulippe.safetynetalerts.fireStation.model.FireStationCoverageResponse;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.fireStation.model.ResidentInfo;
import com.lulippe.safetynetalerts.model.FireStation;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FireStationMapper {
    Logger log = LoggerFactory.getLogger(FireStationMapper.class);
    FireStationMapper INSTANCE = Mappers.getMapper(FireStationMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "address", source = "fireStationDto.address")
    @Mapping(target = "station", source = "fireStationDto.station")
    FireStation toFireStation(final FireStationDto fireStationDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "address", source = "fireStationDto.address")
    @Mapping(target = "station", source = "fireStationDto.station")
    void toFireStation(final FireStationDto fireStationDto, @MappingTarget final FireStation existingFireStation);

    default FireStationCoverageResponse toFireStationCoverageResponse(final List<ResidentInfo> residentInfoList, Map<String,Integer> countMap) {
        final FireStationCoverageResponse fireStationCoverageResponse = new FireStationCoverageResponse();
        fireStationCoverageResponse.setResidents(residentInfoList);
        fireStationCoverageResponse.setAdultCount(countMap.getOrDefault("adults", 0));
        fireStationCoverageResponse.setChildCount(countMap.getOrDefault("children", 0));
        return fireStationCoverageResponse;
    }
}
