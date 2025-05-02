package com.lulippe.safetynetalerts.mapper;


import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.model.FireStation;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
